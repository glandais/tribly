package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import jakarta.ws.rs.core.Response;

public class InternalException extends PedalonsException {

  public InternalException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, null, cause);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.INTERNAL_SERVER_ERROR;
  }
}
