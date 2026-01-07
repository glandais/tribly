package com.tribly.infrastructure.exception;

import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public abstract class TriblyException extends RuntimeException {

  private final ErrorCode errorCode;
  private final @Nullable ErrorDetails errorDetails;

  protected TriblyException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode.name(), cause);
    this.errorCode = errorCode;
    this.errorDetails = errorDetails;
  }

  public abstract Response.Status getStatus();
  /*
    public static TriblyException notFound(String message) {
      return new NotFoundException(message);
    }

    public static TriblyException notFound(String entity, Long id) {
      return new NotFoundException(
          String.format("%s with id %d not found", entity, id), ErrorCode.NOT_FOUND);
    }

    public static TriblyException notFound(String entity, String slug) {
      return new NotFoundException(
          String.format("%s with slug '%s' not found", entity, slug), ErrorCode.NOT_FOUND);
    }

    public static TriblyException conflict(String message) {
      return new ConflictException(message, ErrorCode.CONFLICT);
    }

    public static TriblyException conflict(String message, Throwable cause) {
      return new ConflictException(message, ErrorCode.CONFLICT, cause);
    }

    public static TriblyException conflict(String message, ErrorCode errorCode) {
      return new ConflictException(message, errorCode);
    }

    public static TriblyException forbidden(String message) {
      return new ForbiddenException(message, ErrorCode.FORBIDDEN);
    }

    public static TriblyException forbidden(String message, ErrorCode errorCode) {
      return new ForbiddenException(message, errorCode);
    }

    public static TriblyException validation(String message) {
      return new BusinessException(message, ErrorCode.VALIDATION);
    }

    public static TriblyException businessRule(String message, ErrorCode errorCode) {
      return new BusinessException(message, errorCode);
    }
  */
}
