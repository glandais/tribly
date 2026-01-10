package com.tribly.dto.ads.request;

import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Status;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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
        String locationDescription) {}
