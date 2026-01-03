package com.tribly.dto.ads.request;

import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ad request")
@ValidateSchema
public record AdRequest(
    @Schema(description = "Ad name", required = true) @NotBlank @Size(min = 3, max = 200)
        String name,
    @Schema(description = "Ad description", required = true) MediaDto media,
    @Schema(description = "Ad date/time", required = true) Instant dateTime,
    @Schema(description = "Ad status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Ad type", required = true) @NotNull AdType adType,
    @Nullable @Schema(description = "Price (optional, null for 'contact for price')")
        BigDecimal price,
    @Nullable @Schema(description = "Rental period (required for RENTAL type)")
        RentalPeriod rentalPeriod,
    @Nullable @Schema(description = "Latitude") Double latitude,
    @Nullable @Schema(description = "Longitude") Double longitude,
    @Nullable @Schema(description = "Location description") @Size(max = 255)
        String locationDescription,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt) {}
