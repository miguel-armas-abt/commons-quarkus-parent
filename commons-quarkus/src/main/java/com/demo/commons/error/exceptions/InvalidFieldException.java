package com.demo.commons.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidFieldException extends GenericException {

  public InvalidFieldException(String message) {
    super(
        String.format("Invalid field: %s", message),
        BAD_REQUEST
    );
  }
}
