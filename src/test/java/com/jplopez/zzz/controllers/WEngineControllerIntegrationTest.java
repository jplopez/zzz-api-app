package com.jplopez.zzz.controllers;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class WEngineControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @ParameterizedTest
  @ValueSource(strings = { "/wengines", "/w" })
  void listsWEnginesWithCanonicalLinks(String path) throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.wengineList", hasSize(5)))
        .andExpect(jsonPath("$._embedded.wengineList[*]._links.self.href", hasSize(5)))
        .andExpect(jsonPath("$._links.self.href", endsWith("/wengines")));
  }

  @Test
  @Transactional
  @Sql(statements = "UPDATE WENGINE SET SUB_STAT_TYPE = 'ATK_PER', SUB_STAT_BASE_VALUE = 10 WHERE ID = '1'")
  void readsDetailWithSkillsAndSubStat() throws Exception {
    mvc.perform(get("/w/1").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wengineId").value("1"))
        .andExpect(jsonPath("$.name").value("Street Superstar"))
        .andExpect(jsonPath("$.skillDescriptions", hasSize(2)))
        .andExpect(jsonPath("$.subStatType").value("ATK_PER"))
        .andExpect(jsonPath("$.subStatBaseValue").value(10.0))
        .andExpect(jsonPath("$.version").value(1.0))
        .andExpect(jsonPath("$._links.self.href", endsWith("/wengines/1")));
  }

  @ParameterizedTest
  @CsvSource({
      "/name/STREET SUPERSTAR, Street Superstar",
      "/name_like/Battery, Demara Battery Mark II",
      "/specialty/ATTACK, Street Superstar",
      "/base_attack/594, Street Superstar",
      "/description_like/buildup, Cannon Roamer"
  })
  void filtersCatalog(String path, String name) throws Exception {
    mvc.perform(get("/wengines" + path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder(name)));
  }

  @Test
  void filtersByRarityAndInclusiveAttackRange() throws Exception {
    mvc.perform(get("/wengines/rarity/A"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Steam Oven", "Demara Battery Mark II")));
    mvc.perform(get("/wengines/base_attack_between/594/624"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Street Superstar", "Cannon Roamer", "Rainforest Gourmet")));
  }

  @Test
  @Transactional
  @Sql(statements = "UPDATE WENGINE SET VERSION = 1.2 WHERE ID = '2'")
  void filtersByExactVersionAndInclusiveRange() throws Exception {
    mvc.perform(get("/wengines/version/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cannon Roamer")));
    mvc.perform(get("/wengines/version/1.1/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cannon Roamer")));
    mvc.perform(get("/wengines/version/1.1"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  void returnsNotFoundForUnknownId() throws Exception {
    mvc.perform(get("/wengines/missing")).andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @ValueSource(strings = { "/rarity/Z", "/specialty/BOGUS", "/base_attack/abc",
      "/base_attack_between/600/500", "/version/latest", "/version/NaN", "/version/1.2/1.1" })
  void rejectsInvalidFilters(String path) throws Exception {
    mvc.perform(get("/wengines" + path)).andExpect(status().isBadRequest());
  }
}
