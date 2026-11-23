package com.jplopez.zzz.discsimulation;

import java.io.IOException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;

import jakarta.servlet.http.HttpServletRequest;

/** Uses the same draft 2020-12 document for published contracts and input validation. */
@Component
final class DiscSessionSchema {

  enum Request {
    CREATE("CreateSessionRequest"), STEP("StepRequest"), REWIND("RewindRequest"),
    FORECAST("ForecastRequest"), COMPARISON("ComparisonRequest");

    private final String definition;

    Request(String definition) {
      this.definition = definition;
    }
  }

  private final DiscJson.Document document;
  private final Map<Request, JsonSchema> validators;

  DiscSessionSchema() throws IOException {
    document = DiscJson.resource("schemas/disc-session.json");
    ObjectNode schema = (ObjectNode) DiscJson.MAPPER.readTree(document.bytes());
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    SchemaValidatorsConfig config = SchemaValidatorsConfig.builder()
        .pathType(PathType.JSON_POINTER).locale(Locale.ROOT).formatAssertionsEnabled(true).build();
    var compiled = new EnumMap<Request, JsonSchema>(Request.class);
    for (Request request : Request.values()) {
      ObjectNode root = schema.deepCopy().put("$ref", "#/$defs/" + request.definition);
      JsonSchema validator = factory.getSchema(root, config);
      validator.initializeValidators();
      compiled.put(request, validator);
    }
    validators = Map.copyOf(compiled);
  }

  DiscJson.Document document() {
    return document;
  }

  JsonNode read(HttpServletRequest request, Request shape) {
    if (request.getContentLengthLong() > DiscJson.BODY_LIMIT) {
      throw DiscProblem.tooLarge();
    }
    JsonNode document;
    try {
      byte[] bytes = request.getInputStream().readNBytes(DiscJson.BODY_LIMIT + 1);
      if (bytes.length > DiscJson.BODY_LIMIT) {
        throw DiscProblem.tooLarge();
      }
      document = DiscJson.MAPPER.readTree(bytes);
    } catch (IOException e) {
      throw DiscProblem.malformed();
    }
    if (document == null || document.isMissingNode()) {
      throw DiscProblem.malformed();
    }
    validateDecimals(document, "");
    var errors = validators.get(shape).validate(document);
    if (!errors.isEmpty()) {
      var issues = errors.stream()
          .map(error -> {
            String pointer = error.getInstanceLocation().toString();
            if ("additionalProperties".equals(error.getType()) && error.getProperty() != null) {
              pointer = childPointer(pointer, error.getProperty());
            }
            return new DiscProblem.Issue(pointer, error.getMessage());
          })
          .sorted(Comparator.comparing(DiscProblem.Issue::pointer).thenComparing(DiscProblem.Issue::message))
          .limit(20).toList();
      throw DiscProblem.shape(issues);
    }
    return document;
  }

  private static void validateDecimals(JsonNode node, String pointer) {
    // The byte limit alone does not bound the scale of a decimal with an exponent.
    if (node.isFloatingPointNumber() && (!node.isBigDecimal() || Math.abs((long) node.decimalValue().scale()) > 64)) {
      throw DiscProblem.invalid("invalid-shape", pointer, "Numbers must be finite with a scale between -64 and 64");
    }
    if (node.isObject()) {
      node.properties().forEach(entry -> validateDecimals(entry.getValue(), childPointer(pointer, entry.getKey())));
    } else if (node.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        validateDecimals(node.get(i), pointer + "/" + i);
      }
    }
  }

  private static String childPointer(String parent, String property) {
    return parent + "/" + property.replace("~", "~0").replace("/", "~1");
  }
}
