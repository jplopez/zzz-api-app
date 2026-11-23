package com.jplopez.zzz.discsimulation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/** Loads immutable, versioned rules from bundled data. Never consults the live game or catalog. */
@Component
final class DiscRuleCatalog {

  record RuleSet(String rulesVersion, String probabilityModel, String source, Map<String, RarityRules> rarities) {}

  record RarityRules(int levelCap, List<Integer> upgradeLevels, List<Integer> startingSubstatCounts,
                     int maxSubstats, int rollCap, Map<String, SubstatRule> substats,
                     Map<Integer, Map<String, MainStatRule>> mainStats) {
    boolean rollsAt(int level) {
      return upgradeLevels.contains(level);
    }

    int rollsBetween(int from, int to) {
      return (int) upgradeLevels.stream().filter(level -> level > from && level <= to).count();
    }

    BigDecimal mainValue(int slot, String stat, int level) {
      return mainStats.get(slot).get(stat).values().get(level);
    }
  }

  record SubstatRule(String unit, BigDecimal baseValue, int addWeight, int upgradeWeight) {}

  record MainStatRule(String unit, List<BigDecimal> values) {}

  record Entry(RuleSet rules, DiscJson.Document document) {}

  private final Map<String, Entry> entries;
  private final List<String> versions;

  DiscRuleCatalog() throws IOException {
    Map<String, Entry> found = new HashMap<>();
    var resolver = new PathMatchingResourcePatternResolver();
    for (var resource : resolver.getResources("classpath*:disc-simulation/rules/*.json")) {
      try (var input = resource.getInputStream()) {
        byte[] bytes = input.readAllBytes();
        RuleSet rules = DiscJson.MAPPER.readValue(bytes, RuleSet.class);
        validate(rules);
        if (found.putIfAbsent(rules.rulesVersion(), new Entry(rules, DiscJson.Document.of(bytes))) != null) {
          throw new IllegalStateException("Duplicate disc rules version " + rules.rulesVersion());
        }
      }
    }
    if (found.isEmpty()) {
      throw new IllegalStateException("No disc simulation rules found");
    }
    entries = Map.copyOf(found);
    versions = found.keySet().stream().sorted(DiscRuleCatalog::compareVersions).toList();
  }

  Entry entry(String version, String pointer) {
    String selected = version == null ? versions.get(versions.size() - 1) : version;
    Entry result = entries.get(selected);
    if (result == null) {
      throw DiscProblem.unsupported(pointer, versions);
    }
    return result;
  }

  private static int compareVersions(String left, String right) {
    String[] a = left.split("\\.");
    String[] b = right.split("\\.");
    for (int i = 0; i < Math.min(a.length, b.length); i++) {
      int comparison = Integer.compare(Integer.parseInt(a[i]), Integer.parseInt(b[i]));
      if (comparison != 0) {
        return comparison;
      }
    }
    return Integer.compare(a.length, b.length);
  }

  private static void validate(RuleSet rules) {
    if (!rules.rulesVersion().matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?") || rules.rarities().isEmpty()) {
      throw new IllegalStateException("Invalid disc rules version");
    }
    for (RarityRules rarity : rules.rarities().values()) {
      int cap = rarity.levelCap();
      List<Integer> upgrades = rarity.upgradeLevels();
      if (cap < 1 || cap > 15 || upgrades.isEmpty() || upgrades.size() > 5
          || !upgrades.equals(upgrades.stream().distinct().sorted().toList())
          || upgrades.get(0) < 1 || upgrades.get(upgrades.size() - 1) != cap
          || rarity.maxSubstats() < 1 || rarity.maxSubstats() > 4 || rarity.rollCap() < 1 || rarity.rollCap() > 6
          || rarity.startingSubstatCounts().isEmpty()
          || rarity.startingSubstatCounts().stream().anyMatch(n -> n < 1 || n > rarity.maxSubstats())
          || rarity.substats().size() <= rarity.maxSubstats()
          || rarity.substats().values().stream().anyMatch(stat -> stat.baseValue().signum() <= 0
              || stat.addWeight() <= 0 || stat.upgradeWeight() <= 0)
          || rarity.substats().values().stream().mapToLong(SubstatRule::addWeight).sum() > Integer.MAX_VALUE
          || rarity.substats().values().stream().mapToLong(SubstatRule::upgradeWeight).sum() > Integer.MAX_VALUE
          || rarity.mainStats().size() != 6) {
        throw new IllegalStateException("Invalid rarity data in disc rules " + rules.rulesVersion());
      }
      for (int slot = 1; slot <= 6; slot++) {
        Map<String, MainStatRule> main = rarity.mainStats().get(slot);
        if (main == null || main.isEmpty() || main.values().stream().anyMatch(stat -> stat.values().size() != cap + 1
            || stat.values().stream().anyMatch(value -> value.signum() <= 0))) {
          throw new IllegalStateException("Missing main stat values in disc rules " + rules.rulesVersion());
        }
      }
    }
  }
}
