package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.error.messages.MessageResolver;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.error.messages.SystemErrorMessage;
import io.vertx.core.impl.NoStackTraceTimeoutException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
@RequiredArgsConstructor
public class ServiceUnavailableExceptionResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;
  private final MessageResolver messageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return Objects.nonNull(throwable)
        && throwable instanceof ProcessingException;
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public Response.Status status(Throwable throwable) {
    return Response.Status.SERVICE_UNAVAILABLE;
  }

  @Override
  public ErrorDto map(Throwable throwable) {
    ProcessingException exception = (ProcessingException) throwable;

    ErrorDto error = new ErrorDto();

    error.setOrigin(ErrorOrigin.OWN);
    error.setCode(properties().error().defaultCode());

    String message = throwable.getMessage();

    if (isTimeout(exception)) {
      message = messageResolver
          .messages()
          .getOrDefault(SystemErrorMessage.CONNECT_TIMEOUT.name(), message);
    }

    if (isRefusedConnection(exception)) {
      message = messageResolver
          .messages()
          .getOrDefault(SystemErrorMessage.REFUSED_CONNECTION.name(), message);
    }

    error.setMessage(selectMessage(message));

    return error;
  }

  private String selectMessage(String message) {
    Supplier<String> messageSupplier = () -> Optional.ofNullable(message)
        .orElse(properties().error().defaultMessage());

    return this.mapMessage(messageSupplier);
  }

  private static boolean isTimeout(ProcessingException throwable) {
    return Optional.ofNullable(throwable)
        .map(Throwable::getCause)
        .filter(cause ->
            cause instanceof NoStackTraceTimeoutException
                || cause instanceof SocketTimeoutException)
        .isPresent();
  }

  private static boolean isRefusedConnection(ProcessingException throwable) {
    return Optional.ofNullable(throwable)
        .map(Throwable::getCause)
        .filter(ConnectException.class::isInstance)
        .isPresent();
  }
}
