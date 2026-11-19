package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.jplopez.zzz.entities.SkillStat;

/** Reads the legacy SKILL_STAT projection. */
public interface SkillStatRepository extends Repository<SkillStat, String> {

  @Query("select s from SkillStat s where s.skillId = :skillId order by s.level, s.tokenPosition")
  List<SkillStat> findBySkillId(String skillId);

  @Query("select s from SkillStat s where s.skillId = :skillId and s.level = :level order by s.tokenPosition")
  List<SkillStat> findBySkillIdAndLevel(String skillId, int level);
}
