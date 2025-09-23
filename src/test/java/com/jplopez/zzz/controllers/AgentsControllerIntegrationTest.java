package com.jplopez.zzz.controllers;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.common.EnumUtils;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.Type;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;
import java.util.Random;

/**
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ZzzApiAppApplication.class)
class AgentsControllerIntegrationTest {

  @BeforeAll
  static void setUp() {
    RestAssured.baseURI = "http://localhost:8081";
    RestAssured.basePath = "/agents";
  }

  @Test
  void whenFindAllAgents_thenOK() {
    Response response = given().get();
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    String[] names = { "anby", "billy", "nekomata", "nicole", "anton", "ben", "grace", "koleda", "rina", "corin",
        "ellen", "lycaon", "soldier11", "soukaku", "janedoe", "zhuyuan", "qingyi", "seth", "lucy", "piper", "caesar",
        "burnice", "yanagi", "lighter" };

    assertEquals(Integer.valueOf(24),
        response.then().extract().body().path("_embedded.agentList.size()"));

    assertThat(response.then().extract().jsonPath().getList("_embedded.agentList.name"),
        everyItem(is(oneOf(names))));

    assertThat(response.then().extract().jsonPath().getList("_embedded.agentList.rarity"),
        everyItem(is(oneOf(EnumUtils.getStringValues(Rarity.class)))));

    assertThat(response.then().extract().jsonPath().getList("_embedded.agentList.attribute"),
        everyItem(is(oneOf(EnumUtils.getStringValues(Attributes.class)))));

    List<String> agentIds = response.then().extract().jsonPath().getList("_embedded.agentList.agentId");
    List<String> selfLinks = response.then().extract().jsonPath().getList("_embedded.agentList._links.self.href");

    selfLinks.forEach(link -> {
      assertThat(agentIds.stream().anyMatch(link::endsWith), is(true));
    });

    //TODO assert skill links are present and are formed correctly
  }

  @Test
  void whenFindAgentById_thenOK() {
    String url = "/12";
    Response response = given().get(url);

    response.then()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body("name", equalToIgnoringCase("Lycaon"))
        .and().body("rarity", equalToIgnoringCase("S"))
        .and().body("attribute", equalToIgnoringCase("Ice"))
        .and().body("faction", equalToIgnoringCase("Victoria Housekeeping"))
        .and().body("version", is(1.0f));
  }

  @Test
  void whenFindNonExistentAgent_thenNotFound() {
    Response response = get("/id/" + (new Random().nextInt(1,4) + 1000));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
  }

  @Test
  void whenFindAgentByName_thenOK() {
    String name = "burnice";
    String path = "/name/" + name;
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents.get(0),
        hasProperty("name", is(name)));
  }

  @Test
  void whenFindAgentByRarity_thenOK() {
    String path = "/rarity/" + Rarity.S.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("rarity", is(Rarity.S))));
  }

  @Test
  void whenFindAgentByAttribute_thenOK() {
    String path = "/attribute/" + Attributes.ICE.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("attribute", is(Attributes.ICE))));
  }

  @Test
  void whenFindAgentBySpecialty_thenOK() {
    String path = "/speciality/" + Specialties.ATTACK.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("speciality", is(Specialties.ATTACK))));
  }

  @Test
  void whenFindAgentByType_thenOK() {
    String path = "/type/" + Type.PIERCE.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("type", is(Type.PIERCE))));
  }

  @Test
  void whenFindAgentByFaction_thenOK() {
    String query = "Team";
    String path = "/faction_like/" + query;
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("faction", containsString(query))));
  }

  @Test
  void whenFindAgentByVersion_thenOK() {
    Double version = 1.2;
    String path = "/version/" + version;
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("version", is(version))));
  }

  @Test
  void whenFindAgentBetweenVersion_thenOK() {
    Double from = 1.0d, to = 1.2d;
    String path = "/version/" + from + "/" + to;
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Agent> agents = response.as(new TypeRef<List<Agent>>() {
    });
    assertThat(agents, everyItem(
        hasProperty("version",
            in(List.of(from, 1.1d, to)))));
  }
}
