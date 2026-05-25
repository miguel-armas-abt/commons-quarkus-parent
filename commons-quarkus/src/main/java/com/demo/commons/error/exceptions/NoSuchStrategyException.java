package com.demo.commons.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class NoSuchStrategyException extends GenericException {

  public NoSuchStrategyException(String strategy) {
    super(
        String.format("No such strategy %s", strategy),
        INTERNAL_SERVER_ERROR
    );
  }
}
