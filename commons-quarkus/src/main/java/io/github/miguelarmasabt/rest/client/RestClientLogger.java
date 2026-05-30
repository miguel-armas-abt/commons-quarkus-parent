package io.github.miguelarmasabt.rest.client;

import io.github.miguelarmasabt.mdc.MdcRestClient;
import io.github.miguelarmasabt.mdc.MdcUtil;
import io.github.miguelarmasabt.obfuscation.HeaderObfuscator;
import io.github.miguelarmasabt.obfuscation.JsonBodyObfuscator;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.rest.utils.ContentValidator;
import io.github.miguelarmasabt.rest.utils.HeaderExtractor;
import io.github.miguelarmasabt.utils.TextTruncator;
import io.quarkus.arc.Unremovable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.api.ClientLogger;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Unremovable
@ApplicationScoped
public class RestClientLogger implements ClientLogger {

  private static final Logger log = Logger.getLogger(RestClientLogger.class);

  private final AtomicInteger bodyLimit = new AtomicInteger(2048);

  private final long maxResponseBodyBytes;
  private final boolean showBodyIfChunked;
  private final HeaderObfuscator headerObfuscator;
  private final JsonBodyObfuscator bodyObfuscator;

  public RestClientLogger(ConfigurationBaseProperties properties,
                          HeaderObfuscator headerObfuscator,
                          JsonBodyObfuscator bodyObfuscator) {
    this.maxResponseBodyBytes = Math.max(0, properties.rest().logging().maxResponseBodyBytes());
    this.showBodyIfChunked = properties.rest().logging().showResponseBodyIfChunked();
    this.headerObfuscator = headerObfuscator;
    this.bodyObfuscator = bodyObfuscator;
  }

  @Override
  public void setBodySize(int bodySize) {
    bodyLimit.set(Math.max(0, bodySize));
  }

  @Override
  public void logRequest(HttpClientRequest request, Buffer bodyBuffer, boolean omitBody) {
    String method = request.getMethod().name();
    String uri = request.absoluteURI();
    Map<String, String> headers = HeaderExtractor.asMap(request.headers());
    String obfuscatedHeaders = headerObfuscator.process(headers);

    Runnable action = () -> {
      if (!omitBody) {
        Optional.ofNullable(bodyBuffer)
            .filter(buffer -> buffer.length() > 0)
            .map(buffer -> buffer.toString(StandardCharsets.UTF_8))
            .map(bodyString -> ContentValidator.isJson(headers)
                ? bodyObfuscator.process(bodyString)
                : bodyString)
            .map(bodyString -> TextTruncator.truncate(bodyString, bodyLimit.get()))
            .ifPresentOrElse(bodyString -> {
              String message = String.format("--> %s %s | Request headers: %s | Request body: %s", method, uri, obfuscatedHeaders, bodyString);
              log.info(message);
            }, () -> {
              String message = String.format("--> %s %s | Request headers: %s", method, uri, obfuscatedHeaders);
              log.info(message);
            });
      }
    };

    Map<String, String> mdcLabels = Map.of(
        MdcRestClient.METHOD.getLabel(), method,
        MdcRestClient.URI.getLabel(), uri
    );
    MdcUtil.populate(mdcLabels, action);
  }

  @Override
  public void logResponse(HttpClientResponse response, boolean redirect) {
    HttpClientRequest request = response.request();
    int httpStatus = response.statusCode();
    String httpStatusMessage = response.statusMessage();
    String uri = request.absoluteURI();
    String method = request.getMethod().name();
    Map<String, String> headers = HeaderExtractor.asMap(response.headers());
    String obfuscatedHeaders = headerObfuscator.process(headers);

    boolean shouldLogResponseBody = ContentValidator.getContentLength(headers)
        .map(contentLength -> {
          boolean isLessThanMaxContentLength = contentLength <= maxResponseBodyBytes;
          if (!isLessThanMaxContentLength) {
            log.warnf("Content-Length=%d exceeds configured buffer=%d", contentLength, maxResponseBodyBytes);
          }
          return isLessThanMaxContentLength;
        })
        .orElseGet(() -> {
          log.warnf("Transfer-Encoding: chunked, show-body-if-chunked=%s", showBodyIfChunked);
          return showBodyIfChunked;
        });

    response.bodyHandler(bodyBuffer -> {
      Runnable action = () -> Optional.ofNullable(bodyBuffer)
          .filter(buffer -> shouldLogResponseBody)
          .filter(buffer -> buffer.length() != 0)
          .map(buffer -> buffer.toString(StandardCharsets.UTF_8))
          .map(bodyString -> ContentValidator.isJson(headers)
              ? bodyObfuscator.process(bodyString)
              : bodyString
          )
          .map(bodyString -> TextTruncator.truncate(bodyString, bodyLimit.get()))
          .ifPresentOrElse(bodyString -> {
            String message = String.format("<-- %s %s | Response headers: %s | Response body: %s", httpStatus, httpStatusMessage, obfuscatedHeaders, bodyString);
            log.info(message);

          }, () -> {
            String message = String.format("<-- %s %s | Response headers: %s", httpStatus, httpStatusMessage, obfuscatedHeaders);
            log.info(message);
          });

      Map<String, String> mdcLabels = Map.of(
          MdcRestClient.METHOD.getLabel(), method,
          MdcRestClient.URI.getLabel(), uri,
          MdcRestClient.STATUS.getLabel(), String.valueOf(httpStatus)
      );
      MdcUtil.populate(mdcLabels, action);
    });
  }
}
