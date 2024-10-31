package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.apache.commons.lang3.RandomStringUtils.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Agent;

@SpringBootTest(classes = ZzzApiAppApplication.class)
class AgentsRepositoryUnitTest {

  @Autowired
  private AgentsRepository repo;

  @Test
  void findAllTest() {
    Iterable<Agent> agents = repo.findAll();
    agents.forEach(agent -> weakAssertAgent(agent));
  }

  @Test
  void findById_thenOK() {
    Optional<Agent> optional = repo.findById("2");
    assertFalse(optional.isEmpty());
    Agent obtained = optional.get();
    assertEquals("Zhu Yuan", obtained.getName());
    assertEquals("S", obtained.getRarity());
    assertEquals("Ether", obtained.getElement());
    assertEquals("Attack", obtained.getStyle());
    assertNotNull(obtained.getAgentId());
    assertEquals("Pierce",obtained.getAttackStyle());
  }

  @Test
  void findById_thenNotFound() {
    Optional<Agent> optional = repo.findById(randomNumeric(4));
    assertFalse(optional.isPresent());
  }

  @Test
  void findByName_thenOK() {
    Iterable<Agent> agents = repo.findByNameIgnoreCase("Lycaon");
    assertTrue(agents.iterator().hasNext());
    agents.forEach(agent -> assertEquals("Lycaon", agent.getName()));

  }

  @Test
  void findByElement_thenOK() {
    Iterable<Agent> agents = repo.findByElementIgnoreCase("Fire");
    assertTrue(agents.iterator().hasNext());
    agents.forEach(agent -> assertEquals("Fire", agent.getElement()));

  }

  @Test
  void search_thenReturnMany() {
    String rarity = "S";
    String attackStyle = "Pierce";

    // rarity and attackStyle
    Iterable<Agent> agents = repo.findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersionAllIgnoreCase(
      null, rarity, null, null, attackStyle, null, null);
      assertTrue(agents.iterator().hasNext());
      agents.forEach(agent -> {
      assertThat(agent, 
        anyOf(
            hasProperty("attackStyle", equalTo(attackStyle)), 
            hasProperty("rarity", equalTo(rarity)) ));
    });
  }

  @Test
  void search_thenReturnOne() {
    String style = "Attack";

    // rarity and attackStyle
    Iterable<Agent> agents = repo.findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersionAllIgnoreCase(
      null, null, null, style, null, null, null);
      assertTrue(agents.iterator().hasNext());
      agents.forEach(agent -> assertEquals(style, agent.getStyle()));
  }

  @Test
  void search_thenReturnZero() {
    Iterable<Agent> agents = repo.findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersionAllIgnoreCase(
      null, null, null, "Non Existent Style", null, null, null);
    assertFalse(agents.iterator().hasNext());
  }


  private void weakAssertAgent(Agent agent) {
    assertNotNull(agent.getId());
    assertNotNull(agent.getAgentId());
    assertNotNull(agent.getName());
  }

}
