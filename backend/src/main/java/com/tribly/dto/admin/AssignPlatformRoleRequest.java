package com.tribly.dto.admin;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.PlatformRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to assign or remove platform role")
@ValidateSchema
public record AssignPlatformRoleRequest(
    @Nullable @Schema(description = "Platform role to assign (null to remove)")
        PlatformRole role) {}
