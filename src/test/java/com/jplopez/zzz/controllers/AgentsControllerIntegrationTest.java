package com.jplopez.zzz.controllers;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Agent;
import static com.jplopez.zzz.mocks.AgentMock.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.apache.commons.lang3.RandomStringUtils.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ZzzApiAppApplication.class)
class AgentsControllerIntegrationTest {

  private static final String API_ROOT = "http://localhost:8081/agents";

  private String agentAsURI(Agent agent) {
    Response response = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(agent)
        .post(API_ROOT);
    return API_ROOT + "/" + response.jsonPath().get("id[0]");
  }

  @Test
  void whenFindAllAgents_thenOK() {
    Response response = get(API_ROOT);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());

    given()
        .get(API_ROOT)
        .then()
        .assertThat()
        .body("name[0]", anyOf(equalTo("Lycaon"), equalTo("Zhu Yuan"), equalTo("Burnice")))
        .and()
        .body("name[1]", anyOf(equalTo("Lycaon"), equalTo("Zhu Yuan"), equalTo("Burnice")))
        .and()
        .body("name[2]", anyOf(equalTo("Lycaon"), equalTo("Zhu Yuan"), equalTo("Burnice")));
  }

  @Test
  void whenFindAgentById_thenOK() {
    Agent agent = lycaonMockAgent();
    String url = API_ROOT + "/" + agent.getId();

    given()
        .get(url)
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body("name", equalTo("Lycaon"))
        .and().body("rarity", equalToIgnoringCase("S"))
        .and().body("element", equalToIgnoringCase("Ice"))
        .and().body("faction", equalToIgnoringCase("Victoria Housekeeping"))
        .and().body("version", is(1.0f));
  }

  @Test
  void whenFindAgentByName_thenOK() {
    String name = "Burnice";
    String path = API_ROOT + "/name/" + name;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(1, agents.size());
    agents.forEach(agent -> { assertEquals(name, agent.getName());});
  }

  @Test
  void whenFindAgentByRarity_thenOK() {
    String rarity = "S";
    String path = API_ROOT + "/rarity/" + rarity;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(3, agents.size());
    agents.forEach(agent -> { assertEquals(rarity, agent.getRarity());});
  }

  @Test
  void whenFindAgentByElement_thenOK() {
    String element = "Ice";
    String path = API_ROOT + "/element/" + element;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(1, agents.size());
    agents.forEach(agent -> { assertEquals(element, agent.getElement());});
  }

  @Test
  void whenFindAgentByStyle_thenOK() {
    String style = "Attack";
    String path = API_ROOT + "/style/" + style;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(1, agents.size());
    agents.forEach(agent -> { assertEquals(style, agent.getStyle());});
  }

  @Test
  void whenFindAgentByAttackStyle_thenOK() {
    String attackStyle = "Pierce";
    String path = API_ROOT + "/attackStyle/" + attackStyle;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(2, agents.size());
    agents.forEach(agent -> { assertEquals(attackStyle, agent.getAttackStyle());});
  }

  @Test
  void whenFindAgentByFaction_thenOK() {
    String faction = "N.E.P.S.";
    String path = API_ROOT + "/faction/" + faction;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(1, agents.size());
    agents.forEach(agent -> { assertEquals(faction, agent.getFaction());});
  }

  @Test
  void whenFindAgentByVersion_thenOK() {
    Double version = 1.2;
    String path = API_ROOT + "/version/" + version;

    given().get(path).then().assertThat().statusCode(HttpStatus.OK.value());

    List<Agent> agents = given().get(path).as(new TypeRef<List<Agent>>() {});
    assertEquals(1, agents.size());
    agents.forEach(agent -> { assertEquals(version, agent.getVersion());});
  }

  @Test
  void whenFindNonExistentAgent_thenNotFound() {
    Response response = get(API_ROOT + "/id/" + randomNumeric(4));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
  }

}