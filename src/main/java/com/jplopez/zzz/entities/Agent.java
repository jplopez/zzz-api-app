package com.jplopez.zzz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Agent {

  public Agent(String id, String agentId, String name) {
    this.id = id;
    this.agentId = agentId;
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String agentId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String rarity;

  @Column(nullable = false)
  private String element;

  @Column(nullable = false)
  private String style;

  @Column(nullable = false)
  private String attackStyle;

  @Column(nullable = false)
  private String faction;

  @Column(nullable = false)
  private Double version = 1.0;


}
