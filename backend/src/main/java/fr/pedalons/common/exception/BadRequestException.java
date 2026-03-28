package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class BadRequestException extends PedalonsException {

  public BadRequestException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode, errorDetails, cause);
  }

  public BadRequestException(ErrorCode errorCode, @Nullable Throwable cause) {
    this(errorCode, null, cause);
  }

  public BadRequestException(ErrorCode errorCode) {
    this(errorCode, null, null);
  }

  public BadRequestException() {
    this(ErrorCode.BAD_REQUEST, null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }
}
