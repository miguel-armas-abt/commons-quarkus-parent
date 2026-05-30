package io.github.miguelarmasabt.rest.client;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.properties.rest.RestClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class RestClientErrorSelector {

  private static final Logger log = Logger.getLogger(RestClientErrorSelector.class);

  private final ConfigurationBaseProperties properties;

  public String selectCode(String errorCode, String serviceName) {
    String code = Optional.ofNullable(errorCode)
        .orElseGet(() -> properties.error().defaultCode());

    return findErrors(serviceName)
        .filter(error -> Objects.nonNull(error.get(errorCode)))
        .map(error -> error.get(code).customCode())
        .orElse(code);
  }

  public String selectMessage(String errorCode,
                              String errorMessage,
                              String serviceName) {
    String message = Optional.ofNullable(errorMessage)
        .orElse(properties.error().defaultMessage());

    return findErrors(serviceName)
        .filter(error -> Objects.nonNull(error.get(errorCode)))
        .map(error -> error.get(errorCode))
        .map(RestClient.Error::message)
        .orElse(message);
  }

  public static ErrorOrigin selectType(Class<?> errorWrapperClass) {
    return (errorWrapperClass.isAssignableFrom(ErrorDto.class))
        ? ErrorOrigin.PARTNER
        : ErrorOrigin.THIRD_PARTY;
  }

  public int selectHttpCode(int httpCode,
                            String errorCode,
                            String serviceName) {
    return findErrors(serviceName)
        .filter(errors -> Objects.nonNull(errors.get(errorCode)))
        .map(errors -> errors.get(errorCode))
        .map(RestClient.Error::httpCode)
        .orElse(httpCode);
  }

  public Optional<Map<String, RestClient.Error>> findErrors(String serviceName) {
    return Optional.ofNullable(properties.rest().client().get(serviceName).error());
  }
}