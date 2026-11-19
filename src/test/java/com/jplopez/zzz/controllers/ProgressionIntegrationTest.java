package com.jplopez.zzz.controllers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class ProgressionIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @ParameterizedTest
  @CsvSource({
      "/agent/1/stats/base/1, anby-atk, 5, 10.0",
      "/bangboo/1/stats/base/1, amillion-atk, 6, 25.0"
  })
  void returnsNamedBaseStatsAtTheRequestedLevel(String path, String attackId, int count, double attack) throws Exception {
    mvc.perform(get(path).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.stats", hasSize(count)))
        .andExpect(jsonPath("$.stats[0].id").value(attackId))
        .andExpect(jsonPath("$.stats[0].name").value("Attack"))
        .andExpect(jsonPath("$.stats[0].type").value("base"))
        .andExpect(jsonPath("$.stats[0].abbr").value("ATK"))
        .andExpect(jsonPath("$.stats[0].unit").value("number"))
        .andExpect(jsonPath("$.stats[0].skillId").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.stats[0].values[0].value").value(attack))
        .andExpect(jsonPath("$.stats[*].values[*].level", hasSize(count)))
        .andExpect(jsonPath("$.stats[?(@.abbr == 'CRIT_RATE')].unit", contains("percentage")));
  }

  @ParameterizedTest
  @CsvSource({ "/a/1/stats/BASE, anby-atk, 10.0, 12.0, 14.0", "/b/1/stats/base, amillion-atk, 25.0, 27.0, 29.0" })
  void returnsOneStatsFullProgression(String path, String statId, double first, double second, double third) throws Exception {
    mvc.perform(get(path).param("statId", statId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats", hasSize(1)))
        .andExpect(jsonPath("$.stats[0].id").value(statId))
        .andExpect(jsonPath("$.stats[0].values[*].level", contains(1, 2, 3)))
        .andExpect(jsonPath("$.stats[0].values[*].value", contains(first, second, third)));
  }

  @ParameterizedTest
  @CsvSource({
      "/agents/1/skills, 5, 1, Turbo Volt, basic",
      "/bangboos/1/skills, 3, 11, Pocket Barrage, active"
  })
  void listsSkillDescriptionsWithoutScalingValues(String path, int count, String skillId, String name, String type)
      throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.skillList", hasSize(count)))
        .andExpect(jsonPath("$._embedded.skillList[0].id").value(skillId))
        .andExpect(jsonPath("$._embedded.skillList[0].name").value(name))
        .andExpect(jsonPath("$._embedded.skillList[0].type").value(type))
        .andExpect(jsonPath("$._embedded.skillList[0].description").isNotEmpty())
        .andExpect(jsonPath("$._embedded.skillList[0].values").doesNotHaveJsonPath())
        .andExpect(jsonPath("$._embedded.skillList[0].skillStats").doesNotHaveJsonPath())
        .andExpect(jsonPath("$._embedded.skillList[0]._links.stats.href", endsWith(path + "/" + skillId + "/stats")));
  }

  @ParameterizedTest
  @CsvSource({
      "/agents/1/skills/1/stats/2, 1, basic, turbo-volt-damage, 2, 175.0",
      "/bangboos/1/skills/11/stats/2, 11, active, pocket-barrage-damage, 1, 90.0"
  })
  void returnsOnlyTheRequestedSkillsStatsAtOneLevel(String path, String skillId, String type, String damageId,
                                                     int token, double damage) throws Exception {
    mvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats", hasSize(2)))
        .andExpect(jsonPath("$.stats[*].skillId", contains(skillId, skillId)))
        .andExpect(jsonPath("$.stats[*].type", contains(type, type)))
        .andExpect(jsonPath("$.stats[0].id").value(damageId))
        .andExpect(jsonPath("$.stats[0].unit").value("percentage"))
        .andExpect(jsonPath("$.stats[0].tokenPosition").value(token))
        .andExpect(jsonPath("$.stats[0].values[0].value").value(damage))
        .andExpect(jsonPath("$.stats[1].unit").value("number"))
        .andExpect(jsonPath("$.stats[*].values[*].level", contains(2, 2)));
  }

  @ParameterizedTest
  @CsvSource({
      "/agents/1/skills/1/stats, 150.0, 175.0, 200.0",
      "/bangboos/1/skills/11/stats, 80.0, 90.0, 100.0"
  })
  void omittingLevelReturnsEveryRecordedSkillValue(String path, double first, double second, double third) throws Exception {
    mvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats", hasSize(2)))
        .andExpect(jsonPath("$.stats[0].values[*].level", contains(1, 2, 3)))
        .andExpect(jsonPath("$.stats[0].values[*].value", contains(first, second, third)))
        .andExpect(jsonPath("$.stats[1].values[*].level", contains(1, 2, 3)));
  }

  @Test
  void aTypeCanIncludeMoreThanOneSkill() throws Exception {
    mvc.perform(get("/agents/1/stats/basic/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats[*].skillId", containsInAnyOrder("1", "1", "2", "2")));
    mvc.perform(get("/bangboos/1/stats/additional").param("statId", "hare-support-bonus"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats[0].skillId").value("12"))
        .andExpect(jsonPath("$.stats[0].values[*].value", contains(10.0, 12.5)));
  }

  @Test
  void returnsTheRecordedUnitForADuration() throws Exception {
    mvc.perform(get("/agents/1/skills/2/stats/2").param("statId", "thunderbolt-stun-time"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats", hasSize(1)))
        .andExpect(jsonPath("$.stats[0].unit").value("seconds"))
        .andExpect(jsonPath("$.stats[0].description").value("How long the target remains stunned."))
        .andExpect(jsonPath("$.stats[0].values[0].value").value(2.5));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/agents/2/skills", "/bangboos/2/skills" })
  void knownResourceWithoutSkillsReturnsAnEmptyHalCollection(String path) throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded").doesNotHaveJsonPath())
        .andExpect(jsonPath("$._links.self.href", endsWith(path)));
  }

  @Test
  void doesNotInventValuesAtMissingLevels() throws Exception {
    mvc.perform(get("/agents/1/skills/3/stats/60").param("statId", "fork-lightning-damage"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats[0].id").value("fork-lightning-damage"))
        .andExpect(jsonPath("$.stats[0].values").isEmpty());
    mvc.perform(get("/bangboos/1/stats/base/4"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats", hasSize(6)))
        .andExpect(jsonPath("$.stats[*].values[*]").isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = { "/agents/2/stats/base", "/bangboos/2/stats/base", "/agents/1/skills/5/stats" })
  void knownResourceWithoutDefinitionsReturnsAnEmptyCollection(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isOk()).andExpect(content().json("{\"stats\":[]}"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "/agents/missing/stats/base", "/bangboos/missing/skills", "/agents/999/skills",
      "/agents/2/skills/1/stats", "/bangboos/2/skills/11/stats", "/bangboos/1/skills/1/stats",
      "/agents/1/skills/11/stats", "/agents/1/skills/missing/stats"
  })
  void rejectsMissingOwnersAndSkillsNotOwnedByTheRequestedCharacter(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isNotFound());
  }

  @Test
  void statIdMustBelongToTheSelectedOwnerAndCategoryOrSkill() throws Exception {
    mvc.perform(get("/agents/1/stats/base").param("statId", "amillion-atk")).andExpect(status().isNotFound());
    mvc.perform(get("/agents/1/stats/basic").param("statId", "anby-atk")).andExpect(status().isNotFound());
    mvc.perform(get("/bangboos/1/skills/11/stats").param("statId", "hare-support-bonus"))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @ValueSource(strings = { "/agents/1/stats/active", "/bangboos/1/stats/basic", "/agents/1/stats/base/0",
      "/bangboos/1/stats/base/61", "/agents/1/skills/1/stats/-1", "/bangboos/1/skills/11/stats/not-a-level" })
  void rejectsInvalidCategoriesOrLevels(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isBadRequest());
  }

  @Test
  void rejectsBlankStatId() throws Exception {
    mvc.perform(get("/agents/1/stats/base").param("statId", " ")).andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  @Sql(statements = {
      "insert into STAT_DEFINITION (ID, SKILL_ID, NAME, UNIT) values ('turbo-volt-daze', 1, 'Daze multiplier', 'percentage')",
      "insert into STAT_VALUE (STAT_ID, LEVEL, AMOUNT) values ('turbo-volt-daze', 4, 42.125)"
  })
  void supportsSkillStatsThatAreNotDescriptionTokens() throws Exception {
    mvc.perform(get("/agents/1/skills/1/stats/4").param("statId", "turbo-volt-daze"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.stats[0].tokenPosition").doesNotHaveJsonPath())
        .andExpect(jsonPath("$.stats[0].values[0].value").value(42.125));
    mvc.perform(get("/agents/1/skills/1/level/4"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.skillStats").isEmpty());
  }

  @Test
  void preservesAgentSkillTokenResponses() throws Exception {
    mvc.perform(get("/agents/1/skill/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.skill.name").value("Turbo Volt"))
        .andExpect(jsonPath("$.skillStats", hasSize(6)));
    mvc.perform(get("/agents/1/skills/1/level/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.skillStats[*].tokenPosition", contains(1, 2)))
        .andExpect(jsonPath("$.skillStats[1].tokenValue").value(175.0));
  }
}
