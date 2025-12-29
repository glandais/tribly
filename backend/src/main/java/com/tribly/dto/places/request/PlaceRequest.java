package com.tribly.dto.places.request;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Place create/update request")
@ValidateSchema
public record PlaceRequest(
    @Schema(description = "Place name", required = true) String name,
    @Schema(description = "Address") @Nullable String address,
    @Schema(description = "External link (e.g., Google Maps URL)") @Nullable String link,
    @Schema(description = "Can be used as ride start point", required = true) boolean startPlace,
    @Schema(description = "Can be used as ride end point", required = true) boolean endPlace,
    @Schema(description = "Geographic coordinates [longitude, latitude]")
        @Nullable double[] coordinates) {}
