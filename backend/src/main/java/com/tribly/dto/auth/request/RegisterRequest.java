package com.tribly.dto.auth.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User registration request")
@ValidateSchema
@Builder
public record RegisterRequest(
    @NotBlank
        @Email
        @Size(max = 250)
        @Schema(description = "Email address", examples = "user@example.com")
        String email,
    @NotBlank @Size(min = 1, max = 200) @Schema(description = "Display name", examples = "John Doe")
        String displayName) {}
