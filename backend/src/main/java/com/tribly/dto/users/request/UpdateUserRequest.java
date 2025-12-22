package com.tribly.dto.users.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "User profile update request")
@ValidateSchema
public record UpdateUserRequest(
    @Nullable
        @Schema(description = "User display name", examples = "John Doe")
        @Size(min = 1, max = 255)
        String displayName) {}
