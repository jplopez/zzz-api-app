package com.jplopez.zzz.controllers;

import com.jplopez.zzz.entities.Agent;
import static com.jplopez.zzz.mocks.AgentMock.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.apache.commons.lang3.RandomStringUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AgentsLiveTest {

  private static final String API_ROOT = "http://localhost:8081/zzz/agents";

  private String agentAsURI(Agent agent) {
    Response response = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(agent)
        .post(API_ROOT);
    return API_ROOT + "/" + response.jsonPath().get("id");
  }

  @Test
  void whenFindAllAgents_thenOK() {
    Response response = get(API_ROOT);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

    given()
      .get(API_ROOT)
      .then()
      .assertThat()
        .body("name[0]",equalTo("Lycaon"))
        .and()
        .body("name[1]", equalTo("Zhu Yuan"))
        .and()
        .body("name[2]", equalTo("Burnice"));
   }

  @Test
  void whenFindAgentById_thenOK() {
    Agent agent = lycaonMockAgent();
    agentAsURI(agent);
    Response response = get(API_ROOT + "/agentId/" + agent.getId());

    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertTrue(response.as(List.class).size() > 0);

    response.then()
      .assertThat()
        .body("name", equalTo("Lycaon"))
        .and().body("rarity", equalTo("S"))
        .and().body("element", equalTo("Ice"))
        .and().body("faction", equalTo("Victoria Housekeeping"))
        .and().body("version", equalTo("1.0"));
   }

  @Test
  void whenFindNonExistentAgent_thenNotFound() {
    Response response = get(API_ROOT + "/" + randomNumeric(4));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
   }

}
