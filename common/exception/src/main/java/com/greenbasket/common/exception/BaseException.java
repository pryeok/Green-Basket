package com.greenbasket.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String messageKey;
    private final Object details;

    protected BaseException(HttpStatus httpStatus, String messageKey, Object details) {
        super(messageKey);
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
        this.details = details;
    }

    protected BaseException(HttpStatus httpStatus, String messageKey) {
        this(httpStatus, messageKey, null);
    }
}
