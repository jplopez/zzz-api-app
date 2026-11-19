package com.jplopez.zzz.controllers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class BangbooControllerStatIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void returnsRecordedStatsInLevelOrder() throws Exception {
    mvc.perform(get("/bangboos/1/stats").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].level", contains(1, 2, 3)))
        .andExpect(jsonPath("$[0].bangbooId").value("1"))
        .andExpect(jsonPath("$[0].hp").value(100.0))
        .andExpect(jsonPath("$[0].links[0].rel").value("self"))
        .andExpect(jsonPath("$[0].links[0].href", endsWith("/bangboos/1/stats/1")))
        .andExpect(jsonPath("$[0].bangboo").doesNotHaveJsonPath());
  }

  @Test
  void linksToNeighboringRecordedLevels() throws Exception {
    mvc.perform(get("/b/1/stats/2").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.level").value(2))
        .andExpect(jsonPath("$._links.self.href", endsWith("/bangboos/1/stats/2")))
        .andExpect(jsonPath("$._links.allStats.href", endsWith("/bangboos/1/stats")))
        .andExpect(jsonPath("$._links.prev.href", endsWith("/bangboos/1/stats/1")))
        .andExpect(jsonPath("$._links.next.href", endsWith("/bangboos/1/stats/3")));
  }

  @Test
  void omitsLinksToLevelsWithoutData() throws Exception {
    mvc.perform(get("/bangboos/1/stats/1").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.prev").doesNotExist())
        .andExpect(jsonPath("$._links.next.href", endsWith("/bangboos/1/stats/2")));
    mvc.perform(get("/bangboos/1/stats/3").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.prev.href", endsWith("/bangboos/1/stats/2")))
        .andExpect(jsonPath("$._links.next").doesNotExist());
  }

  @Test
  void returnsEmptyListForBangbooWithoutStats() throws Exception {
    mvc.perform(get("/bangboos/2/stats"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/missing/stats", "/missing/stats/1", "/1/stats/4" })
  void returnsNotFoundForMissingBangbooOrLevel(String path) throws Exception {
    mvc.perform(get("/bangboos" + path)).andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @ValueSource(strings = { "0", "61", "not-a-level" })
  void rejectsInvalidLevels(String level) throws Exception {
    mvc.perform(get("/bangboos/1/stats/{level}", level)).andExpect(status().isBadRequest());
  }
}
