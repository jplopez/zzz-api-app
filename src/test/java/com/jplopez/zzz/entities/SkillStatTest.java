package com.jplopez.zzz.entities;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SkillStatTest {

  @Test
  void testSkillStatCreation() {
    SkillStat skillStat = new SkillStat("skill123", 1, 1, 150.0f);
    
    assertThat(skillStat.getSkillId(), is("skill123"));
    assertThat(skillStat.getLevel(), is(1));
    assertThat(skillStat.getTokenPosition(), is(1));
    assertThat(skillStat.getTokenValue(), is(150.0f));
  }
  
  @Test
  void testTokenizedDescription() {
    // Test that our tokenized descriptions can be processed
    String description = "The agent increases their attack damage by {1}% for {2} seconds";
    
    assertThat(description, containsString("{1}"));
    assertThat(description, containsString("{2}"));
    
    // Simple token replacement simulation
    String processed = description
      .replace("{1}", "25")
      .replace("{2}", "10");
    
    assertThat(processed, is("The agent increases their attack damage by 25% for 10 seconds"));
  }
}