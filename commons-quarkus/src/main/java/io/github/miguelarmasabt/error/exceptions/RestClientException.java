package io.github.miguelarmasabt.error.exceptions;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.error.mapper.ErrorMapper;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class RestClientException extends RuntimeException {

  private final ErrorDto error;
  private final Response.Status httpStatus;

  public RestClientException(String code, String message, ErrorOrigin origin, Response.Status httpStatusCode) {
    super(message);
    this.httpStatus = httpStatusCode;
    this.error = ErrorMapper.external(code, message, origin);
  }
}
