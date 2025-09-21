package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Wengine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.mocks.WengineMock;

/**
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
class WengineRepositoryUnitTest {

  @Autowired
  private WengineRepository repo;

  @Test
  void saveAndFindTest() {
    Wengine testWengine = WengineMock.streetSuperstarMockWengine();
    Wengine saved = repo.save(testWengine);
    assertNotNull(saved);
    assertNotNull(saved.getId());
    
    Optional<Wengine> found = repo.findById(saved.getId());
    assertFalse(found.isEmpty());
    Wengine obtained = found.get();
    assertEquals("Street Superstar", obtained.getName());
    assertEquals(Rarity.S, obtained.getRarity());
    assertEquals(Specialities.ATTACK, obtained.getSpecialty());
    assertEquals(594, obtained.getBaseAttack());
    assertNotNull(obtained.getSkillDescriptions());
    assertFalse(obtained.getSkillDescriptions().isEmpty());
  }

  @Test
  void findByNameTest() {
    Wengine testWengine = WengineMock.cannonRoamerMockWengine();
    testWengine.setWengineId("test-cannon-roamer");
    repo.save(testWengine);
    
    List<Wengine> results = repo.findByNameIgnoreCase("Cannon Roamer");
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("name", is("Cannon Roamer"))));
  }

  @Test
  void findByRarityTest() {
    Wengine testWengine = WengineMock.steamOvenMockWengine();
    testWengine.setWengineId("test-steam-oven");
    repo.save(testWengine);
    
    List<Wengine> results = repo.findByRarity(Rarity.A);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("rarity", is(Rarity.A))));
  }

  @Test
  void findBySpecialtyTest() {
    Wengine testWengine = WengineMock.streetSuperstarMockWengine();
    testWengine.setWengineId("test-street-superstar");
    repo.save(testWengine);
    
    List<Wengine> results = repo.findBySpecialty(Specialities.ATTACK);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("specialty", is(Specialities.ATTACK))));
  }

  @Test
  void findByBaseAttackTest() {
    Wengine testWengine = WengineMock.cannonRoamerMockWengine();
    testWengine.setWengineId("test-cannon-roamer-2");
    repo.save(testWengine);
    
    List<Wengine> results = repo.findByBaseAttack(624);
    assertFalse(results.isEmpty());
    assertThat(results, hasItem(hasProperty("baseAttack", is(624))));
  }

  @Test
  void findByBaseAttackBetweenTest() {
    Wengine testWengine1 = WengineMock.streetSuperstarMockWengine();
    testWengine1.setWengineId("test-street-between-1");
    Wengine testWengine2 = WengineMock.cannonRoamerMockWengine();
    testWengine2.setWengineId("test-cannon-between-2");
    repo.save(testWengine1);
    repo.save(testWengine2);
    
    List<Wengine> results = repo.findByBaseAttackBetween(500, 700);
    assertFalse(results.isEmpty());
    assertThat(results.size(), greaterThanOrEqualTo(2));
  }

}