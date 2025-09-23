package com.jplopez.zzz.entities.skills;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class SkillModifier {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String modifierId;

  @Column(nullable = false, unique = false)
  private float value;

  private boolean mustBePositive = false;

  SkillModifier(String id, String modifierId) {
    this.id = id;
    this.modifierId = modifierId;
  }

  SkillModifier(String id, String modifierId, float value) {
    this.id = id;
    this.modifierId = modifierId;
    this.value = value;
  }

  SkillModifier(String id, String modifierId, int value) {
    this.id = id;
    this.modifierId = modifierId;
    this.value = value;
  }

  public boolean mustBePositive() { return mustBePositive; }

  public void mustBePositive(boolean value) { mustBePositive = value; }

  public float normalizedValue() { return mustBePositive ? Math.abs(value / 100.0f) : (value / 100.0f);}

  public int intValue() { return (int) (mustBePositive ? Math.abs(Math.floor(value))  :  Math.floor(value)); }

}
