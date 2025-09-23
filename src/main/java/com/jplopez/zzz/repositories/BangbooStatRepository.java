package com.jplopez.zzz.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.BangbooStat;

/**
 * Repository to read data of Bangboo Stats.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface BangbooStatRepository extends JpaRepository<BangbooStat, String> {

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
}