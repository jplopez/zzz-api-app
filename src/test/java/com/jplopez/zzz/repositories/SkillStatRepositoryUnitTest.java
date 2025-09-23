package com.jplopez.zzz.repositories;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.entities.SkillStat;

@SpringBootTest(classes = com.jplopez.zzz.app.ZzzApiAppApplication.class)
class SkillStatRepositoryUnitTest {

  @Autowired
  private SkillStatRepository repo;

  @Test
  void findBySkillId_thenReturnMany() {
    // Test data should have skill stats for skillId "1" (Turbo Volt)
    List<SkillStat> skillStats = repo.findBySkillId("1");
    
    assertThat(skillStats, is(not(empty())));
    assertThat(skillStats, everyItem(hasProperty("skillId", is("1"))));
  }

  @Test
  void findBySkillIdAndLevel_thenReturnMatching() {
    // Test data should have skill stats for skillId "1" level 1
    List<SkillStat> skillStats = repo.findBySkillIdAndLevel("1", 1);
    
    assertThat(skillStats, is(not(empty())));
    assertThat(skillStats, everyItem(allOf(
        hasProperty("skillId", is("1")),
        hasProperty("level", is(1))
    )));
  }
}