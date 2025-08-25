package io.github.miguelarmasabt.error.mapper.impl;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.exceptions.GenericException;
import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.properties.error.ErrorProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class GenericExceptionResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof GenericException;
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
    GenericException exception = (GenericException) throwable;
    return selectCode(throwable)
        .flatMap(code -> Optional.ofNullable(properties.error().rules().get(code)))
        .map(errorRule -> Response.Status.fromStatusCode(errorRule.httpCode()))
        .orElseGet(() -> Optional.ofNullable(exception.getHttpStatus())
            .orElse(Response.Status.INTERNAL_SERVER_ERROR));
  }

  @Override
  public Optional<String> selectCode(Throwable throwable) {
    GenericException exception = (GenericException) throwable;
    return Optional.ofNullable(exception.getError().getCode());
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    GenericException exception = (GenericException) throwable;
    return selectCode(exception)
        .flatMap(code -> Optional.ofNullable(exception.getError().getMessageArguments())
            .flatMap(arguments -> configuredMessageResolver.resolve(code, locale, arguments))
            .or(() -> configuredMessageResolver.resolve(code, locale))
            .or(() -> Optional.ofNullable(exception.getError()).map(ErrorDto::getMessage))
            .map(selectedMessage -> {
              boolean exposeMessage = Optional.ofNullable(properties.error().rules().get(code))
                  .map(ErrorProperties.ErrorRule::exposeMessage)
                  .orElse(Boolean.FALSE);
              return this.mapMessage(selectedMessage, exposeMessage, locale);
            }));
  }
}