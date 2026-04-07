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
}
