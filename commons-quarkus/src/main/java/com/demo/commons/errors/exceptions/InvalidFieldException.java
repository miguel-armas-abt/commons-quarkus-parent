package com.demo.commons.errors.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.demo.commons.constants.Symbol;

public class InvalidFieldException extends GenericException {

    public static final String ERROR_CODE = "00.00.01";

    public InvalidFieldException(String message) {
        super(
            ERROR_CODE,
            "Invalid field" + Symbol.COLON_WITH_SPACE + message,
            BAD_REQUEST
        );
    }
}
