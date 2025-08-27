package com.demo.commons.interceptor.restclient;

import com.demo.commons.logging.ThreadContextInjector;
import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.restclient.utils.HeadersExtractor;
import com.demo.commons.tracing.enums.TraceParam;
import com.demo.commons.logging.dto.RestResponseLog;
import com.demo.commons.logging.enums.LoggingType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RestClientResponseInterceptor implements ClientResponseFilter {

  private final ConfigurationBaseProperties properties;
  private final ThreadContextInjector contextInjector;

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
    generateTrace(requestContext, responseContext);
  }

  private void generateTrace(ClientRequestContext requestContext, ClientResponseContext responseContext) {
    boolean isLoggerPresent = LoggingType.isLoggerPresent(properties, LoggingType.REST_CLIENT_REQ);
    if (isLoggerPresent) {

      RestResponseLog log = RestResponseLog.builder()
          .uri(requestContext.getUri().toString())
          .responseBody("{\"to\":\"do\"}")
          .responseHeaders(HeadersExtractor.extractHeadersAsMap(responseContext.getHeaders()))
          .httpCode(String.valueOf(responseContext.getStatus()))
          .traceParent(HeadersExtractor.extractHeadersAsMap(requestContext.getHeaders()).get(TraceParam.TRACE_PARENT.getKey()))
          .build();

      contextInjector.populateFromRestResponse(LoggingType.REST_CLIENT_RES, log);
    }
  }
}