package com.jplopez.zzz.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.mocks.BangbooMock;
import com.jplopez.zzz.repositories.BangbooRepository;

/**
 * Integration tests for BangbooController REST endpoints.
 * Tests the HATEOAS functionality and proper JSON response format.
 * 
 * @since 1.0
 */
@SpringBootTest(classes = ZzzApiAppApplication.class)
@AutoConfigureWebMvc
@AutoConfigureTestDatabase
class BangbooControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BangbooRepository repository;

  @Test
  void findAll_shouldReturnHATEOASCollection() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("integration-test-amillion");
    repository.save(testBangboo);

    mockMvc.perform(get("/bangboos")
            .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/hal+json"))
            .andExpect(jsonPath("$._embedded.bangbooList", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$._links.self.href", containsString("/bangboos")));
  }

  @Test
  void findByName_shouldReturnMatchingBangboos() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.butlerMockBangboo();
    testBangboo.setBangbooId("integration-test-butler");
    repository.save(testBangboo);

    mockMvc.perform(get("/bangboos/name/Butler")
            .accept(MediaType.APPLICATION_JSON))
            .andExpected(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].name", is("Butler")))
            .andExpect(jsonPath("$[0]._links.self.href", notNullValue()));
  }

  @Test
  void findByRarity_shouldReturnMatchingBangboos() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.amillionMockBangboo(); // S rarity
    testBangboo.setBangbooId("integration-test-rarity-s");
    repository.save(testBangboo);

    mockMvc.perform(get("/bangboos/rarity/S")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[*].rarity", everyItem(is("S"))))
            .andExpect(jsonPath("$[0]._links.self.href", notNullValue()));
  }

  @Test
  void findByFaction_shouldReturnMatchingBangboos() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.pengbooMockBangboo();
    testBangboo.setBangbooId("integration-test-faction");
    repository.save(testBangboo);

    mockMvc.perform(get("/bangboos/faction_like/Belobog")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[*].faction", everyItem(containsString("Belobog"))))
            .andExpect(jsonPath("$[0]._links.self.href", notNullValue()));
  }

  @Test
  void findByVersion_shouldReturnMatchingBangboos() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("integration-test-version");
    testBangboo.setVersion(1.2f);
    repository.save(testBangboo);

    mockMvc.perform(get("/bangboos/version/1.2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpected(status().isOk())
            .andExpected(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpected(jsonPath("$[*].version", everyItem(is(1.2))))
            .andExpected(jsonPath("$[0]._links.self.href", notNullValue()));
  }

  @Test
  void findByVersionRange_shouldReturnMatchingBangboos() throws Exception {
    // Setup test data
    Bangboo testBangboo1 = BangbooMock.amillionMockBangboo();
    testBangboo1.setBangbooId("integration-test-version-range-1");
    testBangboo1.setVersion(1.1f);
    Bangboo testBangboo2 = BangbooMock.butlerMockBangboo();
    testBangboo2.setBangbooId("integration-test-version-range-2");
    testBangboo2.setVersion(1.3f);
    repository.save(testBangboo1);
    repository.save(testBangboo2);

    mockMvc.perform(get("/bangboos/version/1.0/1.4")
            .accept(MediaType.APPLICATION_JSON))
            .andExpected(status().isOk())
            .andExpected(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
            .andExpected(jsonPath("$[0]._links.self.href", notNullValue()));
  }

  @Test
  void shortUrlPath_shouldWork() throws Exception {
    // Test that /b shortcut works
    mockMvc.perform(get("/b")
            .accept("application/hal+json"))
            .andExpect(status().isOk())
            .andExpected(content().contentType("application/hal+json"));
  }

  @Test
  void findOne_shouldReturnSingleBangboo() throws Exception {
    // Setup test data
    Bangboo testBangboo = BangbooMock.amillionMockBangboo();
    testBangboo.setBangbooId("integration-test-find-one");
    Bangboo saved = repository.save(testBangboo);

    mockMvc.perform(get("/bangboos/" + saved.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpected(status().isOk())
            .andExpected(jsonPath("$.name", is("Amillion")))
            .andExpect(jsonPath("$.rarity", is("S")))
            .andExpected(jsonPath("$.faction", is("Victoria Housekeeping")));
  }

}