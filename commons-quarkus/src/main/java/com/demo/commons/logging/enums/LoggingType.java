package com.demo.commons.logging.enums;

import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.logging.LoggingTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum LoggingType {

  ERROR("error", "Error"),
  REST_SERVER_REQ("rest.server.req", "REST server request"),
  REST_SERVER_RES("rest.server.res", "REST server response"),
  REST_CLIENT_REQ("rest.client.req", "REST client request"),
  REST_CLIENT_RES("rest.client.res", "REST client response");

  private final String code;
  private final String message;

  public static boolean isLoggerPresent(ConfigurationBaseProperties properties, LoggingType loggingType) {
    return properties.logging()
        .filter(logging -> Objects.nonNull(logging.loggingType()))
        .map(LoggingTemplate::loggingType)
        .filter(loggers -> loggers.containsKey(loggingType.getCode()))
        .map(loggers -> loggers.get(loggingType.getCode()))
        .orElse(Boolean.TRUE);
  }
}