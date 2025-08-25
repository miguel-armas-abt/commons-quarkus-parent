package com.demo.poc.commons.core.errors.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.demo.poc.commons.core.constants.Symbol;

public class JsonReadException extends GenericException {

  private static final String EXCEPTION_CODE = "00.00.03";

  public JsonReadException(String message) {
    super(
        EXCEPTION_CODE,
        "Json is not readable" + Symbol.COLON_WITH_SPACE + message,
        INTERNAL_SERVER_ERROR
    );
  }
}
