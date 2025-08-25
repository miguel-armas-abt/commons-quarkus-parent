package io.github.miguelarmasabt.error.helper;

import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
@RequiredArgsConstructor
public class ConfiguredMessageResolver {

  private static final String APPLICATION_ERRORS_BUNDLE = "errors.errors";
  private static final String DEFAULT_ERRORS_BUNDLE = "errors.default_errors";
  private static final Locale FALLBACK_LOCALE = Locale.ENGLISH;

  private final ConfigurationBaseProperties properties;

  private static final ResourceBundle.Control NO_SYSTEM_LOCALE_FALLBACK =
      ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

  private final ConcurrentMap<Locale, Map<String, String>> messagesByLocale =
      new ConcurrentHashMap<>();

  public Optional<String> resolve(String code, Locale locale) {
    String normalizedCode = normalizeCode(code);

    if (Objects.isNull(normalizedCode))
      return Optional.empty();

    return Optional.ofNullable(messages(locale).get(normalizedCode));
  }

  public Optional<String> resolve(String code, Locale locale, Object... arguments) {
    return resolve(code, locale)
        .map(message -> format(message, locale, arguments));
  }

  public String defaultMessage(Locale locale) {
    return messages(locale).getOrDefault(
        properties.error().defaultCode(),
        messages(FALLBACK_LOCALE).get(properties.error().defaultCode())
    );
  }

  public Map<String, String> messages(Locale locale) {
    Locale resolvedLocale = Optional.ofNullable(locale)
        .orElse(FALLBACK_LOCALE);

    return messagesByLocale.computeIfAbsent(resolvedLocale, this::loadMessages);
  }

  private Map<String, String> loadMessages(Locale locale) {
    Map<String, String> resolvedMessages = new HashMap<>();

    resolvedMessages.putAll(loadBundle(DEFAULT_ERRORS_BUNDLE, locale));
    resolvedMessages.putAll(loadBundle(APPLICATION_ERRORS_BUNDLE, locale));

    if (!resolvedMessages.containsKey(properties.error().defaultCode())
        && !FALLBACK_LOCALE.equals(locale)) {
      resolvedMessages.putAll(loadBundle(DEFAULT_ERRORS_BUNDLE, FALLBACK_LOCALE));
      resolvedMessages.putAll(loadBundle(APPLICATION_ERRORS_BUNDLE, FALLBACK_LOCALE));
    }

    return Map.copyOf(resolvedMessages);
  }

  private Map<String, String> loadBundle(String baseName, Locale locale) {
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(
          baseName,
          locale,
          Thread.currentThread().getContextClassLoader(),
          NO_SYSTEM_LOCALE_FALLBACK
      );

      Map<String, String> messages = new HashMap<>();

      bundle.keySet().forEach(key ->
          messages.put(normalizeCode(key), bundle.getString(key))
      );

      return messages;

    } catch (MissingResourceException exception) {
      return Collections.emptyMap();
    }
  }

  private String format(String message, Locale locale, Object... arguments) {
    if (Objects.isNull(arguments) || arguments.length == 0)
      return message;

    MessageFormat formatter = new MessageFormat(message, locale);
    return formatter.format(arguments);
  }

  private String normalizeCode(String code) {
    return Optional.ofNullable(code)
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .orElse(null);
  }
}