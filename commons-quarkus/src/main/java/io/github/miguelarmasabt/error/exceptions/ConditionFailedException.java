package io.github.miguelarmasabt.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class ConditionFailedException extends GenericException {

  public static final String CONDITION_FAILED = "condition_failed";

  public ConditionFailedException(String errorCode) {
    super(errorCode, BAD_REQUEST);
  }

  public ConditionFailedException(String errorCode, Throwable throwable) {
    super(errorCode, BAD_REQUEST, throwable);
  }

  public ConditionFailedException(String errorCode, String... messageArguments) {
    super(errorCode, BAD_REQUEST, messageArguments);
  }

  public ConditionFailedException(String errorCode, Throwable throwable, String... messageArguments) {
    super(errorCode, BAD_REQUEST, throwable, messageArguments);
  }
}
