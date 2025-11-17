package com.greenbasket.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Environment env;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("Business exception: {} - {}", e.getMessageKey(), e.getDetails(), e);

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getHttpStatus().value(),
                e.getHttpStatus().getReasonPhrase(),
                env.getProperty(e.getMessageKey(), e.getMessage()),
                e.getDetails()
        );

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        log.error("Response status exception: {} - {}", e.getStatusCode(), e.getReason(), e);

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getStatusCode().value(),
                e.getStatusCode().toString(),
                e.getReason() != null ? e.getReason() : e.getMessage()
        );

        return ResponseEntity
                .status(e.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                500,
                "Internal Server Error",
                "Internal server error"
        );

        return ResponseEntity
                .status(500)
                .body(errorResponse);
    }
}
