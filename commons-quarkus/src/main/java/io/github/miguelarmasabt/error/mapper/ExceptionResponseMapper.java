package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.properties.ProjectType;
import jakarta.ws.rs.core.Response;

import java.util.function.Supplier;

public interface ExceptionResponseMapper {

  boolean support(Throwable throwable);

  ConfigurationBaseProperties properties();

  Response.Status status(Throwable throwable);

  ErrorDto map(Throwable throwable);

  default String mapMessage(Supplier<String> messageSupplier) {
    String defaultMessage = properties().error().defaultMessage();

    if (ProjectType.BACKEND_FOR_FRONTEND.equals(properties().projectType()))
      return defaultMessage;

    return messageSupplier.get();
  }
}
