package com.tribly.infrastructure.exception;

public class BusinessException extends RuntimeException {

    private final ErrorType errorType;
    private final String errorCode;

    public BusinessException(String message, ErrorType errorType, String errorCode) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public BusinessException(String message, ErrorType errorType) {
        this(message, errorType, errorType.name());
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public enum ErrorType {
        NOT_FOUND,
        CONFLICT,
        FORBIDDEN,
        VALIDATION,
        BUSINESS_RULE
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, ErrorType.NOT_FOUND);
    }

    public static BusinessException notFound(String entity, Long id) {
        return new BusinessException(
                String.format("%s with id %d not found", entity, id),
                ErrorType.NOT_FOUND,
                entity.toUpperCase() + "_NOT_FOUND"
        );
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, ErrorType.CONFLICT);
    }

    public static BusinessException conflict(String message, String errorCode) {
        return new BusinessException(message, ErrorType.CONFLICT, errorCode);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, ErrorType.FORBIDDEN);
    }

    public static BusinessException validation(String message) {
        return new BusinessException(message, ErrorType.VALIDATION);
    }

    public static BusinessException businessRule(String message, String errorCode) {
        return new BusinessException(message, ErrorType.BUSINESS_RULE, errorCode);
    }
}
