package com.demo.commons.error.exceptions;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.dto.ErrorOrigin;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class RestClientException extends GenericException {

  public RestClientException(String code, String message, ErrorOrigin errorOrigin, Response.Status httpStatusCode) {
    super(message);
    this.httpStatus = httpStatusCode;

    ErrorDto error = new ErrorDto();
    error.setOrigin(errorOrigin);
    error.setCode(code);
    error.setMessage(message);

    this.error = error;
  }
}
