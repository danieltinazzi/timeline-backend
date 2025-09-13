package com.timeline.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {

    public static ErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return ErrorResponse.of(httpStatus, message, path, null);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message, String path, Map<String, String> details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                path,
                details
        );
    }
}


