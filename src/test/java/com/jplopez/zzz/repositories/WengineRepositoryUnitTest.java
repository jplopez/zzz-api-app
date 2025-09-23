package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.WEngine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.mocks.WEngineMock;

/**
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class WEngineRepositoryUnitTest {

  @Autowired
  private WEngineRepository repo;

  @Test
  void saveAndFindTest() {
    WEngine testWEngine = WEngineMock.streetSuperstarMockWEngine();
    WEngine saved = repo.save(testWEngine);
    assertNotNull(saved);
    assertNotNull(saved.getId());
    
    Optional<WEngine> found = repo.findById(saved.getId());
    assertFalse(found.isEmpty());
    WEngine obtained = found.get();
    assertEquals("Street Superstar", obtained.getName());
    assertEquals(Rarity.S, obtained.getRarity());
    assertEquals(Specialties.ATTACK, obtained.getSpecialty());
    assertEquals(594, obtained.getBaseAttack());
    assertNotNull(obtained.getSkillDescriptions());
    assertFalse(obtained.getSkillDescriptions().isEmpty());
  }

  @Test
  void findByNameTest() {
    WEngine testWEngine = WEngineMock.cannonRoamerMockWEngine();
    testWEngine.setWengineId("test-cannon-roamer");
    repo.save(testWEngine);
    
    List<WEngine> results = repo.findByNameIgnoreCase("Cannon Roamer");
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("name", is("Cannon Roamer"))));
  }

  @Test
  void findByRarityTest() {
    WEngine testWEngine = WEngineMock.steamOvenMockWEngine();
    testWEngine.setWengineId("test-steam-oven");
    repo.save(testWEngine);
    
    List<WEngine> results = repo.findByRarity(Rarity.A);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("rarity", is(Rarity.A))));
  }

  @Test
  void findBySpecialtyTest() {
    WEngine testWEngine = WEngineMock.streetSuperstarMockWEngine();
    testWEngine.setWengineId("test-street-superstar");
    repo.save(testWEngine);
    
    List<WEngine> results = repo.findBySpecialty(Specialties.ATTACK);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("specialty", is(Specialties.ATTACK))));
  }

  @Test
  void findByBaseAttackTest() {
    WEngine testWEngine = WEngineMock.cannonRoamerMockWEngine();
    testWEngine.setWengineId("test-cannon-roamer-2");
    repo.save(testWEngine);
    
    List<WEngine> results = repo.findByBaseAttack(624);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("baseAttack", is(624))));
  }

  @Test
  void findByBaseAttackBetweenTest() {
    WEngine testWEngine1 = WEngineMock.streetSuperstarMockWEngine();
    testWEngine1.setWengineId("test-street-between-1");
    WEngine testWEngine2 = WEngineMock.cannonRoamerMockWEngine();
    testWEngine2.setWengineId("test-cannon-between-2");
    repo.save(testWEngine1);
    repo.save(testWEngine2);
    
    List<WEngine> results = repo.findByBaseAttackBetween(500, 700);
    assertFalse(results.isEmpty());
    assertThat(results.size(), greaterThanOrEqualTo(2));
  }

}