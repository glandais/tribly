package com.tribly.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    String path,
    Instant timestamp,
    List<FieldError> errors,
    Map<String, Object> details
) {
    public ErrorResponse(String code, String message, String path) {
        this(code, message, path, Instant.now(), null, null);
    }

    public ErrorResponse(String code, String message, String path, List<FieldError> errors) {
        this(code, message, path, Instant.now(), errors, null);
    }

    public record FieldError(
        String field,
        String message,
        Object rejectedValue
    ) {}

    public static ErrorResponse notFound(String path, String message) {
        return new ErrorResponse("NOT_FOUND", message, path);
    }

    public static ErrorResponse badRequest(String path, String message) {
        return new ErrorResponse("BAD_REQUEST", message, path);
    }

    public static ErrorResponse badRequest(String path, String message, List<FieldError> errors) {
        return new ErrorResponse("BAD_REQUEST", message, path, errors);
    }

    public static ErrorResponse unauthorized(String path, String message) {
        return new ErrorResponse("UNAUTHORIZED", message, path);
    }

    public static ErrorResponse forbidden(String path, String message) {
        return new ErrorResponse("FORBIDDEN", message, path);
    }

    public static ErrorResponse conflict(String path, String message) {
        return new ErrorResponse("CONFLICT", message, path);
    }

    public static ErrorResponse internal(String path, String message) {
        return new ErrorResponse("INTERNAL_ERROR", message, path);
    }
}
