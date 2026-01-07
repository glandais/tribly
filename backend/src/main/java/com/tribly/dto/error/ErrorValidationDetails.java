package com.tribly.dto.error;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RequiredArgsConstructor
@Getter
public class ErrorValidationDetails implements ErrorDetails {
  @Schema(description = "Type", required = true)
  final ErrorCode type = ErrorCode.VALIDATION;

  @Schema(description = "Field Errors", required = true)
  final List<FieldError> fieldErrors;
}
