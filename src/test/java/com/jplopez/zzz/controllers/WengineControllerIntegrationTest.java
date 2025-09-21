package com.jplopez.zzz.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.jplopez.zzz.entities.Wengine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

/**
 * @since 1.0
 */
@SpringBootTest(classes = com.jplopez.zzz.app.ZzzApiAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WengineControllerIntegrationTest {

  @LocalServerPort
  int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Test
  void whenFindAllWengines_thenOK() {
    Response response = given()
        .header("Accept", "application/hal+json")
        .get("/wengines");

    response.then().assertThat().statusCode(HttpStatus.OK.value());
  }

  @Test
  void whenFindWengineByRarity_thenOK() {
    String path = "/wengines/rarity/" + Rarity.S.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Wengine> wengines = response.as(new TypeRef<List<Wengine>>() {
    });
    assertFalse(wengines.isEmpty());
    assertThat(wengines, hasItem(hasProperty("rarity", is(Rarity.S))));
  }

  @Test
  void whenFindWengineBySpecialty_thenOK() {
    String path = "/wengines/specialty/" + Specialities.ATTACK.toString();
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Wengine> wengines = response.as(new TypeRef<List<Wengine>>() {
    });
    assertFalse(wengines.isEmpty());
    assertThat(wengines, hasItem(hasProperty("specialty", is(Specialities.ATTACK))));
  }

  @Test
  void whenFindWengineByBaseAttack_thenOK() {
    String path = "/wengines/base_attack/594";
    Response response = given().get(path);

    response.then().assertThat().statusCode(HttpStatus.OK.value());
    List<Wengine> wengines = response.as(new TypeRef<List<Wengine>>() {
    });
    assertFalse(wengines.isEmpty());
    assertThat(wengines, hasItem(hasProperty("baseAttack", is(594))));
  }

}