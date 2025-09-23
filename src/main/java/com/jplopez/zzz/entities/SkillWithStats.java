package com.jplopez.zzz.entities;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SkillWithStats extends RepresentationModel<SkillWithStats> {
  
  private Skill skill;
  private List<SkillStat> skillStats;
  
  public SkillWithStats(Skill skill, List<SkillStat> skillStats) {
    this.skill = skill;
    this.skillStats = skillStats;
  }
}