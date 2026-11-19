package com.jplopez.zzz.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/** Named stat definitions paired with the requested recorded values. */
public record StatProgression(List<Stat> stats) {

  @JsonInclude(Include.NON_NULL)
  public record Stat(String id, String type, String name, String abbr, String description, String unit,
                     String skillId, Integer tokenPosition, List<LevelValue> values) {}

  public record LevelValue(int level, BigDecimal value) {}
}
