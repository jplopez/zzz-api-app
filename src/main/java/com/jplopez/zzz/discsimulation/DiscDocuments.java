package com.jplopez.zzz.discsimulation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/** Wire documents for the v1 simulation API. The dedicated mapper uses snake_case. */
final class DiscDocuments {

  static final int SCHEMA_VERSION = 1;
  static final long MAX_SEED = 0xffff_ffffL;
  static final long MAX_DRAW_COUNT = (1L << 53) - 1;

  private DiscDocuments() {}

  record Substat(String stat, int rolls) {}

  record DiscState(String rarity, int slot, String mainStat, int level, List<Substat> substats, JsonNode setId) {
    DiscState atLevel(int newLevel, List<Substat> newSubstats) {
      return new DiscState(rarity, slot, mainStat, newLevel, List.copyOf(newSubstats), setId);
    }
  }

  record Event(int n, int level, String kind, String source, String effect, String stat, Long draw) {
    static Event level(int n, int level) {
      return new Event(n, level, "level", null, null, null, null);
    }

    static Event roll(int n, int level, String effect, String stat, Long draw) {
      return new Event(n, level, "roll", draw == null ? "observed" : "simulated", effect, stat, draw);
    }
  }

  record Rng(String algorithm, long seed, long draws) {}

  record ForkOrigin(long seed, int atEvent) {}

  record Meta(String label, String createdAt, ForkOrigin forkedFrom) {}

  record Session(@JsonProperty("$schema") String schema, int schemaVersion, String rulesVersion, DiscState initial,
                 DiscState disc, List<Event> history, Rng rng, Meta meta) {
    Session withState(DiscState newDisc, List<Event> newHistory, Rng newRng, Meta newMeta) {
      return new Session(schema, schemaVersion, rulesVersion, initial, newDisc, List.copyOf(newHistory), newRng, newMeta);
    }
  }

  record CreateRequest(DiscState initial, Long seed, String label, String rulesVersion) {}

  record Observation(String stat) {}

  record StepRequest(Session session, int toLevel, Observation observe, Map<String, BigDecimal> weights) {}

  record RewindRequest(Session session, int toEvent, boolean fork, Long seed) {}

  record ForecastRequest(Session session, Map<String, BigDecimal> weights, Integer targetLevel,
                         List<BigDecimal> thresholds) {}

  record ComparisonRequest(List<JsonNode> discs, Map<String, BigDecimal> weights, String rulesVersion) {}

  record Action(String method, String href, List<String> send, Map<String, Object> body, Boolean rolls,
                Integer levelsSkipped) {}

  record Actions(Action nextLevel, Action nextRoll, Action forecast, Action undo, Action fork) {}

  record SubstatValue(String stat, int rolls, BigDecimal value) {}

  record DiscSummary(int level, String mainStat, BigDecimal mainValue, List<SubstatValue> substats, BigDecimal score) {}

  record SessionResponse(Session session, Actions actions, boolean complete) {}

  record StepResult(List<Event> events, DiscSummary disc) {}

  record StepResponse(Session session, StepResult result, Actions actions, boolean complete) {}

  record Outcome(BigDecimal probability, List<Substat> substats, BigDecimal score) {}

  record Threshold(BigDecimal score, BigDecimal probability) {}

  record ForecastResult(String probabilityModel, int upgradesRemaining, List<Outcome> outcomes,
                        BigDecimal expectedScore, List<Threshold> pAtLeast) {}

  record ForecastResponse(JsonNode session, ForecastResult result, Actions actions, boolean complete) {}

  record ComparisonResult(int index, int slot, String rulesVersion, BigDecimal currentScore,
                          BigDecimal expectedFinalScore, int rank) {}

  record ComparisonResponse(List<ComparisonResult> results, List<String> warnings) {}
}
