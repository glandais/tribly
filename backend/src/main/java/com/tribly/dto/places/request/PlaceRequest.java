package com.tribly.dto.places.request;

import com.tribly.dto.common.GeoJsonPoint;
import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.hibernate.validator.constraints.URL;
import org.jspecify.annotations.Nullable;

@Schema(description = "Place create/update request")
@ValidateSchema
public record PlaceRequest(
    @Schema(description = "Place name", required = true) @NotBlank @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Address") @Size(min = 3, max = 200) @Nullable String address,
    @Schema(description = "External link (e.g., Google Maps URL)") @Size(min = 3, max = 200) @URL
        @Nullable String link,
    @Schema(description = "Can be used as ride start point", required = true) boolean startPlace,
    @Schema(description = "Can be used as ride end point", required = true) boolean endPlace,
    @Schema(
            description = "Geographic coordinates [longitude, latitude]",
            implementation = GeoJsonPoint.class)
        @Nullable Point<G2D> coordinates) {}
