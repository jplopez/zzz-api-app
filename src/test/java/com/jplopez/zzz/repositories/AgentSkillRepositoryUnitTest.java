package com.jplopez.zzz.repositories;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.entities.AgentSkill;

@SpringBootTest(classes = com.jplopez.zzz.app.ZzzApiAppApplication.class)
class AgentSkillRepositoryUnitTest {

  @Autowired
  private AgentSkillRepository repo;

  @Test
  void findByAgentId_thenReturnMany() {
    // Test data should have skills for agent "1" (Anby)
    List<AgentSkill> agentSkills = repo.findByAgentId("1");
    
    assertThat(agentSkills, is(not(empty())));
    assertThat(agentSkills, everyItem(hasProperty("agentId", is("1"))));
  }

  @Test
  void findByAgentIdAndSkillId_thenReturnMatching() {
    // Test data should have agentId "1" linked to skillId "1"
    Optional<AgentSkill> agentSkill = repo.findByAgentIdAndSkillId("1", "1");
    
    assertThat(agentSkill.isPresent(), is(true));
    assertThat(agentSkill.get(), allOf(
        hasProperty("agentId", is("1")),
        hasProperty("skillId", is("1"))
    ));
  }
}