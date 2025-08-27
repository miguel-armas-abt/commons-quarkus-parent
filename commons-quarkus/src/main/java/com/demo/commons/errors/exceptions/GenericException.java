package com.demo.commons.errors.exceptions;

import com.demo.commons.errors.dto.ErrorDto;
import com.demo.commons.errors.dto.ErrorOrigin;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    protected ErrorDto errorDetail;
    protected Response.Status httpStatus;

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String code, String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorDetail = ErrorDto.builder()
            .origin(ErrorOrigin.OWN)
            .code(code)
            .message(message)
            .build();
    }
}
