package com.demo.commons.obfuscation;

import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.rest.RestProperties;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.demo.commons.constants.Strings.COMMA;
import static com.demo.commons.constants.Strings.EQUALS;
import static com.demo.commons.constants.Strings.SPACE;

@ApplicationScoped
public class HeaderObfuscator {

  private final RestProperties.Obfuscation obfuscation;

  public HeaderObfuscator(ConfigurationBaseProperties properties) {
    this.obfuscation = properties.rest().obfuscation();
  }

  private static String obfuscateHeader(Map.Entry<String, String> header,
                                        Set<String> sensitiveHeaders) {
    String key = header.getKey();
    return !sensitiveHeaders.contains(key)
        ? key + EQUALS + header.getValue()
        : Optional.ofNullable(header.getValue())
        .map(value -> key + EQUALS + partiallyObfuscate(value))
        .orElse(key + EQUALS + "null");
  }

  private static String partiallyObfuscate(String value) {
    return value.length() <= 6
        ? MaskingWildcard.OBFUSCATION_MASK
        : value.substring(0, 3) + MaskingWildcard.OBFUSCATION_MASK + value.substring(value.length() - 3);
  }

  public String process(Map<String, String> headers) {

    return headers
        .entrySet()
        .stream()
        .map(entry -> obfuscateHeader(entry, obfuscation.headers()))
        .collect(Collectors.joining(COMMA + SPACE));
  }
}