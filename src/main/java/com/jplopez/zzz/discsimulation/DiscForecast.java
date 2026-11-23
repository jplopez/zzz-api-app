package com.jplopez.zzz.discsimulation;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;
import static java.math.MathContext.DECIMAL128;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jplopez.zzz.discsimulation.DiscDocuments.DiscState;
import com.jplopez.zzz.discsimulation.DiscDocuments.DiscSummary;
import com.jplopez.zzz.discsimulation.DiscDocuments.ForecastResult;
import com.jplopez.zzz.discsimulation.DiscDocuments.Outcome;
import com.jplopez.zzz.discsimulation.DiscDocuments.Substat;
import com.jplopez.zzz.discsimulation.DiscDocuments.SubstatValue;
import com.jplopez.zzz.discsimulation.DiscDocuments.Threshold;
import com.jplopez.zzz.discsimulation.DiscRuleCatalog.RarityRules;

/** Exact dynamic programming over canonical substat roll counts, independent of the session RNG. */
final class DiscForecast {

  private DiscForecast() {}

  static ForecastResult compute(DiscState disc, RarityRules rules, int target, String probabilityModel,
                                Map<String, BigDecimal> weights, List<BigDecimal> thresholds) {
    int upgrades = rules.rollsBetween(disc.level(), target);
    var states = distribution(disc, rules, upgrades);
    List<Outcome> outcomes = new ArrayList<>(states.size());
    Fraction expected = Fraction.NIL;
    List<Fraction> atLeast = new ArrayList<>(thresholds.size());
    thresholds.forEach(threshold -> atLeast.add(Fraction.NIL));
    for (var state : states.entrySet()) {
      Fraction probability = state.getValue();
      BigDecimal score = weights == null ? null : score(disc.atLevel(target, state.getKey()), rules, weights);
      if (score != null) {
        expected = expected.plus(probability.times(score));
        for (int i = 0; i < thresholds.size(); i++) {
          if (score.compareTo(thresholds.get(i)) >= 0) {
            atLeast.set(i, atLeast.get(i).plus(probability));
          }
        }
      }
      outcomes.add(new Outcome(probability.decimal(), state.getKey(), score));
    }
    List<Threshold> probabilities = new ArrayList<>(thresholds.size());
    for (int i = 0; i < thresholds.size(); i++) {
      probabilities.add(new Threshold(thresholds.get(i), atLeast.get(i).decimal()));
    }
    return new ForecastResult(probabilityModel, upgrades, outcomes, weights == null ? null : expected.decimal(),
        thresholds.isEmpty() ? null : probabilities);
  }

  static Fraction expectedScoreAtCap(DiscState disc, RarityRules rules, Map<String, BigDecimal> weights) {
    int cap = rules.levelCap();
    Fraction expected = Fraction.NIL;
    for (var state : distribution(disc, rules, rules.rollsBetween(disc.level(), cap)).entrySet()) {
      expected = expected.plus(state.getValue().times(score(disc.atLevel(cap, state.getKey()), rules, weights)));
    }
    return expected;
  }

  private static Map<List<Substat>, Fraction> distribution(DiscState disc, RarityRules rules, int upgrades) {
    Map<List<Substat>, Fraction> states = Map.of(DiscRolls.canonical(disc.substats()), Fraction.UNIT);
    for (int i = 0; i < upgrades; i++) {
      Map<List<Substat>, Fraction> next = new LinkedHashMap<>();
      for (var state : states.entrySet()) {
        var choices = DiscRolls.choices(rules, disc.mainStat(), state.getKey());
        int total = choices.stream().mapToInt(DiscRolls.Choice::weight).sum();
        if (total == 0) {
          throw new IllegalStateException("No eligible forecast outcome");
        }
        for (var choice : choices) {
          var rolled = DiscRolls.canonical(DiscRolls.apply(state.getKey(), choice.stat()));
          Fraction probability = state.getValue().times(BigInteger.valueOf(choice.weight()), BigInteger.valueOf(total));
          next.merge(rolled, probability, Fraction::plus);
        }
      }
      states = next;
    }
    return states;
  }

  static DiscSummary summary(DiscState disc, RarityRules rules, Map<String, BigDecimal> weights) {
    BigDecimal main = rules.mainValue(disc.slot(), disc.mainStat(), disc.level());
    List<SubstatValue> substats = disc.substats().stream()
        .map(substat -> new SubstatValue(substat.stat(), substat.rolls(), substatValue(substat, rules))).toList();
    return new DiscSummary(disc.level(), disc.mainStat(), main, substats,
        weights == null ? null : score(disc, rules, weights));
  }

  static BigDecimal score(DiscState disc, RarityRules rules, Map<String, BigDecimal> weights) {
    BigDecimal score = rules.mainValue(disc.slot(), disc.mainStat(), disc.level())
        .multiply(weights.getOrDefault(disc.mainStat(), BigDecimal.ZERO));
    for (Substat substat : disc.substats()) {
      score = score.add(substatValue(substat, rules).multiply(weights.getOrDefault(substat.stat(), BigDecimal.ZERO)));
    }
    return score;
  }

  private static BigDecimal substatValue(Substat substat, RarityRules rules) {
    return rules.substats().get(substat.stat()).baseValue().multiply(BigDecimal.valueOf(substat.rolls()));
  }

  /** All probability and expectation arithmetic stays rational until JSON serialization. */
  record Fraction(BigInteger numerator, BigInteger denominator) implements Comparable<Fraction> {
    static final Fraction NIL = new Fraction(ZERO, ONE);
    static final Fraction UNIT = new Fraction(ONE, ONE);

    Fraction {
      BigInteger gcd = numerator.gcd(denominator);
      numerator = numerator.divide(gcd);
      denominator = denominator.divide(gcd);
    }

    Fraction plus(Fraction other) {
      if (denominator.equals(other.denominator)) {
        return new Fraction(numerator.add(other.numerator), denominator);
      }
      return new Fraction(numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
          denominator.multiply(other.denominator));
    }

    Fraction times(BigInteger n, BigInteger d) {
      return new Fraction(numerator.multiply(n), denominator.multiply(d));
    }

    Fraction times(BigDecimal value) {
      return value.scale() < 0 ? times(value.unscaledValue().multiply(TEN.pow(-value.scale())), ONE)
          : times(value.unscaledValue(), TEN.pow(value.scale()));
    }

    BigDecimal decimal() {
      return new BigDecimal(numerator).divide(new BigDecimal(denominator), DECIMAL128);
    }

    @Override
    public int compareTo(Fraction other) {
      return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
    }
  }
}
