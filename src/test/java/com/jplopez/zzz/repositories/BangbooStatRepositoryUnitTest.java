package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.mocks.BangbooStatMock;

/**
 * Unit tests for BangbooStatRepository.
 * Tests all find methods including ordering by level and finding by bangboo ID and level.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class BangbooStatRepositoryUnitTest {

  @Autowired
  private BangbooStatRepository repository;

  @Test
  void testFindByBangbooIdOrderByLevelAsc() {
    // Create some mock stats
    String testBangbooId = "test-bangboo-1";
    List<BangbooStat> mockStats = BangbooStatMock.createMockStatsForBangboo(testBangbooId);
    
    // Save only first 5 levels for testing
    for (int i = 0; i < 5; i++) {
      repository.save(mockStats.get(i));
    }

    // Find and verify ordering
    List<BangbooStat> found = repository.findByBangbooIdOrderByLevelAsc(testBangbooId);
    
    assertNotNull(found);
    assertThat(found.size(), is(5));
    
    // Verify levels are in ascending order
    for (int i = 0; i < found.size(); i++) {
      assertEquals(i + 1, found.get(i).getLevel());
      assertEquals(testBangbooId, found.get(i).getBangbooId());
    }

    // Clean up
    repository.deleteAll(found);
  }

  @Test
  void testFindByBangbooIdAndLevel() {
    // Create a single mock stat
    String testBangbooId = "test-bangboo-2";
    int testLevel = 25;
    BangbooStat mockStat = BangbooStatMock.createMockStatForLevel(testBangbooId, testLevel);
    
    BangbooStat saved = repository.save(mockStat);

    // Find by bangboo ID and level
    Optional<BangbooStat> found = repository.findByBangbooIdAndLevel(testBangbooId, testLevel);
    
    assertTrue(found.isPresent());
    assertEquals(testBangbooId, found.get().getBangbooId());
    assertEquals(testLevel, found.get().getLevel());
    assertEquals(saved.getId(), found.get().getId());

    // Clean up
    repository.delete(saved);
  }

  @Test
  void testFindByBangbooIdAndLevel_NotFound() {
    // Try to find non-existent stat
    Optional<BangbooStat> found = repository.findByBangbooIdAndLevel("non-existent", 1);
    
    assertFalse(found.isPresent());
  }

  @Test
  void testFindByBangbooIdOrderByLevelAsc_EmptyResult() {
    // Try to find stats for non-existent bangboo
    List<BangbooStat> found = repository.findByBangbooIdOrderByLevelAsc("non-existent-bangboo");
    
    assertNotNull(found);
    assertTrue(found.isEmpty());
  }
}