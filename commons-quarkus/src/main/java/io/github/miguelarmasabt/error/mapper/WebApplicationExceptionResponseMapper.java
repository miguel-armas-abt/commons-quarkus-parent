package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
@RequiredArgsConstructor
public class WebApplicationExceptionResponseMapper implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;

  @Override
  public boolean support(Throwable throwable) {
    return Objects.nonNull(throwable) && throwable instanceof WebApplicationException;
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public Response.Status status(Throwable throwable) {
    WebApplicationException exception = (WebApplicationException) throwable;
    return Response.Status.fromStatusCode(exception.getResponse().getStatus());
  }

  @Override
  public ErrorDto map(Throwable throwable) {
    WebApplicationException exception = (WebApplicationException) throwable;
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
