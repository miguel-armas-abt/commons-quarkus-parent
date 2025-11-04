package com.demo.commons.validations;

import com.demo.commons.restserver.RestServerUtils;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ParamValidator {

  private final Instance<ParamMapper> paramMappers;
  private final BodyValidator bodyValidator;

  public <T> Uni<Map.Entry<T, Map<String, String>>> validateHeadersAndGet(MultivaluedMap<String, String> requestHeaders, Class<T> paramClass) {
    Map<String, String> headers = RestServerUtils.extractHeadersAsMap(requestHeaders);
    return validateAndGet(headers, paramClass);
  }

  public <T> Uni<Map.Entry<T, Map<String, String>>> validateAndGet(Map<String, String> paramsMap, Class<T> paramClass) {
    ParamMapper mapper = ParamMapper.selectMapper(paramClass, paramMappers);
    Map.Entry<T, Map<String, String>> tuple = mapper.map(paramsMap);
    return bodyValidator.validateAndGet(tuple.getKey())
        .map(params -> tuple);
  }

}