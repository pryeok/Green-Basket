package com.greenbasket.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Object details;

    public static ErrorResponse of(int status, String error, String message, Object details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                details
        );
    }

    public static ErrorResponse of(int status, String error, String message) {
        return of(status, error, message, null);
    }
}
