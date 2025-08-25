package io.github.miguelarmasabt.error.mapper.impl;

import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.vertx.core.impl.NoStackTraceTimeoutException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ServiceUnavailableExceptionResponseMapper implements ExceptionResponseMapper {

  public static final String CONNECT_TIMEOUT = "connect_timeout";
  public static final String REFUSED_CONNECTION = "refused_connection";

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof ProcessingException;
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
    return Response.Status.SERVICE_UNAVAILABLE;
  }

  @Override
  public Optional<String> selectCode(Throwable throwable) {
    ProcessingException exception = (ProcessingException) throwable;

    if (isTimeout(exception))
      return Optional.of(CONNECT_TIMEOUT);

    if (isRefusedConnection(exception))
      return Optional.of(REFUSED_CONNECTION);

    return Optional.empty();
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    ProcessingException exception = (ProcessingException) throwable;

    return selectCode(exception)
        .flatMap(code -> configuredMessageResolver.resolve(code, locale))
        .map(selectedMessage -> this.mapMessage(selectedMessage, true, locale))
        .or(() -> Optional.ofNullable(exception.getMessage())
            .map(selectedMessage -> this.mapMessage(selectedMessage, false, locale)));
  }

  private static boolean isTimeout(ProcessingException throwable) {
    Throwable cause = throwable.getCause();
    return cause instanceof NoStackTraceTimeoutException
        || cause instanceof SocketTimeoutException;
  }

  private static boolean isRefusedConnection(ProcessingException throwable) {
    return throwable.getCause() instanceof ConnectException;
  }
}