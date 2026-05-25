package com.demo.commons.error.mapper;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.exceptions.GenericException;
import com.demo.commons.error.messages.MessageResolver;
import com.demo.commons.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
@RequiredArgsConstructor
public class GenericExceptionResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;
  private final MessageResolver messageResolver;
  private final DefaultErrorMapper defaultErrorMapper;

  @Override
  public boolean support(Throwable throwable) {
    return Objects.nonNull(throwable) && throwable instanceof GenericException;
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public Response.Status status(Throwable throwable) {
    GenericException exception = (GenericException) throwable;
    return exception.getHttpStatus();
  }

  @Override
  public ErrorDto map(Throwable throwable) {
    GenericException exception = (GenericException) throwable;

    return Optional.ofNullable(exception)
        .filter(error -> Objects.nonNull(exception.getError()))
        .map(GenericException::getError)
        .map(error -> {

          error.setCode(selectCode(error));
          error.setMessage(selectMessage(error));

          return error;
        })
        .orElse(defaultErrorMapper.defaultError());
  }

  private String selectCode(ErrorDto error) {
    String defaultCode = properties().error().defaultCode();

    return Optional.ofNullable(error)
        .filter(err -> Objects.nonNull(err.getCode()))
        .map(ErrorDto::getCode)
        .orElse(defaultCode);
  }

  private String selectMessage(ErrorDto error) {
    String defaultMessage = properties().error().defaultMessage();

    Supplier<String> messageSupplier = () -> Optional.ofNullable(error)
        .filter(err -> Objects.nonNull(err.getCode()))
        .map(err -> messageResolver.messages().getOrDefault(error.getCode(), defaultMessage))
        .orElse(defaultMessage);

    return this.mapMessage(messageSupplier);
  }
}
