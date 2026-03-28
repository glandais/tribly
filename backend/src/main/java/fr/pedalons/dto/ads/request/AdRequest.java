package fr.pedalons.dto.ads.request;

import fr.pedalons.dto.common.GeoJsonPoint;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.RentalPeriod;
import fr.pedalons.enums.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ad request")
@ValidateSchema
public record AdRequest(
    @Schema(description = "Ad name", required = true) @NotBlank @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Ad description", required = true) @Valid MediaDto media,
    @Schema(description = "Ad status", required = true) Status status,
    @Schema(description = "Ad type", required = true) AdType adType,
    @Nullable @Schema(description = "Price (optional, null for 'contact for price')")
        BigDecimal price,
    @Nullable @Schema(description = "Rental period (required for RENTAL type)")
        RentalPeriod rentalPeriod,
    @Nullable @Schema(description = "Location description") @Size(max = 200)
        String locationDescription,
    @Nullable
        @Schema(
            description = "Location coordinates [longitude, latitude]",
            implementation = GeoJsonPoint.class)
        Point<G2D> locationGeometry) {}
