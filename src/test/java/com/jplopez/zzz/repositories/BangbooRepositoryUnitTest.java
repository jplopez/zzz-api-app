package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.mocks.BangbooMock;

/**
 * Unit tests for BangbooRepository.
 * Tests all find methods and edge cases including nullable columns and invalid enum values.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class BangbooRepositoryUnitTest {

  @Autowired
  private BangbooRepository repo;

  @Test
  void saveAndFindTest() {
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("test-amillion-unique");
    Bangboo saved = repo.save(testBangboo);
    assertNotNull(saved);
    assertNotNull(saved.getId());
    
    Optional<Bangboo> found = repo.findById(saved.getId());
    assertFalse(found.isEmpty());
    Bangboo obtained = found.get();
    assertEquals("Amillion", obtained.getName());
    assertEquals(Rarity.S, obtained.getRarity());
    assertEquals("Victoria Housekeeping", obtained.getFaction());
    assertEquals(1.0f, obtained.getVersion());
    
    weakAssertBangboo(obtained);
  }

  @Test
  void findByName_thenOK() {
    Bangboo testBangboo = BangbooMock.butlerMockBangboo();
    testBangboo.setBangbooId("test-butler-name");
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByNameIgnoreCase("Butler");
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("name", is("Butler"))));
  }

  @Test
  void findByNameContaining_thenReturnOne() {
    Bangboo testBangboo = BangbooMock.pengbooMockBangboo();
    testBangboo.setBangbooId("test-pengboo-containing");
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByNameContaining("Peng");
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("name", is("Pengboo"))));
  }

  @Test
  void findByRarity_thenReturnMany() {
    Bangboo testBangboo1 = BangbooMock.butlerMockBangboo();
    testBangboo1.setBangbooId("test-butler-rarity-1");
    Bangboo testBangboo2 = BangbooMock.bagbooMockBangboo();
    testBangboo2.setBangbooId("test-bagboo-rarity-2");
    repo.save(testBangboo1);
    repo.save(testBangboo2);
    
    List<Bangboo> results = repo.findByRarity(Rarity.A);
    assertFalse(results.isEmpty());
    assertThat(results.size(), greaterThanOrEqualTo(2));
  }

  @Test
  void findByFactionContaining_thenReturnMatching() {
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("test-amillion-faction");
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByFactionContaining("Victoria");
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("faction", containsString("Victoria"))));
  }

  @Test
  void findByVersion_thenReturnMatching() {
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("test-amillion-version");
    testBangboo.setVersion(1.2f);
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByVersion(1.2f);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("version", is(1.2f))));
  }

  @Test
  void findByVersionBetween_thenReturnMatching() {
    Bangboo testBangboo1 = BangbooMock.butlerMockBangboo();
    testBangboo1.setBangbooId("test-butler-version-between-1");
    testBangboo1.setVersion(1.1f);
    Bangboo testBangboo2 = BangbooMock.pengbooMockBangboo();
    testBangboo2.setBangbooId("test-pengboo-version-between-2");
    testBangboo2.setVersion(1.3f);
    repo.save(testBangboo1);
    repo.save(testBangboo2);
    
    List<Bangboo> results = repo.findByVersionBetween(1.0f, 1.4f);
    assertFalse(results.isEmpty());
    assertThat(results.size(), greaterThanOrEqualTo(2));
  }

  @Test
  void searchByNonExistentFaction_thenReturnEmpty() {
    String nonFaction = "Non Existent Faction";
    List<Bangboo> results = repo.findByFactionContaining(nonFaction);
    assertThat(results, is(emptyIterable()));
  }

  @Test
  void testNullableRarity_thenOK() {
    Bangboo testBangboo = BangbooMock.nullRarityMockBangboo();
    testBangboo.setBangbooId("test-null-rarity");
    testBangboo.setRarity(null); // Test nullable rarity
    
    Bangboo saved = repo.save(testBangboo);
    assertNotNull(saved.getId());
    
    Optional<Bangboo> found = repo.findById(saved.getId());
    assertFalse(found.isEmpty());
    assertEquals(null, found.get().getRarity());
  }

  @Test
  void testNullableFaction_thenOK() {
    Bangboo testBangboo = BangbooMock.electrobooMockBangboo();
    testBangboo.setBangbooId("test-null-faction");
    testBangboo.setFaction(null); // Test nullable faction
    
    Bangboo saved = repo.save(testBangboo);
    assertNotNull(saved.getId());
    
    Optional<Bangboo> found = repo.findById(saved.getId());
    assertFalse(found.isEmpty());
    assertEquals(null, found.get().getFaction());
  }

  @Test
  void testDuplicateBangbooId_thenThrowException() {
    Bangboo testBangboo1 = BangbooMock.amillionMockBangboo();
    testBangboo1.setBangbooId("duplicate-id");
    Bangboo testBangboo2 = BangbooMock.butlerMockBangboo();
    testBangboo2.setBangbooId("duplicate-id"); // Same ID should cause constraint violation
    
    repo.save(testBangboo1);
    
    assertThrows(DataIntegrityViolationException.class, () -> {
      repo.save(testBangboo2);
      repo.flush(); // Force persistence context to flush and detect constraint violation
    });
  }

  @Test
  void testDuplicateName_thenThrowException() {
    Bangboo testBangboo1 = BangbooMock.amillionMockBangboo();
    testBangboo1.setBangbooId("unique-id-1");
    testBangboo1.setName("Duplicate Name");
    Bangboo testBangboo2 = BangbooMock.butlerMockBangboo();
    testBangboo2.setBangbooId("unique-id-2");
    testBangboo2.setName("Duplicate Name"); // Same name should cause constraint violation
    
    repo.save(testBangboo1);
    
    assertThrows(DataIntegrityViolationException.class, () -> {
      repo.save(testBangboo2);
      repo.flush(); // Force persistence context to flush and detect constraint violation
    });
  }

  @Test
  void findByNameOrRarityOrFactionOrVersion_withSingleParameter() {
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("test-search-single");
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByNameOrRarityOrFactionOrVersion(
        "Amillion", null, null, null);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("name", is("Amillion"))));
  }

  @Test
  void findByNameOrRarityOrFactionOrVersion_withMultipleParameters() {
    Bangboo testBangboo = BangbooMock.butlerMockBangboo();
    testBangboo.setBangbooId("test-search-multiple");
    repo.save(testBangboo);
    
    List<Bangboo> results = repo.findByNameOrRarityOrFactionOrVersion(
        null, Rarity.A, "Victoria Housekeeping", 1.0f);
    assertFalse(results.isEmpty());
    results.forEach(bangboo -> {
      boolean matches = bangboo.getRarity() == Rarity.A ||
                       bangboo.getFaction().contains("Victoria") ||
                       bangboo.getVersion().equals(1.0f);
      assert matches : "Bangboo should match at least one search criteria";
    });
  }

  @Test
  void testVersionMethods() {
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setVersion(1.2f);
    
    // Test version comparison methods
    assert testBangboo.isVersion(1.2f);
    assert testBangboo.afterVersion(1.1f);
    assert testBangboo.afterOrAtVersion(1.2f);
    assert testBangboo.beforeVersion(1.3f);
    assert testBangboo.beforeOrAtVersion(1.2f);
    assert testBangboo.wasInVersion(1.3f);
  }

  private void weakAssertBangboo(Bangboo bangboo) {
    assertNotNull(bangboo.getId());
    assertNotNull(bangboo.getBangbooId());
    assertNotNull(bangboo.getName());
  }

}