package io.github.miguelarmasabt.error.mapper;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class DefaultErrorMapper {

  private final ConfigurationBaseProperties properties;

  public ErrorDto defaultError() {
    ErrorDto error = new ErrorDto();
    error.setCode(properties.error().defaultCode());
    error.setOrigin(ErrorOrigin.OWN);
    error.setMessage(properties.error().defaultMessage());
    return error;
  }
}
