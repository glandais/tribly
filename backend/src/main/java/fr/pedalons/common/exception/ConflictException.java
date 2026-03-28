package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class ConflictException extends PedalonsException {
  public ConflictException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode, errorDetails, cause);
  }

  public ConflictException(ErrorCode errorCode, @Nullable Throwable cause) {
    this(errorCode, null, cause);
  }

  public ConflictException(ErrorCode errorCode) {
    this(errorCode, null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.CONFLICT;
  }
}
