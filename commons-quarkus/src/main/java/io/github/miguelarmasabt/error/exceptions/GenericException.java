package io.github.miguelarmasabt.error.exceptions;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.mapper.ErrorMapper;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

  protected ErrorDto error;
  protected Response.Status httpStatus;

  public GenericException(String errorCode) {
    super(errorCode);
    this.error = ErrorMapper.own(errorCode);
  }

  public GenericException(String errorCode, String... messageArguments) {
    super(errorCode);
    this.error = ErrorMapper.own(errorCode, messageArguments);
  }

  public GenericException(String errorCode, Response.Status httpStatus) {
    super(errorCode);
    this.httpStatus = httpStatus;
    this.error = ErrorMapper.own(errorCode);
  }

  public GenericException(String errorCode, Response.Status httpStatus, String... messageArguments) {
    super(errorCode);
    this.httpStatus = httpStatus;
    this.error = ErrorMapper.own(errorCode, messageArguments);
  }

  public GenericException(String errorCode, Response.Status httpStatus, Throwable exception) {
    super(errorCode, exception);
    this.httpStatus = httpStatus;
    this.error = ErrorMapper.own(errorCode);
  }

  public GenericException(String errorCode, Response.Status httpStatus, Throwable exception, String... messageArguments) {
    super(errorCode, exception);
    this.httpStatus = httpStatus;
    this.error = ErrorMapper.own(errorCode, messageArguments);
  }
}
