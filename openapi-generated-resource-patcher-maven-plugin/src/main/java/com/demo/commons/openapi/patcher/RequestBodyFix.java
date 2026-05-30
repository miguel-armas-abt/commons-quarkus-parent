package com.demo.commons.openapi.patcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestBodyFix {

  private String resource;
  private String operationId;
  private String targetClass;
  private String parameterName = "data";

  public String simpleName() {
    int index = targetClass.lastIndexOf('.');
    return index >= 0 ? targetClass.substring(index + 1) : targetClass;
  }

}
