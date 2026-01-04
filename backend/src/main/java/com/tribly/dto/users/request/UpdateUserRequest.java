package com.tribly.dto.users.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User profile update request")
@ValidateSchema
public record UpdateUserRequest(
    @Schema(description = "User display name", examples = "John Doe", required = true)
        @Size(min = 1, max = 200)
        String displayName) {}
