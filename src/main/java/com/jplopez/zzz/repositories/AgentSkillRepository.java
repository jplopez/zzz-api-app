package com.jplopez.zzz.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.AgentSkill;

@Repository
public interface AgentSkillRepository extends JpaRepository<AgentSkill, String> {
  
  List<AgentSkill> findByAgentId(String agentId);
  
  Optional<AgentSkill> findByAgentIdAndSkillId(String agentId, String skillId);
  
}