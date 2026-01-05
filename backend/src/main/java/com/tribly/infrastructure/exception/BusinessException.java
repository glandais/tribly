package com.tribly.infrastructure.exception;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorType errorType;
  private final String errorCode;

  public BusinessException(
      String message, ErrorType errorType, String errorCode, @Nullable Throwable cause) {
    super(message, cause);
    this.errorType = errorType;
    this.errorCode = errorCode;
  }

  public BusinessException(String message, ErrorType errorType, String errorCode) {
    this(message, errorType, errorCode, null);
  }

  public BusinessException(String message, ErrorType errorType, @Nullable Throwable cause) {
    this(message, errorType, errorType.name(), cause);
  }

  public BusinessException(String message, ErrorType errorType) {
    this(message, errorType, errorType.name(), null);
  }

  public enum ErrorType {
    NOT_FOUND,
    CONFLICT,
    FORBIDDEN,
    VALIDATION,
    BUSINESS_RULE,
    NEW_SLUG
  }

  public static BusinessException notFound(String message) {
    return new NotFoundException(message);
  }

  public static BusinessException notFound(String entity, Long id) {
    return new NotFoundException(
        String.format("%s with id %d not found", entity, id), entity.toUpperCase() + "_NOT_FOUND");
  }

  public static BusinessException notFound(String entity, String id) {
    return new NotFoundException(
        String.format("%s with id '%s' not found", entity, id),
        entity.toUpperCase() + "_NOT_FOUND");
  }

  public static BusinessException newSlug(String oldSlug, String teamSlug, String newSlug) {
    return new NewSlugException(oldSlug, teamSlug, newSlug);
  }

  public static BusinessException conflict(String message) {
    return new BusinessException(message, ErrorType.CONFLICT);
  }

  public static BusinessException conflict(String message, Throwable cause) {
    return new BusinessException(message, ErrorType.CONFLICT, cause);
  }

  public static BusinessException conflict(String message, String errorCode) {
    return new BusinessException(message, ErrorType.CONFLICT, errorCode);
  }

  public static BusinessException forbidden(String message) {
    return new BusinessException(message, ErrorType.FORBIDDEN);
  }

  public static BusinessException forbidden(String message, String errorCode) {
    return new BusinessException(message, ErrorType.FORBIDDEN, errorCode);
  }

  public static BusinessException validation(String message) {
    return new BusinessException(message, ErrorType.VALIDATION);
  }

  public static BusinessException businessRule(String message, String errorCode) {
    return new BusinessException(message, ErrorType.BUSINESS_RULE, errorCode);
  }
}
