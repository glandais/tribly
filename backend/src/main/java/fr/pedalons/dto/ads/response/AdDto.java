package fr.pedalons.dto.ads.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.RentalPeriod;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.asset.AssetService;
import java.math.BigDecimal;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ad data")
@ValidateSchema
public record AdDto(
    @Schema(description = "Team", required = true) TeamPublicationDto team,
    @Schema(description = "Ad ID (TSID)", required = true) String id,
    @Schema(description = "Ad URL slug", required = true) String slug,
    @Schema(description = "Ad name", required = true) String name,
    @Schema(description = "Ad media", required = true) MediaDto media,
    @Schema(description = "Ad status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Ad type", required = true) AdType adType,
    @Schema(description = "Price") @Nullable BigDecimal price,
    @Schema(description = "Rental period") @Nullable RentalPeriod rentalPeriod,
    @Schema(description = "Location description") @Nullable String locationDescription,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Schema(description = "Creation timestamp", required = true) Instant updatedAt,
    @Schema(description = "Creator ID (TSID)", required = true) String createdById) {

  public static AdDto from(Ad ad, AssetService assetService) {
    return new AdDto(
        TeamPublicationDto.from(ad.getTeam()),
        TsidUtils.toString(ad.getId()),
        ad.getSlug(),
        ad.getName(),
        MediaDto.from(ad, assetService),
        ad.getStatus(),
        ad.getVisibility(),
        ad.getAdType(),
        ad.getPrice(),
        ad.getRentalPeriod(),
        ad.getLocationDescription(),
        ad.getCreatedAt(),
        ad.getUpdatedAt(),
        TsidUtils.toString(ad.getCreatedBy().getId()));
  }
}
