package com.tribly.dto.gps.response;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Result of uploading a route to a GPS service")
@ValidateSchema
public record RouteUploadResponse(
    @Schema(description = "Whether the upload was successful", required = true) boolean success,
    @Nullable @Schema(description = "Error message if upload failed") String message,
    @Nullable @Schema(description = "External route ID on the GPS service")
        String externalRouteId) {}
