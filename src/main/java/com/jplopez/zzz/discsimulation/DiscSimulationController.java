package com.jplopez.zzz.discsimulation;

import static com.jplopez.zzz.discsimulation.DiscSimulation.SESSION_PATH;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.jplopez.zzz.discsimulation.DiscDocuments.ComparisonRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.ComparisonResult;
import com.jplopez.zzz.discsimulation.DiscDocuments.CreateRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.ForecastRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.RewindRequest;
import com.jplopez.zzz.discsimulation.DiscDocuments.StepRequest;
import com.jplopez.zzz.discsimulation.DiscSessionSchema.Request;

import jakarta.servlet.http.HttpServletRequest;

@RestController
final class DiscSimulationController {

  static final String RULES_PATH = "/v1/disc-rules";
  static final String SCHEMA_PATH = "/v1/schemas/disc-session.json";
  static final String COMPARISON_PATH = "/v1/disc-comparisons";
  static final String OPENAPI_PATH = "/v1/openapi.yaml";
  static final String RULES_VERSION_ATTRIBUTE = DiscSimulationController.class.getName() + ".rulesVersion";
  private static final String JSON = MediaType.APPLICATION_JSON_VALUE;
  private static final String SCHEMA_JSON = "application/schema+json";
  private static final String YAML = "application/yaml";
  private static final String IMMUTABLE = "public, max-age=31536000, immutable";

  private final DiscRuleCatalog rules;
  private final DiscSessionSchema schema;
  private final DiscSimulation simulation;
  private final DiscJson.Document openApi;

  DiscSimulationController(DiscRuleCatalog rules, DiscSessionSchema schema, DiscSimulation simulation) throws IOException {
    this.rules = rules;
    this.schema = schema;
    this.simulation = simulation;
    openApi = DiscJson.resource("openapi.yaml");
  }

  @GetMapping(value = RULES_PATH, produces = JSON)
  public ResponseEntity<byte[]> rules(@RequestParam(name = "version", required = false) String version,
                                      WebRequest request, HttpServletRequest servletRequest) {
    var entry = rules.entry(version, null);
    servletRequest.setAttribute(RULES_VERSION_ATTRIBUTE, entry.rules().rulesVersion());
    return cached(entry.document(), JSON, version == null ? "public, no-cache" : IMMUTABLE, request);
  }

  @GetMapping(value = SCHEMA_PATH, produces = SCHEMA_JSON)
  public ResponseEntity<byte[]> schema(WebRequest request) {
    return cached(schema.document(), SCHEMA_JSON, IMMUTABLE, request);
  }

  @GetMapping(value = OPENAPI_PATH, produces = YAML)
  public ResponseEntity<byte[]> openApi(WebRequest request) {
    return cached(openApi, YAML, "public, no-cache", request);
  }

  @PostMapping(value = SESSION_PATH, consumes = JSON, produces = JSON)
  public JsonNode create(HttpServletRequest request) {
    var result = simulation.create(DiscJson.bind(schema.read(request, Request.CREATE), CreateRequest.class));
    return response(request, result, result.session().rulesVersion());
  }

  @PostMapping(value = SESSION_PATH + "/step", consumes = JSON, produces = JSON)
  public JsonNode step(HttpServletRequest request) {
    var result = simulation.step(DiscJson.bind(schema.read(request, Request.STEP), StepRequest.class));
    return response(request, result, result.session().rulesVersion());
  }

  @PostMapping(value = SESSION_PATH + "/rewind", consumes = JSON, produces = JSON)
  public JsonNode rewind(HttpServletRequest request) {
    var result = simulation.rewind(DiscJson.bind(schema.read(request, Request.REWIND), RewindRequest.class));
    return response(request, result, result.session().rulesVersion());
  }

  @PostMapping(value = SESSION_PATH + "/forecast", consumes = JSON, produces = JSON)
  public JsonNode forecast(HttpServletRequest request) {
    JsonNode body = schema.read(request, Request.FORECAST);
    var input = DiscJson.bind(body, ForecastRequest.class);
    return response(request, simulation.forecast(input, body.get("session")), input.session().rulesVersion());
  }

  @PostMapping(value = COMPARISON_PATH, consumes = JSON, produces = JSON)
  public JsonNode compare(HttpServletRequest request) {
    var result = simulation.compare(DiscJson.bind(schema.read(request, Request.COMPARISON), ComparisonRequest.class));
    String versions = result.results().stream().map(ComparisonResult::rulesVersion).distinct().sorted()
        .collect(Collectors.joining(","));
    return response(request, result, versions);
  }

  @ExceptionHandler(DiscProblem.class)
  ResponseEntity<JsonNode> problem(DiscProblem problem) {
    var response = ResponseEntity.status(problem.status).contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .header(HttpHeaders.CACHE_CONTROL, "no-store");
    return response.body(problem.body());
  }

  private static JsonNode response(HttpServletRequest request, Object body, String version) {
    request.setAttribute(RULES_VERSION_ATTRIBUTE, version);
    return DiscJson.MAPPER.valueToTree(body);
  }

  private static ResponseEntity<byte[]> cached(DiscJson.Document document, String contentType, String cacheControl,
                                                WebRequest request) {
    boolean unchanged = request.checkNotModified(document.etag());
    var response = ResponseEntity.status(unchanged ? HttpStatus.NOT_MODIFIED : HttpStatus.OK)
        .eTag(document.etag()).header(HttpHeaders.CACHE_CONTROL, cacheControl);
    return unchanged ? response.build() : response.contentType(MediaType.parseMediaType(contentType))
        .body(document.bytes());
  }
}
