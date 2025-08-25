package io.github.miguelarmasabt.error.mapper.impl;

import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class WebApplicationExceptionResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof WebApplicationException;
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
    WebApplicationException exception = (WebApplicationException) throwable;
    return Response.Status.fromStatusCode(exception.getResponse().getStatus());
  }

  @Override
  public Optional<String> selectCode(Throwable throwable) {
    return Optional.of(properties.error().defaultCode());
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    return Optional.ofNullable(configuredMessageResolver.defaultMessage(locale));
  }
}
