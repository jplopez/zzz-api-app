package com.jplopez.zzz.discsimulation;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** Expected simulation failures, rendered as RFC 9457 without leaking the request body. */
final class DiscProblem extends RuntimeException {

  record Issue(String pointer, String message) {}

  final HttpStatus status;
  private final String type;
  private final List<Issue> errors;
  private final List<String> supportedVersions;
  final Integer retryAfter;

  private DiscProblem(HttpStatus status, String type, String detail, List<Issue> errors,
                      List<String> supportedVersions, Integer retryAfter) {
    super(detail);
    this.status = status;
    this.type = type;
    this.errors = errors;
    this.supportedVersions = supportedVersions;
    this.retryAfter = retryAfter;
  }

  static DiscProblem malformed() {
    return new DiscProblem(HttpStatus.BAD_REQUEST, "malformed-json", "Expected one well-formed JSON document",
        List.of(), List.of(), null);
  }

  static DiscProblem tooLarge() {
    return new DiscProblem(HttpStatus.PAYLOAD_TOO_LARGE, "body-too-large", "Request bodies are limited to 32 KiB",
        List.of(), List.of(), null);
  }

  static DiscProblem invalid(String type, String pointer, String message) {
    return new DiscProblem(HttpStatus.UNPROCESSABLE_ENTITY, type, message, List.of(new Issue(pointer, message)),
        List.of(), null);
  }

  static DiscProblem shape(List<Issue> errors) {
    return new DiscProblem(HttpStatus.UNPROCESSABLE_ENTITY, "invalid-shape", "The document does not match the schema",
        errors, List.of(), null);
  }

  static DiscProblem unsupported(String pointer, List<String> versions) {
    String message = "The requested rules version is not supported";
    List<Issue> errors = pointer == null ? List.of() : List.of(new Issue(pointer, message));
    return new DiscProblem(HttpStatus.CONFLICT, "unsupported-rules-version", message, errors, versions, null);
  }

  static DiscProblem rateLimited(int retryAfter) {
    return new DiscProblem(HttpStatus.TOO_MANY_REQUESTS, "rate-limited", "Too many simulation requests",
        List.of(), List.of(), retryAfter);
  }

  ObjectNode body() {
    ObjectNode result = DiscJson.MAPPER.createObjectNode();
    result.put("type", type);
    result.put("title", status.getReasonPhrase());
    result.put("status", status.value());
    result.put("detail", getMessage());
    if (!errors.isEmpty()) {
      result.set("errors", DiscJson.MAPPER.valueToTree(errors));
    }
    if (!supportedVersions.isEmpty()) {
      result.set("supported_rules_versions", DiscJson.MAPPER.valueToTree(supportedVersions));
    }
    if (retryAfter != null) {
      result.put("retry_after", retryAfter);
    }
    return result;
  }
}
