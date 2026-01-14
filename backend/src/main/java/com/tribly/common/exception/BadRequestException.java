package com.tribly.common.exception;

import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.Nullable;

public class BadRequestException extends TriblyException {

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
