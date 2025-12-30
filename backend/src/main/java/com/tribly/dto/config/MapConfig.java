package com.tribly.dto.config;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Map configuration")
@ValidateSchema
public record MapConfig(
    @Schema(
            description = "Map tile URL template",
            examples = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            required = true)
        String tileUrl,
    @Schema(description = "Map attribution text", required = true) String attribution) {}
