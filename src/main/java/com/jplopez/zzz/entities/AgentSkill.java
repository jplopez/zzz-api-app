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
public class AgentSkill {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @Column(nullable = false)
  private String skillId;

  @Column(nullable = false)
  private String agentId;

  public AgentSkill(String skillId, String agentId) {
    this.skillId = skillId;
    this.agentId = agentId;
  }
}