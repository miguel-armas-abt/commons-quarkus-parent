package com.demo.commons.errors.exceptions;

import com.demo.commons.errors.dto.ErrorOrigin;
import com.demo.commons.errors.dto.ErrorDto;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class RestClientException extends GenericException {

  public RestClientException(String code, String message, ErrorOrigin errorOrigin, Response.Status httpStatusCode) {
    super(message);
    this.httpStatus = httpStatusCode;
    this.errorDetail = ErrorDto.builder()
        .origin(errorOrigin)
        .code(code)
        .message(message)
        .build();
  }
}
