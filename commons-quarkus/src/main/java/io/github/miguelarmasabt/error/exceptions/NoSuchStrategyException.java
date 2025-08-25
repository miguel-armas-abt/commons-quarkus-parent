package io.github.miguelarmasabt.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class NoSuchStrategyException extends GenericException {

  public static final String NO_SUCH_STRATEGY = "no_such_strategy";

  public NoSuchStrategyException(String errorCode) {
    super(errorCode, INTERNAL_SERVER_ERROR);
  }

  public NoSuchStrategyException(String errorCode, Throwable throwable) {
    super(errorCode, INTERNAL_SERVER_ERROR, throwable);
  }

  public NoSuchStrategyException(String errorCode, String... parameterNames) {
    super(errorCode, INTERNAL_SERVER_ERROR, parameterNames);
  }

  public NoSuchStrategyException(String errorCode, Throwable throwable, String... parameterNames) {
    super(errorCode, INTERNAL_SERVER_ERROR, throwable, parameterNames);
  }
}
