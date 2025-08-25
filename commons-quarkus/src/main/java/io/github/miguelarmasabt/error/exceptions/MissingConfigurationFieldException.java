package io.github.miguelarmasabt.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class MissingConfigurationFieldException extends GenericException {

  public static final String MISSING_CONFIGURATION_FIELD = "missing_configuration_field";

  public MissingConfigurationFieldException(String errorCode) {
    super(errorCode, INTERNAL_SERVER_ERROR);
  }

  public MissingConfigurationFieldException(String errorCode, Throwable throwable) {
    super(errorCode, INTERNAL_SERVER_ERROR, throwable);
  }

  public MissingConfigurationFieldException(String errorCode, String... fieldNames) {
    super(errorCode, INTERNAL_SERVER_ERROR, fieldNames);
  }

  public MissingConfigurationFieldException(String errorCode, Throwable throwable, String... fieldNames) {
    super(errorCode, INTERNAL_SERVER_ERROR, throwable, fieldNames);
  }
}
