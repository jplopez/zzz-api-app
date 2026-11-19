package com.jplopez.zzz.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureMockMvc
class AgentsControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void whenFindAllAgents_thenOK() throws Exception {
    mvc.perform(get("/agents").accept(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.agentList[*].name", containsInAnyOrder(
            "anby", "billy", "nekomata", "nicole", "anton", "ben", "grace", "koleda", "rina", "corin",
            "ellen", "lycaon", "soldier11", "soukaku", "janedoe", "zhuyuan", "qingyi", "seth", "lucy", "piper",
            "caesar", "burnice", "yanagi", "lighter")))
        .andExpect(jsonPath("$._embedded.agentList[*]._links.self.href", hasSize(24)))
        .andExpect(jsonPath("$._embedded.agentList[*]._links.allSkills.href", hasItem(endsWith("/agents/1/skills"))));
  }

  @Test
  void whenFindAgentById_thenOK() throws Exception {
    mvc.perform(get("/agents/12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("lycaon"))
        .andExpect(jsonPath("$.rarity").value("S"))
        .andExpect(jsonPath("$.attribute").value("ICE"))
        .andExpect(jsonPath("$.faction").value("Victoria Housekeeping"))
        .andExpect(jsonPath("$.version").value(1.0));
  }

  @Test
  void whenFindNonExistentAgent_thenNotFound() throws Exception {
    mvc.perform(get("/agents/1000")).andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @CsvSource({
      "/name/burnice, name, burnice",
      "/rarity/S, rarity, S",
      "/attribute/ICE, attribute, ICE",
      "/speciality/ATTACK, speciality, ATTACK",
      "/type/PIERCE, type, PIERCE"
  })
  void whenFindAgentsByField_thenOK(String path, String field, String value) throws Exception {
    mvc.perform(get("/agents" + path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$[*]." + field, everyItem(is(value))));
  }

  @Test
  void whenFindAgentByFaction_thenOK() throws Exception {
    mvc.perform(get("/agents/faction_like/Team"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$[*].faction", everyItem(containsString("Team"))));
  }

  @Test
  void whenFindAgentByVersion_thenOK() throws Exception {
    mvc.perform(get("/agents/version/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$[*].version", everyItem(is(1.2))));
  }

  @Test
  void whenFindAgentBetweenVersions_thenOK() throws Exception {
    mvc.perform(get("/agents/version/1.0/1.2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$[*].version", everyItem(oneOf(1.0, 1.1, 1.2))));
  }
}
