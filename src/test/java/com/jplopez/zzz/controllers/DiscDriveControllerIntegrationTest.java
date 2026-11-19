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
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class DiscDriveControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @ParameterizedTest
  @ValueSource(strings = { "/discdrives", "/dd", "/discs" })
  void listsDiscs(String path) throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.discDriveList", hasSize(7)))
        .andExpect(jsonPath("$._links.self.href", endsWith("/discdrives")));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/discdrivesets", "/dds", "/discsets" })
  void listsSets(String path) throws Exception {
    mvc.perform(get(path).accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.discDriveSetList", hasSize(2)))
        .andExpect(jsonPath("$._links.self.href", endsWith("/discdrivesets")));
  }

  @Test
  void followsDiscSetAndMembershipLinks() throws Exception {
    mvc.perform(get("/dd/4").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("4"))
        .andExpect(jsonPath("$.discDriveId").value("woodpecker-electro-4"))
        .andExpect(jsonPath("$.position").value(4))
        .andExpect(jsonPath("$.type").value("CRIT_RATE"))
        .andExpect(jsonPath("$.baseValue").value(4.8))
        .andExpect(jsonPath("$.discDriveSetId").value("1"))
        .andExpect(jsonPath("$._links.self.href", endsWith("/discdrives/4")))
        .andExpect(jsonPath("$._links.set.href", endsWith("/discdrivesets/1")));
    mvc.perform(get("/dds/1").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.discDriveSetId").value("woodpecker-electro"))
        .andExpect(jsonPath("$.twoPieceSkillDescription").value("Increases CRIT Rate by 8%."))
        .andExpect(jsonPath("$.fourPieceSkillDescription").isNotEmpty())
        .andExpect(jsonPath("$._links.discs.href", endsWith("/discdrivesets/1/discs")));
    mvc.perform(get("/discdrivesets/1/discs").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.discDriveList[*].position", contains(1, 2, 3, 4, 5, 6)))
        .andExpect(jsonPath("$._embedded.discDriveList[0]._links.self.href", endsWith("/discdrives/1")))
        .andExpect(jsonPath("$._links.self.href", endsWith("/discdrivesets/1/discs")));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/position/1", "/type/HP", "/base_value/220", "/base_value_between/220/220" })
  void filtersDiscsByPositionTypeAndValue(String path) throws Exception {
    mvc.perform(get("/discdrives" + path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", containsInAnyOrder("1", "7")));
  }

  @Test
  void filtersBySetId() throws Exception {
    mvc.perform(get("/discdrives/set/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].id", contains("7")));
    mvc.perform(get("/discdrives/set/missing")).andExpect(status().isOk()).andExpect(content().json("[]"));
  }

  @ParameterizedTest
  @CsvSource({
      "/name/woodpecker electro, Woodpecker Electro",
      "/name_like/JAZZ, Swing Jazz",
      "/description_like/regeneration, Swing Jazz",
      "/two_piece_skill_like/crit, Woodpecker Electro",
      "/four_piece_skill_like/ULTIMATE, Swing Jazz"
  })
  void filtersSetsByNameAndBonuses(String path, String name) throws Exception {
    mvc.perform(get("/discdrivesets" + path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", contains(name)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "/discdrives/missing", "/discdrivesets/missing", "/discdrivesets/missing/discs" })
  void returnsNotFoundForMissingResources(String path) throws Exception {
    mvc.perform(get(path)).andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @ValueSource(strings = { "/position/0", "/position/7", "/position/abc", "/type/UNKNOWN",
      "/base_value/NaN", "/base_value_between/30/20" })
  void rejectsInvalidDiscFilters(String path) throws Exception {
    mvc.perform(get("/discdrives" + path)).andExpect(status().isBadRequest());
  }
}
