package io.github.miguelarmasabt.error.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class ProcessingFailedException extends GenericException {

  public static final String PROCESSING_FAILED = "processing_failed";

  public ProcessingFailedException(String code) {
    super(code, INTERNAL_SERVER_ERROR);
  }

  public ProcessingFailedException(String code, Throwable throwable) {
    super(code, INTERNAL_SERVER_ERROR, throwable);
  }

  public ProcessingFailedException(String code, String... messageArguments) {
    super(code, INTERNAL_SERVER_ERROR, messageArguments);
  }

  public ProcessingFailedException(String code, Throwable throwable, String... messageArguments) {
    super(code, INTERNAL_SERVER_ERROR, throwable, messageArguments);
  }
}
