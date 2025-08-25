package io.github.miguelarmasabt.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class RequiredFieldException extends GenericException {

  public static final String REQUIRED_FIELD = "required_field";

  public RequiredFieldException(String code) {
    super(code, BAD_REQUEST);
  }

  public RequiredFieldException(String code, Throwable throwable) {
    super(code, BAD_REQUEST, throwable);
  }

  public RequiredFieldException(String code, String... fieldNames) {
    super(code, BAD_REQUEST, fieldNames);
  }

  public RequiredFieldException(String code, Throwable throwable, String... fieldNames) {
    super(code, BAD_REQUEST, throwable, fieldNames);
  }
}
