package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.properties.ProjectType;
import jakarta.ws.rs.core.Response;

import java.util.Locale;
import java.util.Optional;

public interface ExceptionResponseMapper {

  boolean support(Throwable throwable);

  ConfigurationBaseProperties properties();

  ConfiguredMessageResolver configuredMessageResolver();

  Response.Status selectStatus(Throwable throwable);

  default ErrorDto map(Throwable throwable, Locale locale) {
    String selectedCode = this.selectCode(throwable)
        .orElseGet(() -> properties().error().defaultCode());

    String selectedMessage = this.selectMessage(throwable, locale)
        .orElseGet(() -> configuredMessageResolver().defaultMessage(locale));

    return new ErrorDto(ErrorOrigin.OWN, selectedCode, selectedMessage, null);
  }

  Optional<String> selectMessage(Throwable throwable, Locale locale);

  default Optional<String> selectCode(Throwable throwable) {
    return Optional.of(properties().error().defaultCode());
  }

  default String mapMessage(String message, boolean exposeMessage, Locale locale) {
    if (ProjectType.BACKEND_FOR_FRONTEND.equals(properties().projectType()) && !exposeMessage) {
      return configuredMessageResolver().defaultMessage(locale);
    }
    return message;
  }
}
