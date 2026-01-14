package com.tribly.dto.auth.response;

import com.tribly.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Simple message response")
@Builder
@ValidateSchema
public record MessageResponse(@Schema(description = "Response message") String message) {}
