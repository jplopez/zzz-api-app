package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.SkillStat;

@Repository
public interface SkillStatRepository extends JpaRepository<SkillStat, String> {
  
  List<SkillStat> findBySkillId(String skillId);
  
  List<SkillStat> findBySkillIdAndLevel(String skillId, int level);
  
}