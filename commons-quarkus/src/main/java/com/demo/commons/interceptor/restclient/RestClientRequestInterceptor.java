package com.demo.commons.interceptor.restclient;

import com.demo.commons.constants.Symbol;
import com.demo.commons.logging.enums.LoggingType;
import com.demo.commons.logging.obfuscation.ObfuscationTemplateProvider;
import com.demo.commons.logging.obfuscation.body.BodyObfuscator;
import com.demo.commons.logging.obfuscation.header.HeaderObfuscator;
import com.demo.commons.logging.restclient.RestClientConstant;
import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.logging.ObfuscationTemplate;
import com.demo.commons.restclient.utils.HeadersExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.util.Optional;

public class RestClientRequestInterceptor implements ClientRequestFilter {

  private final ConfigurationBaseProperties properties;
  private final ObfuscationTemplate obfuscation;
  private final ObjectMapper objectMapper;

  public RestClientRequestInterceptor(
      ConfigurationBaseProperties properties,
      ObjectMapper objectMapper,
      ObfuscationTemplateProvider provider) {

    this.properties = properties;
    this.objectMapper = objectMapper;
    this.obfuscation = provider.get();
  }

  private static final Logger log = Logger.getLogger(RestClientRequestInterceptor.class);

  @Override
  public void filter(ClientRequestContext requestContext) {
    generateTrace(requestContext);
  }

  private void generateTrace(ClientRequestContext requestContext) {
    boolean isLoggerPresent = LoggingType.isLoggerPresent(properties, LoggingType.REST_CLIENT_REQ);
    if (isLoggerPresent) {
      String uri = requestContext.getUri().toString();
      MDC.put(LoggingType.REST_CLIENT_REQ.getCode() + RestClientConstant.METHOD, requestContext.getMethod());
      MDC.put(LoggingType.REST_CLIENT_REQ.getCode() + RestClientConstant.URI, uri);

      log.info("--> " + requestContext.getMethod() + Symbol.SPACE + uri);

      Optional.ofNullable(HeadersExtractor.extractHeadersAsMap(requestContext.getHeaders()))
          .ifPresent(headers -> log.info("Request headers --> " + HeaderObfuscator.process(obfuscation, headers)));

      Optional.ofNullable(requestContext.getEntity())
          .map(this::serializeJson)
          .ifPresent(body -> log.info("Request body --> " + BodyObfuscator.process(obfuscation, body)));
    }
  }

  private String serializeJson(Object payload) {
    try {
      return (payload instanceof String)
          ? (String) payload
          : objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException exception) {
      throw new JsonException(exception.getMessage());
    }
  }

}