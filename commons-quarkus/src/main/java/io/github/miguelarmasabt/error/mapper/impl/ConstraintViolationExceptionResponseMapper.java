package io.github.miguelarmasabt.error.mapper.impl;

import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ConstraintViolationExceptionResponseMapper implements ExceptionResponseMapper {

  public static final String CONSTRAINT_VIOLATION = "constraint_violation";

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof ConstraintViolationException;
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
  public Optional<String> selectCode(Throwable throwable) {
    return Optional.of(CONSTRAINT_VIOLATION);
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    ConstraintViolationException exception = (ConstraintViolationException) throwable;

    return Optional.of(exception)
        .map(Throwable::getMessage)
        .filter(message -> !message.isBlank())
        .or(() -> configuredMessageResolver.resolve(CONSTRAINT_VIOLATION, locale))
        .map(selectedMessage -> this.mapMessage(selectedMessage, true, locale));
  }
}
