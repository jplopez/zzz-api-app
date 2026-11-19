package com.jplopez.zzz.entities;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Legacy read-only projection of skill description token values. */
@Data
@NoArgsConstructor
@Entity
@Immutable
public class SkillStat {

  @Id
  private String id;

  @Column(nullable = false)
  private String skillId;

  @Column(nullable = false)
  private int level;

  @Column(nullable = false)
  private int tokenPosition;

  @Column(nullable = false)
  private float tokenValue;

  public SkillStat(String skillId, int level, int tokenPosition, float tokenValue) {
    this.skillId = skillId;
    this.level = level;
    this.tokenPosition = tokenPosition;
    this.tokenValue = tokenValue;
  }
}
