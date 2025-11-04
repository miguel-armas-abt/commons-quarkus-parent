package com.demo.commons.validations.headers;

import com.demo.commons.tracing.enums.TraceParam;
import com.demo.commons.validations.ParamMapper;
import com.demo.commons.tracing.enums.ForwardedParam;

import java.util.Map;
import java.util.TreeMap;

public class DefaultHeadersMapper implements ParamMapper<DefaultHeaders> {

  @Override
  public Map.Entry<DefaultHeaders, Map<String, String>> map(Map<String, String> params) {
    DefaultHeaders defaultHeaders = DefaultHeaders.builder()
        .channelId(params.get(ForwardedParam.CHANNEL_ID.getKey()))
        .traceParent(params.get(TraceParam.TRACE_PARENT.getKey()))
        .build();

    Map<String, String> defaultHeadersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    defaultHeadersMap.put(ForwardedParam.CHANNEL_ID.getKey(), defaultHeaders.getChannelId());
    defaultHeadersMap.put(TraceParam.TRACE_PARENT.getKey(), defaultHeaders.getTraceParent());

    return Map.entry(defaultHeaders, defaultHeadersMap);
  }

  @Override
  public boolean supports(Class<?> paramClass) {
    return DefaultHeaders.class.isAssignableFrom(paramClass);
  }
}
