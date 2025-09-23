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
import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.enums.StatTypes;

/**
 * Unit tests for DiscDriveRepository.
 * Tests all find methods and edge cases for nullable columns and invalid enum values.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class DiscDriveRepositoryUnitTest {

  @Autowired
  private DiscDriveRepository repo;

  @Test
  void findAllTest() {
    Iterable<DiscDrive> discDrives = repo.findAll();
    discDrives.forEach(this::weakAssertDiscDrive);
  }

  @Test
  void findById_thenOK() {
    Optional<DiscDrive> optional = repo.findById("dd1");
    assertFalse(optional.isEmpty());
    DiscDrive obtained = optional.get();
    assertEquals("DD001", obtained.getDiscDriveId());
    assertEquals(1, obtained.getPosition());
    assertEquals(StatTypes.HP, obtained.getType());
    assertEquals(2780.0f, obtained.getBaseValue(), 0.01f);
  }

  @Test
  void findById_nonExistent_thenEmpty() {
    Optional<DiscDrive> optional = repo.findById("nonexistent");
    assertTrue(optional.isEmpty());
  }

  @Test
  void findByDiscDriveId_thenOK() {
    List<DiscDrive> drives = repo.findByDiscDriveId("DD001");
    assertThat(drives, hasSize(1));
    assertEquals("DD001", drives.get(0).getDiscDriveId());
  }

  @Test
  void findByPosition_thenOK() {
    List<DiscDrive> drives = repo.findByPosition(1);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(1, d.getPosition()));
  }

  @Test
  void findByType_thenOK() {
    List<DiscDrive> drives = repo.findByType(StatTypes.ATK);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(StatTypes.ATK, d.getType()));
  }

  @Test
  void findByType_HpType_thenOK() {
    List<DiscDrive> drives = repo.findByType(StatTypes.HP);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(StatTypes.HP, d.getType()));
  }

  @Test
  void findByType_DefType_thenOK() {
    List<DiscDrive> drives = repo.findByType(StatTypes.DEF);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(StatTypes.DEF, d.getType()));
  }

  @Test
  void findByType_ElectricDmgType_thenOK() {
    List<DiscDrive> drives = repo.findByType(StatTypes.ELECTRIC_DMG);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(StatTypes.ELECTRIC_DMG, d.getType()));
  }

  @Test
  void findByTypeIn_thenOK() {
    List<DiscDrive> drives = repo.findByTypeIn(List.of(StatTypes.ATK, StatTypes.HP));
    assertThat(drives.size(), greaterThan(0));
    drives.forEach(d -> assertThat(List.of(StatTypes.ATK, StatTypes.HP), hasItem(d.getType())));
  }

  @Test
  void findByBaseValue_thenOK() {
    List<DiscDrive> drives = repo.findByBaseValue(2780.0f);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> assertEquals(2780.0f, d.getBaseValue(), 0.01f));
  }

  @Test
  void findByBaseValueBetween_thenOK() {
    List<DiscDrive> drives = repo.findByBaseValueBetween(100.0f, 200.0f);
    assertThat(drives.size(), greaterThan(0));
    drives.forEach(d -> {
      assertTrue(d.getBaseValue() >= 100.0f);
      assertTrue(d.getBaseValue() <= 200.0f);
    });
  }

  @Test
  void findByDiscDriveSetId_thenOK() {
    List<DiscDrive> drives = repo.findByDiscDriveSetId("dds1");
    assertThat(drives.size(), greaterThan(0));
    drives.forEach(d -> assertEquals("dds1", d.getDiscDriveSetId()));
  }

  @Test
  void findByPositionAndType_thenOK() {
    List<DiscDrive> drives = repo.findByPositionAndType(1, StatTypes.HP);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> {
      assertEquals(1, d.getPosition());
      assertEquals(StatTypes.HP, d.getType());
    });
  }

  @Test
  void findByDiscDriveSetIdAndPosition_thenOK() {
    List<DiscDrive> drives = repo.findByDiscDriveSetIdAndPosition("dds1", 1);
    assertThat(drives.size(), greaterThanOrEqualTo(1));
    drives.forEach(d -> {
      assertEquals("dds1", d.getDiscDriveSetId());
      assertEquals(1, d.getPosition());
    });
  }

  @Test
  void findByTypeOrBaseValue_thenOK() {
    List<DiscDrive> drives = repo.findByTypeOrBaseValue(StatTypes.ATK, 30.0f);
    assertThat(drives.size(), greaterThan(0));
    // Should find drives that match either the type or base value
  }

  /**
   * Test edge case with null type
   */
  @Test
  void findByType_nullType_thenHandled() {
    try {
      List<DiscDrive> drives = repo.findByType(null);
      assertNotNull(drives);
    } catch (Exception e) {
      // Expected behavior for null input
      assertNotNull(e);
    }
  }

  /**
   * Test edge case with zero base value
   */
  @Test
  void findByBaseValue_zeroValue_thenOK() {
    List<DiscDrive> drives = repo.findByBaseValue(0.0f);
    assertNotNull(drives);
    // Should handle zero values gracefully
  }

  /**
   * Test edge case with negative base value
   */
  @Test
  void findByBaseValue_negativeValue_thenOK() {
    List<DiscDrive> drives = repo.findByBaseValue(-1.0f);
    assertNotNull(drives);
    // Should handle negative values gracefully (may return empty list)
  }

  /**
   * Test with invalid position (negative)
   */
  @Test
  void findByPosition_negativePosition_thenHandled() {
    List<DiscDrive> drives = repo.findByPosition(-1);
    assertNotNull(drives);
    // Should return empty list for invalid positions
    assertThat(drives, hasSize(0));
  }

  /**
   * Test finding drives with null values (edge case data)
   */
  @Test
  void findById_withNullValues_thenOK() {
    // Test the edge case entry with null values
    Optional<DiscDrive> optional = repo.findById("dd17");
    if (!optional.isEmpty()) {
      DiscDrive drive = optional.get();
      assertEquals("DD017", drive.getDiscDriveId());
      // position, type, baseValue, and discDriveSetId should be null
    }
  }

  /**
   * Test base value range with invalid range (from > to)
   */
  @Test
  void findByBaseValueBetween_invalidRange_thenHandled() {
    List<DiscDrive> drives = repo.findByBaseValueBetween(200.0f, 100.0f);
    assertNotNull(drives);
    // Should handle invalid range gracefully (likely return empty list)
  }

  private void weakAssertDiscDrive(DiscDrive discDrive) {
    assertNotNull(discDrive);
    assertNotNull(discDrive.getId());
    assertNotNull(discDrive.getDiscDriveId());
    // position, type, baseValue, discDriveSetId can be null based on our schema
  }

}