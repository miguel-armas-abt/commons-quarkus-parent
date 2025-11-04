package com.demo.commons.validations;

import com.demo.commons.errors.exceptions.NoSuchParamMapperException;
import jakarta.enterprise.inject.Instance;

import java.util.Map;

public interface ParamMapper<T> {

  Map.Entry<T, Map<String, String>> map(Map<String, String> params);

  boolean supports(Class<?> paramClass);

  static <T> ParamMapper<T> selectMapper(Class<?> paramClass, Instance<ParamMapper> paramMappers) {
    return paramMappers
        .stream()
        .filter(mapper -> mapper.supports(paramClass))
        .findFirst()
        .orElseThrow(() -> new NoSuchParamMapperException(paramClass));
  }
}
