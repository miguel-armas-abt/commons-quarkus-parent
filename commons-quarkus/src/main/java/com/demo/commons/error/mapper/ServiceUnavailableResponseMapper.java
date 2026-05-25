package com.demo.commons.error.mapper;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.dto.ErrorOrigin;
import com.demo.commons.error.messages.MessageResolver;
import com.demo.commons.properties.ConfigurationBaseProperties;
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

import static com.demo.commons.error.messages.SystemErrorMessage.CONNECT_TIMEOUT;
import static com.demo.commons.error.messages.SystemErrorMessage.REFUSED_CONNECTION;

@ApplicationScoped
@RequiredArgsConstructor
public class ServiceUnavailableResponseMapper implements ExceptionResponseMapper {

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
          .getOrDefault(CONNECT_TIMEOUT.name(), message);
    }

    if (isRefusedConnection(exception)) {
      message = messageResolver
          .messages()
          .getOrDefault(REFUSED_CONNECTION.name(), message);
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
