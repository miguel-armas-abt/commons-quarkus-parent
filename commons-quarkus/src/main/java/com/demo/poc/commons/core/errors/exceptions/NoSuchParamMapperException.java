package com.demo.poc.commons.core.errors.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import com.demo.poc.commons.core.constants.Symbol;

public class NoSuchParamMapperException extends GenericException {

  private static final String EXCEPTION_CODE = "00.00.04";

  public NoSuchParamMapperException(Class<?> mapperClass) {
    super(
        EXCEPTION_CODE,
        "No such implementation of param mapper" + Symbol.COLON_WITH_SPACE + mapperClass.getName(),
        INTERNAL_SERVER_ERROR
    );
  }
}
