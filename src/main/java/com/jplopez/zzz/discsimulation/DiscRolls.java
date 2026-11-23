package com.jplopez.zzz.discsimulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jplopez.zzz.discsimulation.DiscDocuments.Substat;
import com.jplopez.zzz.discsimulation.DiscRuleCatalog.RarityRules;

/** One transition table shared by simulated steps, observations, replay, and forecasts. */
final class DiscRolls {

  record Choice(String stat, int weight) {}

  private DiscRolls() {}

  static List<Choice> choices(RarityRules rules, String mainStat, List<Substat> substats) {
    if (substats.size() < rules.maxSubstats()) {
      return rules.substats().keySet().stream().sorted()
          .filter(stat -> !stat.equals(mainStat) && substats.stream().noneMatch(existing -> existing.stat().equals(stat)))
          .map(stat -> new Choice(stat, rules.substats().get(stat).addWeight())).toList();
    }
    return substats.stream().filter(stat -> stat.rolls() < rules.rollCap()).map(Substat::stat).sorted()
        .map(stat -> new Choice(stat, rules.substats().get(stat).upgradeWeight())).toList();
  }

  static String select(List<Choice> choices, long draw) {
    int total = choices.stream().mapToInt(Choice::weight).sum();
    // Map one unsigned 32-bit draw into the weight range. Bucket sizes differ
    // by at most one ticket when the total weight does not divide 2^32.
    long ticket = (draw * total) >>> 32;
    for (Choice choice : choices) {
      if (ticket < choice.weight()) {
        return choice.stat();
      }
      ticket -= choice.weight();
    }
    throw new IllegalStateException("No eligible substat for roll");
  }

  static String effect(RarityRules rules, List<Substat> substats) {
    return substats.size() < rules.maxSubstats() ? "added" : "upgraded";
  }

  static List<Substat> apply(List<Substat> substats, String stat) {
    List<Substat> result = new ArrayList<>(substats);
    for (int i = 0; i < substats.size(); i++) {
      Substat existing = substats.get(i);
      if (existing.stat().equals(stat)) {
        result.set(i, new Substat(stat, existing.rolls() + 1));
        return result;
      }
    }
    result.add(new Substat(stat, 1));
    return result;
  }

  static List<Substat> canonical(List<Substat> substats) {
    return substats.stream().sorted(Comparator.comparing(Substat::stat)).toList();
  }
}
