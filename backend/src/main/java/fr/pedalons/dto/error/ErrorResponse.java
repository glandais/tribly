package fr.pedalons.dto.error;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Error response")
@ValidateSchema
public record ErrorResponse(
    @Schema(description = "Error code", required = true) ErrorCode code,
    @Nullable @Schema(description = "Additional details") ErrorDetails errorDetails) {
  public ErrorResponse(ErrorCode code) {
    this(code, null);
  }

  public static ErrorResponse notFound() {
    return new ErrorResponse(ErrorCode.NOT_FOUND);
  }

  public static ErrorResponse validation(List<FieldError> errors) {
    return new ErrorResponse(ErrorCode.VALIDATION, new ErrorValidationDetails(errors));
  }

  public static ErrorResponse badRequest() {
    return new ErrorResponse(ErrorCode.BAD_REQUEST);
  }

  public static ErrorResponse unauthorized() {
    return new ErrorResponse(ErrorCode.UNAUTHORIZED);
  }

  public static ErrorResponse forbidden() {
    return new ErrorResponse(ErrorCode.FORBIDDEN);
  }

  public static ErrorResponse internal() {
    return new ErrorResponse(ErrorCode.INTERNAL_ERROR);
  }
}
