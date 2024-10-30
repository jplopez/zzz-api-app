package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.apache.commons.lang3.RandomStringUtils.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.ZzzApiAppApplication;
import com.jplopez.zzz.common.exceptions.NotFoundException;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.repositories.AgentsRepository;

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
  }

  @Test
  void findById_thenNotFound() {
    Optional<Agent> optional = repo.findById(randomNumeric(4));
    assertFalse(optional.isPresent());
  }

  @Test
  void findByName_thenOK() {
    Iterable<Agent> agents = repo.findByName("Lycaon");
    agents.forEach(agent -> assertEquals("Lycaon", agent.getName()));
  }
  
  private void weakAssertAgent(Agent agent) {
    assertNotNull(agent.getId());
    assertNotNull(agent.getAgentId());
    assertNotNull(agent.getName());
  }

}
