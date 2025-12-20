package com.tribly.dto.rides.request;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to join a ride group")
@ValidateSchema
public record JoinGroupRequest(
    @Nullable @Schema(description = "Optional notes for the organizer", nullable = true)
        String notes) {}
