package com.tribly.dto.error;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record FieldError(
    @Schema(description = "Field", required = true) String field,
    @Schema(description = "Message", required = true) String message,
    @Schema(description = "Rejected value", required = true) Object rejectedValue) {}
