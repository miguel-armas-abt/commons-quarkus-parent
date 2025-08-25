package io.github.miguelarmasabt.rest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentValidator {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String CONTENT_LENGTH = "Content-Length";

  private static final String JSON = "json";

  public static boolean isJson(Map<String, String> headers) {
    return HeaderExtractor.firstHeader(headers, CONTENT_TYPE)
        .map(String::toLowerCase)
        .map(value -> value.contains(JSON))
        .orElse(false);
  }

  public static Optional<Long> getContentLength(Map<String, String> headers) {
    return HeaderExtractor.firstHeader(headers, CONTENT_LENGTH)
        .flatMap(value -> {
          try {
            return Optional.of(Long.parseLong(value.trim()));
          } catch (Exception exception) {
            return Optional.empty();
          }
        });
  }
}
