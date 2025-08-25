package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@ApplicationScoped
@RequiredArgsConstructor
public class ErrorMapper {

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  public ErrorDto defaultError(Locale locale) {
    return new ErrorDto(
        ErrorOrigin.OWN,
        properties.error().defaultCode(),
        configuredMessageResolver.defaultMessage(locale),
        null
    );
  }

  public static ErrorDto own(String code, String... messageArguments) {
    return new ErrorDto(ErrorOrigin.OWN, code, null, messageArguments);
  }

  public static ErrorDto own(String code) {
    return new ErrorDto(ErrorOrigin.OWN, code, null, null);
  }

  public static ErrorDto external(String code, String message, ErrorOrigin origin) {
    return new ErrorDto(origin, code, message, null);
  }
}
