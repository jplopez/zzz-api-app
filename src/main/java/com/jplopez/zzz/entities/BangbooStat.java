package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * BangbooStat Entity.
 * 
 * Stores the stats of a bangboo for each level (1 to 60).
 * Each entry represents the stats for a specific bangboo at a specific level.
 * 
 * @since 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class BangbooStat extends RepresentationModel<BangbooStat> {

  public BangbooStat(String bangbooId, int level, float hp, float atk, float def, float critRate, float critDamage, float impact) {
    this.bangbooId = bangbooId;
    this.level = level;
    this.hp = hp;
    this.atk = atk;
    this.def = def;
    this.critRate = critRate;
    this.critDamage = critDamage;
    this.impact = impact;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false)
  private String bangbooId;

  @Column(nullable = false)
  @Min(1)
  @Max(60)
  private int level;

  @Column(nullable = false)
  private float hp;

  @Column(nullable = false)
  private float atk;

  @Column(nullable = false) 
  private float def;

  @Column(nullable = false)
  private float critRate;

  @Column(nullable = false)
  private float critDamage;

  @Column(nullable = false)
  private float impact;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bangbooId", insertable = false, updatable = false)
  private Bangboo bangboo;
}