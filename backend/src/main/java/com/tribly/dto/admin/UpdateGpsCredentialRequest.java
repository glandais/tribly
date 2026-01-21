package com.tribly.dto.admin;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to update a GPS credential")
@ValidateSchema
public record UpdateGpsCredentialRequest(
    @NotBlank @Size(max = 255) @Schema(description = "OAuth client ID", required = true)
        String clientId,
    @Nullable @Size(max = 500) @Schema(description = "OAuth client secret (null = keep current)")
        String clientSecret,
    @Schema(description = "Whether credential is active") boolean active) {}
