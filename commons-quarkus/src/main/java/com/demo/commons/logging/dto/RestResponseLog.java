package com.demo.commons.logging.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
@Setter
public class RestResponseLog implements Serializable {

  private String method;
  private String httpCode;
  private String uri;
  private Map<String, String> responseHeaders;
  private String responseBody;
  private String traceParent;
}
