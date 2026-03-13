package com.demo.commons.logging.enums;

import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.logging.LoggingTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum LoggingType {

  ERROR("error"),
  REST_SERVER_REQ("rest.server.req"),
  REST_SERVER_RES("rest.server.res"),
  REST_CLIENT_REQ("rest.client.req"),
  REST_CLIENT_RES("rest.client.res");

  private final String code;

  public static boolean isLoggerPresent(ConfigurationBaseProperties properties, LoggingType loggingType) {
    return properties.logging()
        .filter(logging -> Objects.nonNull(logging.loggingType()))
        .map(LoggingTemplate::loggingType)
        .filter(loggers -> loggers.containsKey(loggingType.getCode()))
        .map(loggers -> loggers.get(loggingType.getCode()))
        .orElse(Boolean.TRUE);
  }
}