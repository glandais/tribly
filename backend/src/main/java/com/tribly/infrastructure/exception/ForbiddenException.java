package com.tribly.infrastructure.exception;

import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class ForbiddenException extends TriblyException {
  public ForbiddenException(@Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(ErrorCode.FORBIDDEN, errorDetails, cause);
  }

  public ForbiddenException(@Nullable Throwable cause) {
    this(null, cause);
  }

  public ForbiddenException() {
    this(null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.FORBIDDEN;
  }
}
