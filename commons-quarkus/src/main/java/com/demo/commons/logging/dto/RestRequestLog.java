package com.demo.commons.logging.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
@Setter
public class RestRequestLog implements Serializable {

  private String method;
  private String uri;
  private Map<String, String> requestHeaders;
  private String requestBody;
  private String traceParent;
}
