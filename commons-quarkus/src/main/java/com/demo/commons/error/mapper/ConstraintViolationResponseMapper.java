package com.demo.commons.error.mapper;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.dto.ErrorOrigin;
import com.demo.commons.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
@RequiredArgsConstructor
public class ConstraintViolationResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;

  @Override
  public boolean support(Throwable throwable) {
    return Objects.nonNull(throwable) && throwable instanceof ConstraintViolationException;
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public Response.Status status(Throwable throwable) {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  public ErrorDto map(Throwable throwable) {
    ConstraintViolationException exception = (ConstraintViolationException) throwable;
    ErrorDto error = new ErrorDto();

    error.setOrigin(ErrorOrigin.OWN);
    error.setCode(properties().error().defaultCode());
    String message = exception.getMessage();

    error.setMessage(selectMessage(message));

    return error;
  }

  private String selectMessage(String message) {
    Supplier<String> messageSupplier = () -> Optional.ofNullable(message)
        .orElse(properties().error().defaultMessage());

    return this.mapMessage(messageSupplier);
  }
}
