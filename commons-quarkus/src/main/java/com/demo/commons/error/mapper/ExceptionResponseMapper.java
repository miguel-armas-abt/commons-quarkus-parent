package com.demo.commons.error.mapper;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.ProjectType;
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
