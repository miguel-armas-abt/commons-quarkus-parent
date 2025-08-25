package com.demo.poc.commons.core.errors.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.demo.poc.commons.core.constants.Symbol;

public class InvalidFieldException extends GenericException {

    public static final String INVALID_FIELD_CODE = "00.00.01";

    public InvalidFieldException(String message) {
        super(
            INVALID_FIELD_CODE,
            "Invalid field" + Symbol.COLON_WITH_SPACE + message,
            BAD_REQUEST
        );
    }
}
