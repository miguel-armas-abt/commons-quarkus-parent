package com.demo.commons.interceptor.restclient;

import com.demo.commons.logging.enums.LoggingType;
import com.demo.commons.logging.obfuscation.ObfuscationTemplateProvider;
import com.demo.commons.logging.obfuscation.body.BodyObfuscator;
import com.demo.commons.logging.obfuscation.header.HeaderObfuscator;
import com.demo.commons.logging.restclient.RestClientConstant;
import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.logging.ObfuscationTemplate;
import com.demo.commons.restclient.utils.HeadersExtractor;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RestClientResponseInterceptor implements ClientResponseFilter {

  private final ConfigurationBaseProperties properties;
  private final ObfuscationTemplate obfuscation;

  public RestClientResponseInterceptor(
      ConfigurationBaseProperties properties,
      ObfuscationTemplateProvider provider) {

    this.properties = properties;
    this.obfuscation = provider.get();
  }

  private static final Logger log = Logger.getLogger(RestClientRequestInterceptor.class);

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
    generateTrace(requestContext, responseContext);
  }

  private void generateTrace(ClientRequestContext requestContext, ClientResponseContext responseContext) {
    boolean isLoggerPresent = LoggingType.isLoggerPresent(properties, LoggingType.REST_CLIENT_REQ);
    if (isLoggerPresent) {

      Optional.ofNullable(responseContext)
          .ifPresent(context -> {
            MDC.put(LoggingType.REST_CLIENT_REQ + RestClientConstant.STATUS, String.valueOf(responseContext.getStatus()));

            Optional.ofNullable(context.getHeaders())
                .map(HeadersExtractor::extractHeadersAsMap)
                .ifPresent(headers -> log.info("Response headers <-- " + HeaderObfuscator.process(obfuscation, headers)));

            readAndResetResponseBody(context)
                .ifPresent(body -> log.info("Response body <-- " + BodyObfuscator.process(obfuscation, body)));
          });
    }
  }

  private Optional<String> readAndResetResponseBody(ClientResponseContext context) {
    try {
      String charset = Optional.ofNullable(context.getMediaType())
          .map(mt -> mt.getParameters().get("charset"))
          .orElse(StandardCharsets.UTF_8.name());

      InputStream originalStream = context.getEntityStream();

      if (Optional.ofNullable(originalStream).isEmpty()) {
        log.info("Response body <-- No content");
        return Optional.empty();
      }

      byte[] responseBody = originalStream.readAllBytes();
      String bodyString = new String(responseBody, charset);
      context.setEntityStream(new ByteArrayInputStream(responseBody));
      return Optional.of(bodyString);

    } catch (IOException exception) {
      log.error("HTTP response cannot be deserialized", exception);
      return Optional.empty();
    }
  }
}