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
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class BangbooControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @ParameterizedTest
  @ValueSource(strings = { "/bangboos", "/b" })
  void listsBangboos(String path) throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.bangbooList", hasSize(11)))
        .andExpect(jsonPath("$._links.self.href", endsWith("/bangboos")));
  }

  @Test
  void detailLinksToStats() throws Exception {
    mvc.perform(get("/b/1").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Amillion"))
        .andExpect(jsonPath("$._links.self.href", endsWith("/bangboos/1")))
        .andExpect(jsonPath("$._links.stats.href", endsWith("/bangboos/1/stats")));
  }

  @ParameterizedTest
  @CsvSource({ "/name/AMILLION, Amillion", "/name_like/lion, Amillion", "/faction_like/Belobog, Pengboo" })
  void filtersByNameAndFaction(String path, String name) throws Exception {
    mvc.perform(get("/bangboos" + path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder(name)));
  }

  @Test
  void filtersByRarityAndVersion() throws Exception {
    mvc.perform(get("/bangboos/rarity/S"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Amillion", "Boollseye", "Crybaby")));
    mvc.perform(get("/bangboos/version/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Crybaby", "Luckyboo")));
    mvc.perform(get("/bangboos/version/1.1/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Boollseye", "Officer Cui", "Crybaby", "Luckyboo")));
  }

  @Test
  void distinguishesMissingResourceFromEmptySearch() throws Exception {
    mvc.perform(get("/bangboos/missing")).andExpect(status().isNotFound());
    mvc.perform(get("/bangboos/name/missing")).andExpect(status().isOk()).andExpect(content().json("[]"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/rarity/unknown", "/version/latest", "/version/Infinity", "/version/2.0/1.0" })
  void rejectsInvalidFilters(String path) throws Exception {
    mvc.perform(get("/bangboos" + path)).andExpect(status().isBadRequest());
  }
}
