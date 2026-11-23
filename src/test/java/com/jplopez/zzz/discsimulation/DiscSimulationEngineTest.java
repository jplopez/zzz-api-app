package com.jplopez.zzz.discsimulation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import com.jplopez.zzz.discsimulation.DiscDocuments.CreateRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.DiscState;
import com.jplopez.zzz.discsimulation.DiscDocuments.Session;
import com.jplopez.zzz.discsimulation.DiscDocuments.StepRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.Substat;
import com.jplopez.zzz.discsimulation.DiscRuleCatalog.RarityRules;

class DiscSimulationEngineTest {

  @Test
  void pcg32MatchesTheReferenceSequenceAndJumpsToLargeDrawIndices() {
    long[] expected = { 0xa15c02b7L, 0x7b47f409L, 0xba1d3330L, 0x83d2f293L, 0xbfa4784bL, 0xcbed606eL };
    for (int i = 0; i < expected.length; i++) {
      assertThat(Pcg32.draw(42, i)).isEqualTo(expected[i]);
    }
    assertThat(Pcg32.draw(42, DiscDocuments.MAX_DRAW_COUNT - 1)).isBetween(0L, DiscDocuments.MAX_SEED);
  }

  @Test
  void arbitraryLegalSessionsReplayAndConserveRollCounts() throws Exception {
    var catalog = new DiscRuleCatalog();
    var simulation = new DiscSimulation(catalog);
    var random = new SplittableRandom(15591);
    for (String version : List.of("2.4", "2.5")) {
      for (String rank : List.of("B", "A", "S")) {
        var rules = catalog.entry(version, null).rules().rarities().get(rank);
        for (int trial = 0; trial < 20; trial++) {
          int slot = random.nextInt(1, 7);
          var mainStats = rules.mainStats().get(slot).keySet().stream().sorted().toList();
          String main = mainStats.get(random.nextInt(mainStats.size()));
          int starting = rules.startingSubstatCounts().get(trial % rules.startingSubstatCounts().size());
          var pool = new ArrayList<>(rules.substats().keySet().stream().sorted().filter(stat -> !stat.equals(main)).toList());
          Collections.shuffle(pool, new Random(random.nextLong()));
          var substats = pool.subList(0, starting).stream().map(stat -> new Substat(stat, 1)).toList();
          var initial = new DiscState(rank, slot, main, 0, substats, null);
          Session session = simulation.create(new CreateRequest(initial, random.nextLong(1L << 32), null, version)).session();
          while (session.disc().level() < rules.levelCap()) {
            int level = session.disc().level();
            int nextRoll = rules.upgradeLevels().stream().filter(next -> next > level).findFirst().orElseThrow();
            int next = random.nextInt(level + 1, nextRoll + 1);
            session = simulation.step(new StepRequest(session, next, null, null)).session();
            assertReplay(session, rules);
          }
        }
      }
    }
  }

  @Test
  void exactForecastMatchesOneMillionIndependentSimulatedPaths() throws Exception {
    var rules = new DiscRuleCatalog().entry("2.5", null).rules().rarities().get("S");
    List<String> initialStats = List.of("atk_pct", "crit_dmg", "hp");
    List<String> eligible = List.of("anomaly_proficiency", "atk", "def", "def_pct", "hp_pct", "pen");
    var disc = new DiscState("S", 4, "crit_rate", 0, initialStats.stream().map(stat -> new Substat(stat, 1)).toList(), null);
    var forecast = DiscForecast.compute(disc, rules, 15, "uniform", null, List.of());
    assertThat(forecast.upgradesRemaining()).isEqualTo(5);
    assertThat(forecast.outcomes()).hasSize(6 * 35); // one addition, 35 distributions of four rolls among four stats
    assertThat(forecast.outcomes().stream().mapToDouble(outcome -> outcome.probability().doubleValue()).sum())
        .isCloseTo(1, within(1e-9));

    int trials = 1_000_000;
    int[] histogram = new int[eligible.size() * 625];
    int[] digits = {1, 5, 25, 125};
    var random = new SplittableRandom(1295673);
    for (int trial = 0; trial < trials; trial++) {
      int key = random.nextInt(eligible.size()) * 625;
      for (int i = 0; i < 4; i++) {
        key += digits[random.nextInt(4)];
      }
      histogram[key]++;
    }
    int covered = 0;
    for (var outcome : forecast.outcomes()) {
      int group = -1;
      int key = 0;
      for (Substat stat : outcome.substats()) {
        int initialIndex = initialStats.indexOf(stat.stat());
        if (initialIndex >= 0) {
          key += (stat.rolls() - 1) * digits[initialIndex];
        } else {
          group = eligible.indexOf(stat.stat());
          key += (stat.rolls() - 1) * digits[3];
        }
      }
      int observed = histogram[group * 625 + key];
      covered += observed;
      double p = outcome.probability().doubleValue();
      assertThat((double) observed / trials).isCloseTo(p, within(6 * Math.sqrt(p * (1 - p) / trials)));
    }
    assertThat(covered).isEqualTo(trials);
  }

  private static void assertReplay(Session session, RarityRules rules) {
    Map<String, Integer> replay = new LinkedHashMap<>();
    session.initial().substats().forEach(stat -> replay.put(stat.stat(), stat.rolls()));
    long draws = 0;
    int level = session.initial().level();
    for (var event : session.history()) {
      assertThat(event.level()).isEqualTo(++level);
      if (rules.upgradeLevels().contains(level)) {
        assertThat(event.draw()).isEqualTo(draws++);
        assertThat(event.stat()).isNotEqualTo(session.initial().mainStat());
        boolean added = replay.size() < rules.maxSubstats();
        assertThat(event.effect()).isEqualTo(added ? "added" : "upgraded");
        assertThat(replay.containsKey(event.stat())).isNotEqualTo(added);
        replay.merge(event.stat(), 1, Integer::sum);
      } else {
        assertThat(event.kind()).isEqualTo("level");
      }
    }
    var expected = replay.entrySet().stream().map(entry -> new Substat(entry.getKey(), entry.getValue())).toList();
    assertThat(session.disc().level()).isEqualTo(level);
    assertThat(session.disc().substats()).isEqualTo(expected).allSatisfy(stat -> assertThat(stat.rolls()).isLessThanOrEqualTo(rules.rollCap()));
    assertThat(session.rng().draws()).isEqualTo(draws);
  }
}
