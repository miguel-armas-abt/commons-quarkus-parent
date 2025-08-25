package io.github.miguelarmasabt.rest.utils;

import io.vertx.core.MultiMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderExtractor {

  public static Map<String, String> asMap(MultiMap headers) {
    return Optional.ofNullable(headers)
        .map(h -> h.names().stream()
            .filter(Objects::nonNull)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.toMap(
                Function.identity(),
                k -> Optional.ofNullable(h.get(k)).orElse(""),
                (a, b) -> a,
                LinkedHashMap::new
            )))
        .map(Collections::unmodifiableMap)
        .orElseGet(Collections::emptyMap);
  }

  public static Optional<String> firstHeader(Map<String, String> headers, String key) {
    return headers.entrySet()
        .stream()
        .filter(entry -> Objects.nonNull(entry.getKey()) && entry.getKey().equalsIgnoreCase(key))
        .findFirst()
        .map(Map.Entry::getValue);
  }
}
