package io.github.miguelarmasabt.model;

import java.util.Objects;

public record RequestBodyFix(
    String resource,
    String operationId,
    String targetClass
) {

  public String simpleName() {
    if (Objects.isNull(targetClass) || targetClass.isBlank()) {
      return null;
    }

    int index = targetClass.lastIndexOf('.');
    return index >= 0 ? targetClass.substring(index + 1) : targetClass;
  }
}