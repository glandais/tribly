package com.tribly.dto.admin;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to update a domain")
@ValidateSchema
public record UpdateDomainRequest(
    @NotBlank @Size(max = 250) @Schema(description = "Domain display name", required = true)
        String name,
    @NotBlank @Size(max = 500) @Schema(description = "Base URL for the domain", required = true)
        String baseUrl,
    @Schema(description = "Whether domain is single-team mode") boolean singleTeam) {}
