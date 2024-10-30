package com.jplopez.zzz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Agent {

  public Agent(String id, String agentId, String name) {
    this.id = id;
    this.agentId = agentId;
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @Column(nullable = false, unique = true)
  private String agentId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private String rarity;

  @Column(nullable = true)
  private String element;

  @Column(nullable = true)
  private String style;

  @Column(nullable = true)
  private String attackStyle;

  @Column(nullable = true)
  private String faction;

  @Column(nullable = false)
  private Double version = 1.0;


}
