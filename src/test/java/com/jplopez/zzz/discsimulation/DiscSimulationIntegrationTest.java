package com.jplopez.zzz.discsimulation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

@SpringBootTest(classes = ZzzApiAppApplication.class, properties = "zzz.disc-simulation.requests-per-minute=500")
@AutoConfigureMockMvc
class DiscSimulationIntegrationTest {

  private static final String SESSIONS = "/v1/disc-sessions";
  private static final String SCHEMA = "/v1/schemas/disc-session.json";
  private static final String JSON = "application/json";
  private static final String PROBLEM = "application/problem+json";

  @Autowired
  private MockMvc mvc;

  @Test
  void servesVersionedRulesAndSchemaWithConditionalCaching() throws Exception {
    String rulesPath = "/v1/disc-rules";
    mvc.perform(get(rulesPath)).andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "public, no-cache"))
        .andExpect(jsonPath("$.rules_version").value("2.5"))
        .andExpect(jsonPath("$.probability_model").value("uniform"))
        .andExpect(jsonPath("$.rarities.B.level_cap").value(9))
        .andExpect(jsonPath("$.rarities.A.starting_substat_counts[1]").value(3))
        .andExpect(jsonPath("$.rarities.S.substats.crit_rate.base_value").value(2.4))
        .andExpect(jsonPath("$.rarities.S.main_stats['4'].crit_rate.values[15]").value(24));
    String etag = mvc.perform(get(rulesPath).param("version", "2.4")).andExpect(status().isOk())
        .andExpect(jsonPath("$.rules_version").value("2.4"))
        .andExpect(header().string("Cache-Control", "public, max-age=31536000, immutable"))
        .andReturn().getResponse().getHeader("ETag");
    assertThat(etag).isNotBlank();
    mvc.perform(get(rulesPath).param("version", "2.4").header("If-None-Match", etag))
        .andExpect(status().isNotModified()).andExpect(header().string("ETag", etag))
        .andExpect(content().string(""));

    var schema = mvc.perform(get(SCHEMA)).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/schema+json"))
        .andExpect(jsonPath("$['$schema']").value("https://json-schema.org/draft/2020-12/schema")).andReturn();
    mvc.perform(get(SCHEMA).header("If-None-Match", schema.getResponse().getHeader("ETag")))
        .andExpect(status().isNotModified());
  }

  @Test
  void schemaExampleIsReproducibleAndAllOpenApiPostExamplesWork() throws Exception {
    var schema = body(mvc.perform(get(SCHEMA)).andReturn());
    var validator = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(schema);
    for (JsonNode example : schema.path("examples")) {
      assertThat(validator.validate(example)).isEmpty();
      var created = success(SESSIONS, Map.of("initial", example.get("initial"), "seed", example.at("/rng/seed"),
          "label", example.at("/meta/label")));
      var stepped = success(SESSIONS + "/step", Map.of("session", created.get("session"), "to_level", 3));
      assertThat(stepped.get("session")).isEqualTo(example);
    }
    var openApi = URI.create("/v1/openapi.yaml");
    var document = mvc.perform(get(openApi)).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/yaml")).andReturn();
    var yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
    JsonNode spec = DiscJson.MAPPER.valueToTree(yaml.load(document.getResponse().getContentAsString()));
    assertThat(spec.path("openapi").asText()).isEqualTo("3.1.0");
    for (var path : spec.path("paths").properties()) {
      var operation = path.getValue().get("post");
      if (operation != null) {
        JsonNode request = operation.at("/requestBody/content/application~1json");
        JsonNode response = operation.at("/responses/200/content/application~1json");
        validateExample(schema, request, request.get("example"));
        var result = send(path.getKey(), request.get("example")).andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"))
            .andExpect(content().json(response.get("example").toString(), JsonCompareMode.STRICT))
            .andReturn();
        validateExample(schema, response, body(result));
      } else {
        for (var media : path.getValue().at("/get/responses/200/content")) {
          for (var example : media.get("examples")) {
            URI url = openApi.resolve(example.get("externalValue").asText());
            assertThat(url.getPath()).isEqualTo(path.getKey());
            mvc.perform(get(url)).andExpect(status().isOk());
          }
        }
      }
    }
  }

  private static void validateExample(JsonNode schema, JsonNode media, JsonNode example) {
    String ref = media.at("/schema/$ref").asText();
    assertThat(ref).startsWith("./schemas/disc-session.json#/$defs/");
    var definition = ((ObjectNode) schema).deepCopy().put("$ref", ref.substring(ref.indexOf('#')));
    var factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    assertThat(factory.getSchema(definition).validate(example)).isEmpty();
  }

  @Test
  void stepIsByteIdenticalAndAddsOneEventPerLevel() throws Exception {
    JsonNode session = create("2.5");
    var request = Map.of("session", session, "to_level", 3);
    MvcResult first = send(SESSIONS + "/step", request).andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-store"))
        .andExpect(jsonPath("$.result.events[0].kind").value("level"))
        .andExpect(jsonPath("$.result.events[1].level").value(2))
        .andExpect(jsonPath("$.result.events[2].effect").value("added"))
        .andExpect(jsonPath("$.result.events[2].draw").value(0))
        .andExpect(jsonPath("$.session.rng.draws").value(1))
        .andExpect(jsonPath("$.result.disc.main_value").value(9.6))
        .andReturn();
    MvcResult retry = send(SESSIONS + "/step", request).andExpect(status().isOk()).andReturn();
    assertThat(first.getResponse().getContentAsByteArray()).isEqualTo(retry.getResponse().getContentAsByteArray());
  }

  @Test
  void actionsTrackLevelRollAndCompletion() throws Exception {
    JsonNode current = success(SESSIONS, Map.of("initial", initial(), "seed", 42));
    for (int level = 0; level < 15; level++) {
      assertThat(current.at("/actions/next_level/body/to_level").asInt()).isEqualTo(level + 1);
      assertThat(current.at("/actions/next_level/rolls").asBoolean()).isEqualTo((level + 1) % 3 == 0);
      assertThat(current.at("/actions/next_roll/body/to_level").asInt()).isEqualTo((level / 3 + 1) * 3);
      assertThat(current.at("/actions/next_roll/levels_skipped").asInt()).isEqualTo(2 - level % 3);
      assertThat(current.at("/actions/forecast/send/0").asText()).isEqualTo("session");
      assertThat(current.at("/actions/undo").isMissingNode()).isEqualTo(level == 0);
      assertThat(current.at("/actions/fork").isMissingNode()).isEqualTo(level == 0);
      var action = current.at("/actions/next_level");
      assertThat(action.get("method").asText()).isEqualTo("POST");
      var request = ((ObjectNode) action.get("body")).deepCopy();
      for (JsonNode field : action.get("send")) {
        request.set(field.asText(), current.get(field.asText()));
      }
      current = success(action.get("href").asText(), request);
    }
    assertThat(current.get("complete").asBoolean()).isTrue();
    assertThat(current.get("actions").has("next_level")).isFalse();
    assertThat(current.get("actions").has("next_roll")).isFalse();
    assertThat(current.get("actions").has("forecast")).isTrue();
    assertThat(current.at("/session/history")).hasSize(15);
  }

  @Test
  void observedRollDerivesTheEffectWithoutUsingRng() throws Exception {
    var session = create("2.5");
    var atThree = success(SESSIONS + "/step", Map.of("session", session, "to_level", 3, "observe", Map.of("stat", "pen")));
    assertThat(atThree.at("/result/events/2/effect").asText()).isEqualTo("added");
    expectProblem(SESSIONS + "/step", Map.of("session", atThree.get("session"), "to_level", 6,
        "observe", Map.of("stat", "def")), "illegal-roll", "/observe/stat");
    var atSix = success(SESSIONS + "/step", Map.of("session", atThree.get("session"), "to_level", 6,
        "observe", Map.of("stat", "pen")));
    assertThat(atSix.at("/result/events/2/effect").asText()).isEqualTo("upgraded");
    assertThat(atSix.at("/result/events/2/draw").isMissingNode()).isTrue();
    assertThat(atSix.at("/session/rng/draws").asInt()).isZero();
    assertThat(atSix.at("/result/disc/substats/3/value").asInt()).isEqualTo(18);
  }

  @Test
  void errorsPointToTheInvalidSelectionOrDerivedState() throws Exception {
    var session = create("2.5");
    expectProblem(SESSIONS + "/step", Map.of("session", session, "to_level", 6), "multiple-rolls", "/to_level");
    expectProblem(SESSIONS + "/step", Map.of("session", session, "to_level", 4, "observe", Map.of("stat", "pen")),
        "no-roll-to-observe", "/observe");
    expectProblem(SESSIONS + "/step", Map.of("session", session, "to_level", 3, "observe", Map.of("stat", "crit_rate")),
        "illegal-roll", "/observe/stat");
    ((ObjectNode) session.get("disc")).put("level", 1);
    expectProblem(SESSIONS + "/step", Map.of("session", session, "to_level", 3), "replay-mismatch", "/session/disc");

    var bad = initial().put("slot", 3);
    expectProblem(SESSIONS, Map.of("initial", bad), "illegal-main-stat", "/initial/main_stat");
    expectProblem(SESSIONS, Map.of("initial", initial().put("level", 3)), "unreachable-disc", "/initial/substats");
  }

  @Test
  void undoDoesNotRewindDrawsAndForkKeepsRecordedOutcomes() throws Exception {
    var original = create("2.5");
    var atThree = success(SESSIONS + "/step", Map.of("session", original, "to_level", 3)).get("session");
    var undone = success(SESSIONS + "/rewind", Map.of("session", atThree, "to_event", 0)).get("session");
    assertThat(undone.get("disc")).isEqualTo(original.get("disc"));
    assertThat(undone.at("/rng/draws").asInt()).isEqualTo(1);
    var rolledAgain = success(SESSIONS + "/step", Map.of("session", undone, "to_level", 3)).get("session");
    assertThat(rolledAgain.at("/history/2/draw").asInt()).isEqualTo(1);
    assertThat(rolledAgain.at("/history/2/stat")).isNotEqualTo(atThree.at("/history/2/stat"));

    var forkRequest = Map.of("session", atThree, "to_event", 3, "fork", true);
    var forked = success(SESSIONS + "/rewind", forkRequest).get("session");
    assertThat(success(SESSIONS + "/rewind", forkRequest).get("session")).isEqualTo(forked);
    assertThat(forked.get("history")).isEqualTo(atThree.get("history"));
    assertThat(forked.at("/rng/seed")).isNotEqualTo(atThree.at("/rng/seed"));
    assertThat(forked.at("/meta/forked_from/seed")).isEqualTo(atThree.at("/rng/seed"));
    assertThat(forked.at("/rng/draws").asInt()).isZero();
    var stepped = success(SESSIONS + "/step", Map.of("session", forked, "to_level", 6)).get("session");
    assertThat(stepped.at("/history/5/draw").asInt()).isZero();
    assertThat(stepped.at("/disc/level").asInt()).isEqualTo(6);
    var bad = stepped.deepCopy();
    ((ObjectNode) bad.get("rng")).put("draws", 0);
    expectProblem(SESSIONS + "/forecast", Map.of("session", bad), "invalid-draw", "/session/history/5/draw");
    var reforked = success(SESSIONS + "/rewind", Map.of("session", stepped, "to_event", 6, "fork", true,
        "seed", 47)).get("session");
    assertThat(reforked.at("/rng/seed").asInt()).isEqualTo(47);
    assertThat(reforked.get("history")).isEqualTo(stepped.get("history"));
    var atNine = success(SESSIONS + "/step", Map.of("session", reforked, "to_level", 9)).get("session");
    assertThat(atNine.at("/history/8/draw").asInt()).isZero();
    var reset = success(SESSIONS + "/rewind", Map.of("session", atNine, "to_event", 0)).get("session");
    assertThat(reset.get("meta")).isEqualTo(reforked.get("meta"));
    var atThreeAgain = success(SESSIONS + "/step", Map.of("session", reset, "to_level", 3)).get("session");
    assertThat(atThreeAgain.at("/history/2/draw").asInt()).isEqualTo(1);
    success(SESSIONS + "/forecast", Map.of("session", atThreeAgain));
  }

  @Test
  void oldVersionReplaysAndUnsupportedVersionsListAlternatives() throws Exception {
    var old = create("2.4");
    var stepped = success(SESSIONS + "/step", Map.of("session", old, "to_level", 3)).get("session");
    assertThat(stepped.get("rules_version").asText()).isEqualTo("2.4");
    var more = success(SESSIONS + "/step", Map.of("session", stepped, "to_level", 6)).get("session");
    assertThat(more.at("/history/5/draw").asInt()).isEqualTo(1);
    assertThat(more.get("rules_version").asText()).isEqualTo("2.4");
    ((ObjectNode) stepped).put("rules_version", "99.0");
    send(SESSIONS + "/forecast", Map.of("session", stepped))
        .andExpect(status().isConflict()).andExpect(content().contentTypeCompatibleWith(PROBLEM))
        .andExpect(jsonPath("$.errors[0].pointer").value("/session/rules_version"))
        .andExpect(jsonPath("$.supported_rules_versions[0]").value("2.4"))
        .andExpect(jsonPath("$.supported_rules_versions[1]").value("2.5"));
    mvc.perform(get("/v1/disc-rules").param("version", "99.0")).andExpect(status().isConflict());
  }

  @Test
  void validatesShapeBeforeReplayIncludingSourceSpecificDrawFields() throws Exception {
    var session = (ObjectNode) success(SESSIONS + "/step", Map.of("session", create("2.5"), "to_level", 3)).get("session");
    var roll = (ObjectNode) session.at("/history/2");
    roll.remove("draw");
    send(SESSIONS + "/forecast", Map.of("session", session)).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.type").value("invalid-shape"))
        .andExpect(jsonPath("$.errors[*].pointer").value(hasItem("/session/history/2")));
    roll.put("source", "observed").put("draw", 0);
    send(SESSIONS + "/forecast", Map.of("session", session)).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.type").value("invalid-shape"));
    send(SESSIONS, Map.of("initial", initial().put("extra~/field", 1))).andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.errors[0].pointer").value("/initial/extra~0~1field"));
    send("/v1/disc-comparisons", Map.of("discs", List.of(initial()), "weights", Map.of()))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void enforcesJsonBodyLimitsEvenWithoutContentLength() throws Exception {
    for (String bad : List.of("{invalid-json", "{} {}", "{\"seed\":1,\"seed\":2}")) {
      mvc.perform(post(SESSIONS).contentType(JSON).content(bad))
          .andExpect(status().isBadRequest()).andExpect(content().contentTypeCompatibleWith(PROBLEM))
          .andExpect(jsonPath("$.type").value("malformed-json"));
    }
    mvc.perform(post(SESSIONS).contentType(JSON).content(" ".repeat(DiscJson.BODY_LIMIT + 1)))
        .andExpect(status().isPayloadTooLarge()).andExpect(jsonPath("$.type").value("body-too-large"));
    var chunked = new MockHttpServletRequest() {
      @Override
      public long getContentLengthLong() {
        return -1;
      }
    };
    chunked.setContent(new byte[DiscJson.BODY_LIMIT + 1]);
    var schema = new DiscSessionSchema();
    assertThatThrownBy(() -> schema.read(chunked, DiscSessionSchema.Request.CREATE)).isInstanceOfSatisfying(
        DiscProblem.class, problem -> assertThat(problem.status.value()).isEqualTo(413));
  }

  @Test
  void permitsCrossOriginSimulationPreflight() throws Exception {
    mvc.perform(options(SESSIONS + "/step").header("Origin", "https://client.example")
        .header("Access-Control-Request-Method", "POST").header("Access-Control-Request-Headers", "Content-Type"))
        .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "*"));
    mvc.perform(post(SESSIONS).header("Origin", "https://client.example").contentType(JSON).content("{}"))
        .andExpect(status().isUnprocessableEntity()).andExpect(header().string("Access-Control-Allow-Origin", "*"));
    mvc.perform(get("/discdrives/4").header("Origin", "https://client.example"))
        .andExpect(status().isOk()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
  }

  @Test
  void forecastEchoesTheSessionAndScoresTheTargetEvenWithoutARoll() throws Exception {
    var session = (ObjectNode) create("2.5");
    session.put("$schema", "urn:zzz:disc-simulation:session:v1").putObject("meta")
        .put("created_at", "2026-11-18T00:00:00Z").put("label", "Imported disc");
    var result = send(SESSIONS + "/forecast", Map.of("session", session, "target_level", 2, "weights",
        Map.of("crit_rate", 1), "thresholds", List.of(8.4, 8.5)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result.probability_model").value("uniform"))
        .andExpect(jsonPath("$.result.upgrades_remaining").value(0))
        .andExpect(jsonPath("$.result.outcomes.length()").value(1))
        .andExpect(jsonPath("$.result.outcomes[0].probability").value(1))
        .andExpect(jsonPath("$.result.outcomes[0].score").value(8.4))
        .andExpect(jsonPath("$.result.expected_score").value(8.4))
        .andExpect(jsonPath("$.result.p_at_least[0].probability").value(1))
        .andExpect(jsonPath("$.result.p_at_least[1].probability").value(0))
        .andReturn();
    assertThat(body(result).get("session")).isEqualTo(session);
    expectProblem(SESSIONS + "/forecast", Map.of("session", session, "thresholds", List.of(0)), "weights-required",
        "/thresholds");
    expectProblem(SESSIONS + "/forecast", Map.of("session", session, "weights", Map.of("crit_rate", new BigDecimal("1e10000"))),
        "invalid-shape", "/weights/crit_rate");
  }

  @Test
  void importsMidLevelAndCappedDiscsButRejectsImpossibleRollTotals() throws Exception {
    var cap = initial().put("level", 15);
    ((ObjectNode) cap.at("/substats/0")).put("rolls", 6);
    ((ArrayNode) cap.get("substats")).addObject().put("stat", "pen").put("rolls", 1);
    var imported = success(SESSIONS, Map.of("initial", cap, "seed", 42));
    assertThat(imported.get("complete").asBoolean()).isTrue();
    assertThat(imported.at("/session/disc")).isEqualTo(cap);
    assertThat(imported.at("/session/history")).isEmpty();
    expectProblem(SESSIONS + "/step", Map.of("session", imported.get("session"), "to_level", 15), "invalid-level", "/to_level");
    cap.put("level", 14);
    expectProblem(SESSIONS, Map.of("initial", cap), "unreachable-disc", "/initial/substats");
    var illegalB = initial().put("rarity", "B").put("level", 10);
    expectProblem(SESSIONS, Map.of("initial", illegalB), "invalid-level", "/initial/level");
    var illegalRolls = initial().put("rarity", "B").put("level", 9);
    ((ObjectNode) illegalRolls.at("/substats/0")).put("rolls", 3);
    expectProblem(SESSIONS, Map.of("initial", illegalRolls), "roll-cap-exceeded", "/initial/substats/0/rolls");
  }

  @Test
  void comparisonsRankByCapThenCurrentThenInputIndex() throws Exception {
    var low = create("2.4");
    var high = levelFourteen();
    var cappedB = levelFourteen().put("rarity", "B").put("level", 9);
    ((ObjectNode) cappedB.at("/substats/0")).put("rolls", 2);
    ((ObjectNode) cappedB.at("/substats/1")).put("rolls", 1);
    send("/v1/disc-comparisons", Map.of("discs", List.of(cappedB, low, high, low), "weights", Map.of("crit_rate", 1)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.warnings").isEmpty())
        .andExpect(jsonPath("$.results[0].current_score").value(8))
        .andExpect(jsonPath("$.results[0].expected_final_score").value(8))
        .andExpect(jsonPath("$.results[0].rank").value(4))
        .andExpect(jsonPath("$.results[1].current_score").value(6))
        .andExpect(jsonPath("$.results[1].expected_final_score").value(24))
        .andExpect(jsonPath("$.results[1].rules_version").value("2.4"))
        .andExpect(jsonPath("$.results[1].rank").value(2))
        .andExpect(jsonPath("$.results[2].current_score").value(22.8))
        .andExpect(jsonPath("$.results[2].rank").value(1))
        .andExpect(jsonPath("$.results[3].rank").value(3));
  }

  @Test
  void comparisonsRankBeforeRoundingTheExpectation() throws Exception {
    var higherCurrent = levelFourteen();
    ((ObjectNode) higherCurrent.at("/substats/1")).put("stat", "def");
    send("/v1/disc-comparisons", Map.of("discs", List.of(higherCurrent, initial()),
        "weights", Map.of("crit_rate", 1, "crit_dmg", new BigDecimal("1e-60"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.results[0].rank").value(2))
        .andExpect(jsonPath("$.results[1].rank").value(1));
  }

  private static ObjectNode levelFourteen() throws Exception {
    return (ObjectNode) DiscJson.MAPPER.readTree("""
        {"rarity":"S","slot":4,"main_stat":"crit_rate","level":14,
         "substats":[{"stat":"atk_pct","rolls":4},{"stat":"crit_dmg","rolls":2},{"stat":"hp","rolls":1},{"stat":"pen","rolls":1}]}
        """);
  }

  private JsonNode create(String version) throws Exception {
    return success(SESSIONS, Map.of("initial", initial(), "seed", 8856, "rules_version", version)).get("session");
  }

  private static ObjectNode initial() throws Exception {
    return (ObjectNode) DiscJson.MAPPER.readTree("""
        {"rarity":"S","slot":4,"main_stat":"crit_rate","level":0,
         "substats":[{"stat":"atk_pct","rolls":1},{"stat":"crit_dmg","rolls":1},{"stat":"hp","rolls":1}]}
        """);
  }

  private ResultActions send(String path, Object input) throws Exception {
    return mvc.perform(post(path).contentType(JSON).content(DiscJson.MAPPER.writeValueAsBytes(input)));
  }

  private JsonNode success(String path, Object input) throws Exception {
    return body(send(path, input).andExpect(status().isOk()).andReturn());
  }

  private void expectProblem(String path, Object input, String type, String pointer) throws Exception {
    send(path, input).andExpect(status().isUnprocessableEntity()).andExpect(content().contentTypeCompatibleWith(PROBLEM))
        .andExpect(jsonPath("$.type").value(type)).andExpect(jsonPath("$.errors[0].pointer").value(pointer));
  }

  private static JsonNode body(MvcResult result) throws Exception {
    return DiscJson.MAPPER.readTree(result.getResponse().getContentAsByteArray());
  }
}
