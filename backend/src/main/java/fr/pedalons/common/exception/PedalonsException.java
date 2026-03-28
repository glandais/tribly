package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public abstract class PedalonsException extends RuntimeException {

  private final ErrorCode errorCode;
  private final @Nullable ErrorDetails errorDetails;

  protected PedalonsException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode.name(), cause);
    this.errorCode = errorCode;
    this.errorDetails = errorDetails;
  }

  public abstract Response.Status getStatus();
  /*
    public static PedalonsException notFound(String message) {
      return new NotFoundException(message);
    }

    public static PedalonsException notFound(String entity, Long id) {
      return new NotFoundException(
          String.format("%s with id %d not found", entity, id), ErrorCode.NOT_FOUND);
    }

    public static PedalonsException notFound(String entity, String slug) {
      return new NotFoundException(
          String.format("%s with slug '%s' not found", entity, slug), ErrorCode.NOT_FOUND);
    }

    public static PedalonsException conflict(String message) {
      return new ConflictException(message, ErrorCode.CONFLICT);
    }

    public static PedalonsException conflict(String message, Throwable cause) {
      return new ConflictException(message, ErrorCode.CONFLICT, cause);
    }

    public static PedalonsException conflict(String message, ErrorCode errorCode) {
      return new ConflictException(message, errorCode);
    }

    public static PedalonsException forbidden(String message) {
      return new ForbiddenException(message, ErrorCode.FORBIDDEN);
    }

    public static PedalonsException forbidden(String message, ErrorCode errorCode) {
      return new ForbiddenException(message, errorCode);
    }

    public static PedalonsException validation(String message) {
      return new BusinessException(message, ErrorCode.VALIDATION);
    }

    public static PedalonsException businessRule(String message, ErrorCode errorCode) {
      return new BusinessException(message, errorCode);
    }
  */
}
