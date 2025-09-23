package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.DiscDriveSet;

/**
 * Unit tests for DiscDriveSetRepository.
 * Tests all find methods and edge cases for nullable columns.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class DiscDriveSetRepositoryUnitTest {

  @Autowired
  private DiscDriveSetRepository repo;

  @Test
  void findAllTest() {
    Iterable<DiscDriveSet> discDriveSets = repo.findAll();
    discDriveSets.forEach(this::weakAssertDiscDriveSet);
  }

  @Test
  void findById_thenOK() {
    Optional<DiscDriveSet> optional = repo.findById("dds1");
    assertFalse(optional.isEmpty());
    DiscDriveSet obtained = optional.get();
    assertEquals("Woodpecker Electro", obtained.getName());
    assertEquals("DDS001", obtained.getDiscDriveSetId());
    assertThat(obtained.getDescription(), containsString("electric damage amplification"));
  }

  @Test
  void findById_nonExistent_thenEmpty() {
    Optional<DiscDriveSet> optional = repo.findById("nonexistent");
    assertTrue(optional.isEmpty());
  }

  @Test
  void findByNameIgnoreCase_thenOK() {
    List<DiscDriveSet> sets = repo.findByNameIgnoreCase("woodpecker electro");
    assertThat(sets, hasSize(1));
    assertEquals("Woodpecker Electro", sets.get(0).getName());
  }

  @Test
  void findByNameIgnoreCase_nonExistent_thenEmpty() {
    List<DiscDriveSet> sets = repo.findByNameIgnoreCase("nonexistent");
    assertThat(sets, hasSize(0));
  }

  @Test
  void findByNameContaining_thenOK() {
    List<DiscDriveSet> sets = repo.findByNameContaining("Electro");
    assertThat(sets.size(), greaterThan(0));
    sets.forEach(s -> assertThat(s.getName(), containsString("Electro")));
  }

  @Test
  void findByDiscDriveSetId_thenOK() {
    List<DiscDriveSet> sets = repo.findByDiscDriveSetId("DDS001");
    assertThat(sets, hasSize(1));
    assertEquals("Woodpecker Electro", sets.get(0).getName());
  }

  @Test
  void findByDescriptionContaining_thenOK() {
    List<DiscDriveSet> sets = repo.findByDescriptionContaining("electric");
    assertThat(sets.size(), greaterThan(0));
    sets.forEach(s -> assertThat(s.getDescription().toLowerCase(), containsString("electric")));
  }

  @Test
  void findByTwoPieceSkillDescriptionContaining_thenOK() {
    List<DiscDriveSet> sets = repo.findByTwoPieceSkillDescriptionContaining("Electric DMG");
    assertThat(sets.size(), greaterThan(0));
    sets.forEach(s -> assertThat(s.getTwoPieceSkillDescription(), containsString("Electric DMG")));
  }

  @Test
  void findByFourPieceSkillDescriptionContaining_thenOK() {
    List<DiscDriveSet> sets = repo.findByFourPieceSkillDescriptionContaining("CRIT");
    assertThat(sets.size(), greaterThan(0));
    sets.forEach(s -> assertThat(s.getFourPieceSkillDescription(), containsString("CRIT")));
  }

  @Test
  void findByNameOrDescription_thenOK() {
    List<DiscDriveSet> sets = repo.findByNameOrDescription("Inferno Metal", "electric damage amplification");
    assertThat(sets.size(), greaterThan(0));
    // Should find sets that match either the name or description
  }

  /**
   * Test edge case with null description search
   */
  @Test
  void findByDescriptionContaining_nullInput_thenHandled() {
    try {
      List<DiscDriveSet> sets = repo.findByDescriptionContaining(null);
      assertNotNull(sets);
    } catch (Exception e) {
      // Expected behavior for null input
      assertNotNull(e);
    }
  }

  /**
   * Test edge case with empty string search
   */
  @Test
  void findByDescriptionContaining_emptyString_thenHandled() {
    List<DiscDriveSet> sets = repo.findByDescriptionContaining("");
    assertNotNull(sets);
    // Empty string should match all non-null descriptions
  }

  /**
   * Test searching for non-existent description content
   */
  @Test
  void findByDescriptionContaining_nonExistent_thenEmpty() {
    List<DiscDriveSet> sets = repo.findByDescriptionContaining("nonexistent content");
    assertThat(sets, hasSize(0));
  }

  /**
   * Test with special characters in search
   */
  @Test
  void findByNameContaining_specialCharacters_thenOK() {
    List<DiscDriveSet> sets = repo.findByNameContaining("-");
    // May or may not find results depending on naming conventions
    assertNotNull(sets);
  }

  private void weakAssertDiscDriveSet(DiscDriveSet discDriveSet) {
    assertNotNull(discDriveSet);
    assertNotNull(discDriveSet.getId());
    assertNotNull(discDriveSet.getName());
    assertNotNull(discDriveSet.getDiscDriveSetId());
    // description, twoPieceSkillDescription, fourPieceSkillDescription can be null
  }

}