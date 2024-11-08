package com.jplopez.zzz.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Skill;


@Repository
public interface SkillsRepository extends JpaRepository<Skill, String> {

  List<Skill> findByAgentId(String agentId);

  Optional<Skill> findByIdAndAgentId(String id, String agentId);

}
