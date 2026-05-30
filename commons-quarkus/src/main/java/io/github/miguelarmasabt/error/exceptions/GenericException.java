package io.github.miguelarmasabt.error.exceptions;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    protected ErrorDto error;
    protected Response.Status httpStatus;

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;

        ErrorDto error = new ErrorDto();
        error.setOrigin(ErrorOrigin.OWN);
        error.setMessage(message);
        this.error = error;
    }

    public GenericException(String code, String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;

        ErrorDto error = new ErrorDto();
        error.setOrigin(ErrorOrigin.OWN);
        error.setCode(code);
        error.setMessage(message);
        this.error = error;
    }

    public GenericException(String code, String message, Response.Status httpStatus, Throwable exception) {
        super(message, exception);
        this.httpStatus = httpStatus;

        ErrorDto error = new ErrorDto();
        error.setOrigin(ErrorOrigin.OWN);
        error.setCode(code);
        error.setMessage(message);
        this.error = error;
    }
}
