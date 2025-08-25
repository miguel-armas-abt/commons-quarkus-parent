package com.demo.poc.commons.core.validations;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public interface ParamMapper {

  Object map(@NotNull Map<String, String> params);

  boolean supports(Class<?> paramClass);
}
