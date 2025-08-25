package io.github.miguelarmasabt.error.mapper.impl;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.AbstractInvalidFormatMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@ApplicationScoped
@RequiredArgsConstructor
public class InvalidEnumFormatExceptionResponseMapper
    extends AbstractInvalidFormatMapper
    implements ExceptionResponseMapper {

  public static final String INVALID_ENUM = "invalid_enum";

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return asInvalidFormatException(throwable)
        .map(InvalidFormatException::getTargetType)
        .map(Class::getEnumConstants)
        .map(Objects::nonNull)
        .orElse(Boolean.FALSE);
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public ConfiguredMessageResolver configuredMessageResolver() {
    return configuredMessageResolver;
  }

  @Override
  public Response.Status selectStatus(Throwable throwable) {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  protected Map<Class<?>, Predicate<String>> validators() {
    return Map.of();
  }

  @Override
  public Optional<String> selectCode(Throwable throwable) {
    return Optional.of(INVALID_ENUM);
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    InvalidFormatException exception = (InvalidFormatException) throwable;

    return extractFieldName(exception)
        .flatMap(fieldName -> {
          String messageArguments = Arrays.toString(exception.getTargetType().getEnumConstants());
          return configuredMessageResolver.resolve(INVALID_ENUM, locale, fieldName, messageArguments);
        })
        .map(selectedMessage -> this.mapMessage(selectedMessage, true, locale));
  }
}
