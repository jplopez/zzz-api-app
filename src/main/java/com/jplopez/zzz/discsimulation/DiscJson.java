package com.jplopez.zzz.discsimulation;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

/** Keeps strict simulation parsing and serialization separate from the catalog API. */
final class DiscJson {

  static final int BODY_LIMIT = 32 * 1024;
  static final ObjectMapper MAPPER = JsonMapper.builder(JsonFactory.builder()
      .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
      .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(32)
          .maxNumberLength(128).maxStringLength(BODY_LIMIT).build())
      .build())
      .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
      .serializationInclusion(JsonInclude.Include.NON_NULL)
      .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
      .build();

  private DiscJson() {}

  static <T> T bind(JsonNode node, Class<T> type) {
    try {
      return MAPPER.treeToValue(node, type);
    } catch (JsonProcessingException e) {
      // The schema is checked before binding. Never expose values echoed by the parser.
      throw DiscProblem.invalid("invalid-shape", "", "The document could not be read as " + type.getSimpleName());
    }
  }

  static Document resource(String name) throws IOException {
    try (var stream = new ClassPathResource("disc-simulation/" + name).getInputStream()) {
      return Document.of(stream.readAllBytes());
    }
  }

  static MessageDigest sha256() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 unavailable", e);
    }
  }

  record Document(byte[] bytes, String etag) {
    static Document of(byte[] bytes) {
      return new Document(bytes, "\"" + HexFormat.of().formatHex(sha256().digest(bytes)) + "\"");
    }
  }
}
