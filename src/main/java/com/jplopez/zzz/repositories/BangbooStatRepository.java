package com.jplopez.zzz.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.jplopez.zzz.entities.BangbooStat;

/** Reads the legacy BANGBOO_STAT projection. */
public interface BangbooStatRepository extends Repository<BangbooStat, String> {

  /**
   * Find all stats for a specific bangboo ordered by level ascending
   * @param bangbooId the bangboo ID
   * @return list of stats ordered by level
   */
  List<BangbooStat> findByBangbooIdOrderByLevelAsc(String bangbooId);

  /**
   * Find stats for a specific bangboo at a specific level
   * @param bangbooId the bangboo ID
   * @param level the level (1-60)
   * @return optional bangboo stat
   */
  Optional<BangbooStat> findByBangbooIdAndLevel(String bangbooId, int level);

  boolean existsByBangbooIdAndLevel(String bangbooId, int level);
}
