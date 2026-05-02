package com.demo.commons.errors.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class NoSuchHeaderException extends GenericException {

  public static final String ERROR_CODE = "00.00.01";

  public NoSuchHeaderException(String headerName) {
    super(
        ERROR_CODE,
        "No such header: " + headerName,
        BAD_REQUEST
    );
  }
}
