package com.demo.commons.error.mapper;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.dto.ErrorOrigin;
import com.demo.commons.properties.ConfigurationBaseProperties;
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
