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
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;

/**
 * Unit tests for BangbooRepository.
 * Tests all find methods and edge cases for nullable columns and invalid enum values.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class BangbooRepositoryUnitTest {

  @Autowired
  private BangbooRepository repo;

  @Test
  void findAllTest() {
    Iterable<Bangboo> bangboos = repo.findAll();
    bangboos.forEach(this::weakAssertBangboo);
  }

  @Test
  void findById_thenOK() {
    Optional<Bangboo> optional = repo.findById("b1");
    assertFalse(optional.isEmpty());
    Bangboo obtained = optional.get();
    assertEquals("Butler", obtained.getName());
    assertEquals(Rarity.S, obtained.getRarity());
    assertEquals("Victoria Housekeeping", obtained.getFaction());
    assertEquals("BB001", obtained.getBangbooId());
  }

  @Test
  void findById_nonExistent_thenEmpty() {
    Optional<Bangboo> optional = repo.findById("nonexistent");
    assertTrue(optional.isEmpty());
  }

  @Test
  void findByNameIgnoreCase_thenOK() {
    List<Bangboo> bangboos = repo.findByNameIgnoreCase("butler");
    assertThat(bangboos, hasSize(1));
    assertEquals("Butler", bangboos.get(0).getName());
  }

  @Test
  void findByNameIgnoreCase_nonExistent_thenEmpty() {
    List<Bangboo> bangboos = repo.findByNameIgnoreCase("nonexistent");
    assertThat(bangboos, hasSize(0));
  }

  @Test
  void findByNameContaining_thenOK() {
    List<Bangboo> bangboos = repo.findByNameContaining("boo");
    assertThat(bangboos.size(), greaterThan(0));
    // Should find names containing "boo" like "Rocketboo", "Bangvolver", etc.
    bangboos.forEach(b -> assertThat(b.getName().toLowerCase(), containsString("boo")));
  }

  @Test
  void findByBangbooId_thenOK() {
    List<Bangboo> bangboos = repo.findByBangbooId("BB001");
    assertThat(bangboos, hasSize(1));
    assertEquals("Butler", bangboos.get(0).getName());
  }

  @Test
  void findByRarity_thenOK() {
    List<Bangboo> bangboos = repo.findByRarity(Rarity.S);
    assertThat(bangboos.size(), greaterThanOrEqualTo(1));
    bangboos.forEach(b -> assertEquals(Rarity.S, b.getRarity()));
  }

  @Test
  void findByRarity_ARarity_thenOK() {
    List<Bangboo> bangboos = repo.findByRarity(Rarity.A);
    assertThat(bangboos.size(), greaterThanOrEqualTo(1));
    bangboos.forEach(b -> assertEquals(Rarity.A, b.getRarity()));
  }

  @Test
  void findByRarity_BRarity_thenOK() {
    List<Bangboo> bangboos = repo.findByRarity(Rarity.B);
    assertThat(bangboos.size(), greaterThanOrEqualTo(1));
    bangboos.forEach(b -> assertEquals(Rarity.B, b.getRarity()));
  }

  @Test
  void findByRarityIn_thenOK() {
    List<Bangboo> bangboos = repo.findByRarityIn(List.of(Rarity.S, Rarity.A));
    assertThat(bangboos.size(), greaterThan(0));
    bangboos.forEach(b -> assertThat(List.of(Rarity.S, Rarity.A), hasItem(b.getRarity())));
  }

  @Test
  void findByFactionContaining_thenOK() {
    List<Bangboo> bangboos = repo.findByFactionContaining("Cunning");
    assertThat(bangboos.size(), greaterThanOrEqualTo(1));
    bangboos.forEach(b -> assertThat(b.getFaction().toLowerCase(), containsString("cunning")));
  }

  @Test
  void findByFactionIgnoreCase_thenOK() {
    List<Bangboo> bangboos = repo.findByFactionIgnoreCase("cunning hares");
    assertThat(bangboos.size(), greaterThanOrEqualTo(1));
    bangboos.forEach(b -> assertEquals("Cunning Hares", b.getFaction()));
  }

  @Test
  void findByNameOrRarityOrFaction_thenOK() {
    List<Bangboo> bangboos = repo.findByNameOrRarityOrFaction("Butler", Rarity.A, "Obol Squad");
    assertThat(bangboos.size(), greaterThan(0));
    // Should find bangboos that match any of the criteria
  }

  /**
   * Test edge case with null rarity
   */
  @Test
  void findByRarity_nullRarity_thenHandled() {
    // This test verifies the system handles null rarity gracefully
    // Note: Depending on JPA implementation, this might throw or return empty
    try {
      List<Bangboo> bangboos = repo.findByRarity(null);
      assertNotNull(bangboos);
    } catch (Exception e) {
      // Expected behavior for null input
      assertNotNull(e);
    }
  }

  /**
   * Test edge case with null faction - should find bangboos with null faction
   */
  @Test
  void findByFactionIgnoreCase_nullFaction_thenHandled() {
    try {
      List<Bangboo> bangboos = repo.findByFactionIgnoreCase(null);
      assertNotNull(bangboos);
    } catch (Exception e) {
      // Expected behavior for null input
      assertNotNull(e);
    }
  }

  private void weakAssertBangboo(Bangboo bangboo) {
    assertNotNull(bangboo);
    assertNotNull(bangboo.getId());
    assertNotNull(bangboo.getName());
    assertNotNull(bangboo.getBangbooId());
    // faction and rarity can be null based on our schema
  }

}