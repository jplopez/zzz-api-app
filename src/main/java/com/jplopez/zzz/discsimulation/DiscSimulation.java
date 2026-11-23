package com.jplopez.zzz.discsimulation;

import static com.jplopez.zzz.discsimulation.DiscDocuments.MAX_DRAW_COUNT;
import static com.jplopez.zzz.discsimulation.DiscDocuments.SCHEMA_VERSION;
import static com.jplopez.zzz.discsimulation.DiscProblem.invalid;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jplopez.zzz.discsimulation.DiscDocuments.Action;
import com.jplopez.zzz.discsimulation.DiscDocuments.Actions;
import com.jplopez.zzz.discsimulation.DiscDocuments.ComparisonRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.ComparisonResponse;
import com.jplopez.zzz.discsimulation.DiscDocuments.ComparisonResult;
import com.jplopez.zzz.discsimulation.DiscDocuments.CreateRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.DiscState;
import com.jplopez.zzz.discsimulation.DiscDocuments.Event;
import com.jplopez.zzz.discsimulation.DiscDocuments.ForecastRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.ForecastResponse;
import com.jplopez.zzz.discsimulation.DiscDocuments.ForkOrigin;
import com.jplopez.zzz.discsimulation.DiscDocuments.Meta;
import com.jplopez.zzz.discsimulation.DiscDocuments.RewindRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.Rng;
import com.jplopez.zzz.discsimulation.DiscDocuments.Session;
import com.jplopez.zzz.discsimulation.DiscDocuments.SessionResponse;
import com.jplopez.zzz.discsimulation.DiscDocuments.StepRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.StepResponse;
import com.jplopez.zzz.discsimulation.DiscDocuments.StepResult;
import com.jplopez.zzz.discsimulation.DiscForecast.Fraction;
import com.jplopez.zzz.discsimulation.DiscRuleCatalog.RarityRules;
import com.jplopez.zzz.discsimulation.DiscRuleCatalog.RuleSet;

/** Pure transitions over the supplied disc, with randomness only when creation omits its seed. */
@Service
final class DiscSimulation {

  static final String SESSION_PATH = "/v1/disc-sessions";
  private static final Comparator<ScoredDisc> BY_RANK = Comparator
      .comparing(ScoredDisc::expected, Comparator.reverseOrder())
      .thenComparing(ScoredDisc::current, Comparator.reverseOrder())
      .thenComparingInt(ScoredDisc::index);

  private record SessionRules(RuleSet snapshot, RarityRules rarity) {}

  private record ScoredDisc(int index, int slot, String version, BigDecimal current, Fraction expected) {}

  private final DiscRuleCatalog catalog;
  private final SecureRandom seeds = new SecureRandom();

  DiscSimulation(DiscRuleCatalog catalog) {
    this.catalog = catalog;
  }

  SessionResponse create(CreateRequest request) {
    RuleSet rules = catalog.entry(request.rulesVersion(), "/rules_version").rules();
    DiscState initial = request.initial();
    RarityRules rarity = validateDisc(initial, rules, "/initial");
    long seed = request.seed() == null ? Integer.toUnsignedLong(seeds.nextInt()) : request.seed();
    Meta meta = request.label() == null ? null : new Meta(request.label(), null, null);
    Session session = new Session(null, SCHEMA_VERSION, rules.rulesVersion(), initial, initial, List.of(),
        new Rng(Pcg32.ALGORITHM, seed, 0), meta);
    return sessionResponse(session, rarity);
  }

  StepResponse step(StepRequest request) {
    Session session = request.session();
    RarityRules rules = validateSession(session, "/session").rarity();
    DiscState disc = session.disc();
    int target = request.toLevel();
    if (target <= disc.level() || target > rules.levelCap()) {
      throw invalid("invalid-level", "/to_level", "Target level must be above the current level and within the cap");
    }
    if (rules.rollsBetween(disc.level(), target) > 1) {
      throw invalid("multiple-rolls", "/to_level", "A step cannot cross more than one roll level");
    }
    if (request.observe() != null && !rules.rollsAt(target)) {
      throw invalid("no-roll-to-observe", "/observe", "An observed roll must target a roll level");
    }

    var history = new ArrayList<>(session.history());
    int originalSize = history.size();
    long draws = session.rng().draws();
    for (int level = disc.level() + 1; level <= target; level++) {
      int n = history.size() + 1;
      if (!rules.rollsAt(level)) {
        history.add(Event.level(n, level));
        disc = disc.atLevel(level, disc.substats());
        continue;
      }
      var choices = DiscRolls.choices(rules, disc.mainStat(), disc.substats());
      String stat;
      Long draw = null;
      if (request.observe() == null) {
        if (draws == MAX_DRAW_COUNT) {
          throw invalid("rng-exhausted", "/session/rng/draws", "The draw counter is full; fork to start a new sequence");
        }
        draw = draws++;
        stat = DiscRolls.select(choices, Pcg32.draw(session.rng().seed(), draw));
      } else {
        stat = request.observe().stat();
        checkChoice(choices, stat, "/observe/stat");
      }
      history.add(Event.roll(n, level, DiscRolls.effect(rules, disc.substats()), stat, draw));
      disc = disc.atLevel(level, DiscRolls.apply(disc.substats(), stat));
    }
    Session stepped = session.withState(disc, history, new Rng(Pcg32.ALGORITHM, session.rng().seed(), draws),
        session.meta());
    StepResult result = new StepResult(List.copyOf(history.subList(originalSize, history.size())),
        DiscForecast.summary(disc, rules, request.weights()));
    return new StepResponse(stepped, result, actions(stepped, rules), disc.level() == rules.levelCap());
  }

  SessionResponse rewind(RewindRequest request) {
    Session session = request.session();
    RarityRules rules = validateSession(session, "/session").rarity();
    int retained = request.toEvent();
    if (retained > session.history().size()) {
      throw invalid("invalid-event", "/to_event", "Cannot retain more events than the session contains");
    }
    if (!request.fork() && request.seed() != null) {
      throw invalid("unexpected-seed", "/seed", "A seed can only be supplied for a fork");
    }
    List<Event> history = session.history().subList(0, retained);
    DiscState disc = replay(session, history, rules, "/session");
    Rng rng = session.rng();
    Meta meta = session.meta();
    if (request.fork()) {
      long seed = request.seed() == null ? Pcg32.forkSeed(rng.seed(), rng.draws(), retained) : request.seed();
      if (seed == rng.seed()) {
        throw invalid("same-seed", "/seed", "A fork requires a seed different from its parent's seed");
      }
      meta = new Meta(meta == null ? null : meta.label(), meta == null ? null : meta.createdAt(),
          new ForkOrigin(rng.seed(), retained));
      rng = new Rng(Pcg32.ALGORITHM, seed, 0);
    }
    return sessionResponse(session.withState(disc, history, rng, meta), rules);
  }

  ForecastResponse forecast(ForecastRequest request, JsonNode originalSession) {
    Session session = request.session();
    SessionRules sessionRules = validateSession(session, "/session");
    RarityRules rules = sessionRules.rarity();
    DiscState disc = session.disc();
    int target = request.targetLevel() == null ? rules.levelCap() : request.targetLevel();
    if (target < disc.level() || target > rules.levelCap()) {
      throw invalid("invalid-level", "/target_level", "Forecast target cannot precede the current level or exceed the cap");
    }
    var thresholds = request.thresholds() == null ? List.<BigDecimal>of() : request.thresholds();
    if (!thresholds.isEmpty() && request.weights() == null) {
      throw invalid("weights-required", "/thresholds", "Score thresholds require a weight profile");
    }
    var result = DiscForecast.compute(disc, rules, target, sessionRules.snapshot().probabilityModel(), request.weights(),
        thresholds);
    return new ForecastResponse(originalSession, result, actions(session, rules), disc.level() == rules.levelCap());
  }

  ComparisonResponse compare(ComparisonRequest request) {
    RuleSet defaultRules = catalog.entry(request.rulesVersion(), "/rules_version").rules();
    List<ScoredDisc> scores = new ArrayList<>();
    for (int i = 0; i < request.discs().size(); i++) {
      JsonNode input = request.discs().get(i);
      String pointer = "/discs/" + i;
      String version = defaultRules.rulesVersion();
      DiscState disc;
      RarityRules rules;
      if (input.has("schema_version")) {
        Session session = DiscJson.bind(input, Session.class);
        rules = validateSession(session, pointer).rarity();
        version = session.rulesVersion();
        disc = session.disc();
      } else {
        disc = DiscJson.bind(input, DiscState.class);
        rules = validateDisc(disc, defaultRules, pointer);
      }
      var current = DiscForecast.score(disc, rules, request.weights());
      var expected = DiscForecast.expectedScoreAtCap(disc, rules, request.weights());
      scores.add(new ScoredDisc(i, disc.slot(), version, current, expected));
    }
    List<ScoredDisc> ordered = scores.stream().sorted(BY_RANK).toList();
    int[] ranks = new int[scores.size()];
    for (int i = 0; i < ordered.size(); i++) {
      ranks[ordered.get(i).index()] = i + 1;
    }
    var results = scores.stream().map(score -> new ComparisonResult(score.index(), score.slot(), score.version(),
        score.current(), score.expected().decimal(), ranks[score.index()])).toList();
    boolean mixedSlots = results.stream().map(ComparisonResult::slot).distinct().count() > 1;
    return new ComparisonResponse(results, mixedSlots ? List.of("mixed-slots") : List.of());
  }

  private SessionRules validateSession(Session session, String pointer) {
    RuleSet ruleSet = catalog.entry(session.rulesVersion(), pointer + "/rules_version").rules();
    RarityRules rules = validateDisc(session.initial(), ruleSet, pointer + "/initial");
    ForkOrigin origin = forkOrigin(session);
    if (origin != null) {
      if (origin.atEvent() > rules.levelCap() - session.initial().level()) {
        throw invalid("invalid-fork-origin", pointer + "/meta/forked_from/at_event", "The fork point exceeds the cap");
      }
      if (origin.seed() == session.rng().seed()) {
        throw invalid("invalid-fork-origin", pointer + "/meta/forked_from/seed",
            "The parent seed must differ from the fork seed");
      }
    }
    DiscState replayed = replay(session, session.history(), rules, pointer);
    if (!replayed.equals(session.disc())) {
      throw invalid("replay-mismatch", pointer + "/disc", "The disc does not match the replay of initial and history");
    }
    return new SessionRules(ruleSet, rules);
  }

  private static RarityRules validateDisc(DiscState disc, RuleSet ruleSet, String pointer) {
    RarityRules rules = ruleSet.rarities().get(disc.rarity());
    if (rules == null) {
      throw invalid("invalid-rarity", pointer + "/rarity", "The rarity is not supported by these rules");
    }
    if (disc.level() > rules.levelCap()) {
      throw invalid("invalid-level", pointer + "/level", "Disc level exceeds its rarity cap");
    }
    if (!rules.mainStats().get(disc.slot()).containsKey(disc.mainStat())) {
      throw invalid("illegal-main-stat", pointer + "/main_stat", "The main stat is not allowed in this slot");
    }
    var seen = new HashSet<String>();
    int totalRolls = 0;
    for (int i = 0; i < disc.substats().size(); i++) {
      var substat = disc.substats().get(i);
      String path = pointer + "/substats/" + i;
      if (!rules.substats().containsKey(substat.stat()) || substat.stat().equals(disc.mainStat())
          || !seen.add(substat.stat())) {
        throw invalid("illegal-substat", path + "/stat", "Substats must be eligible, distinct, and differ from the main stat");
      }
      if (substat.rolls() > rules.rollCap()) {
        throw invalid("roll-cap-exceeded", path + "/rolls", "Substat rolls exceed the rarity cap");
      }
      totalRolls += substat.rolls();
    }
    int startingCount = totalRolls - rules.rollsBetween(0, disc.level());
    if (!rules.startingSubstatCounts().contains(startingCount)
        || disc.substats().size() != Math.min(rules.maxSubstats(), totalRolls)) {
      throw invalid("unreachable-disc", pointer + "/substats", "These substat counts cannot be reached at this level");
    }
    return rules;
  }

  private static DiscState replay(Session session, List<Event> history, RarityRules rules, String pointer) {
    DiscState disc = session.initial();
    ForkOrigin origin = forkOrigin(session);
    int forkPoint = origin == null ? 0 : origin.atEvent();
    long lastDraw = -1;
    for (int i = 0; i < history.size(); i++) {
      Event event = history.get(i);
      String path = pointer + "/history/" + i;
      int level = disc.level() + 1;
      if (event.n() != i + 1 || event.level() != level || level > rules.levelCap()) {
        throw invalid("invalid-history", path, "Events must be consecutive from the initial level and not exceed the cap");
      }
      boolean rolls = rules.rollsAt(level);
      if (rolls != "roll".equals(event.kind())) {
        throw invalid("invalid-history", path + "/kind", "The event kind must match the rules for that level");
      }
      if (!rolls) {
        disc = disc.atLevel(level, disc.substats());
        continue;
      }
      checkChoice(DiscRolls.choices(rules, disc.mainStat(), disc.substats()), event.stat(), path + "/stat");
      if (!DiscRolls.effect(rules, disc.substats()).equals(event.effect())) {
        throw invalid("invalid-history", path + "/effect", "The recorded roll effect does not match the disc state");
      }
      // The retained prefix can include several seed sequences. Only indices after
      // the recorded fork point are known to belong to the current sequence.
      if (event.draw() != null && i >= forkPoint) {
        if (event.draw() <= lastDraw || event.draw() >= session.rng().draws()) {
          throw invalid("invalid-draw", path + "/draw", "Draw indices must increase and precede the draw counter");
        }
        lastDraw = event.draw();
      }
      disc = disc.atLevel(level, DiscRolls.apply(disc.substats(), event.stat()));
    }
    return disc;
  }

  private static ForkOrigin forkOrigin(Session session) {
    return session.meta() == null ? null : session.meta().forkedFrom();
  }

  private static void checkChoice(List<DiscRolls.Choice> choices, String stat, String pointer) {
    if (choices.stream().noneMatch(choice -> choice.stat().equals(stat))) {
      throw invalid("illegal-roll", pointer, "The selected substat cannot be added or upgraded at this level");
    }
  }

  private static SessionResponse sessionResponse(Session session, RarityRules rules) {
    return new SessionResponse(session, actions(session, rules), session.disc().level() == rules.levelCap());
  }

  private static Actions actions(Session session, RarityRules rules) {
    int level = session.disc().level();
    Action nextLevel = level < rules.levelCap()
        ? action("step", Map.of("to_level", level + 1), rules.rollsAt(level + 1), null) : null;
    Action nextRoll = rules.upgradeLevels().stream().filter(next -> next > level).findFirst()
        .map(next -> action("step", Map.of("to_level", next), true, next - level - 1)).orElse(null);
    int size = session.history().size();
    Action undo = size == 0 ? null : action("rewind", Map.of("to_event", size - 1, "fork", false), null, null);
    Action fork = size == 0 ? null : action("rewind", Map.of("to_event", size, "fork", true), null, null);
    return new Actions(nextLevel, nextRoll, action("forecast", Map.of(), null, null), undo, fork);
  }

  private static Action action(String operation, Map<String, Object> body, Boolean rolls, Integer skipped) {
    // Stable key order for responses that repeat byte for byte on another JVM.
    return new Action("POST", SESSION_PATH + "/" + operation, List.of("session"), new TreeMap<>(body),
        rolls, skipped);
  }
}
