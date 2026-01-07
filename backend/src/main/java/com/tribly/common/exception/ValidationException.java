package com.tribly.common.exception;

import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class ValidationException extends TriblyException {

  public ValidationException(
      ErrorCode errorCode, @Nullable ErrorDetails errorDetails, @Nullable Throwable cause) {
    super(errorCode, errorDetails, cause);
  }

  protected ValidationException(ErrorCode errorCode, @Nullable Throwable cause) {
    this(errorCode, null, cause);
  }

  protected ValidationException(ErrorCode errorCode) {
    this(errorCode, null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }
}
