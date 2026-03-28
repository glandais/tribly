package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class NotFoundException extends PedalonsException {

  public NotFoundException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode, errorDetails, cause);
  }

  public NotFoundException(ErrorCode errorCode, @Nullable Throwable cause) {
    this(errorCode, null, cause);
  }

  public NotFoundException(ErrorCode errorCode) {
    this(errorCode, null, null);
  }

  public NotFoundException() {
    this(ErrorCode.NOT_FOUND, null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }
}
