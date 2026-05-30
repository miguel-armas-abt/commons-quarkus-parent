package io.github.miguelarmasabt.model;

public class RequestBodyFix {

  private String resource;
  private String operationId;
  private String targetClass;
  private String parameterName = "data";

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(String targetClass) {
    this.targetClass = targetClass;
  }

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public String simpleName() {
    if (targetClass == null || targetClass.isBlank()) {
      return null;
    }

    int index = targetClass.lastIndexOf('.');
    return index >= 0 ? targetClass.substring(index + 1) : targetClass;
  }

  @Override
  public String toString() {
    return "RequestBodyFix{"
        + "resource='" + resource + '\''
        + ", operationId='" + operationId + '\''
        + ", targetClass='" + targetClass + '\''
        + ", parameterName='" + parameterName + '\''
        + '}';
  }
}
