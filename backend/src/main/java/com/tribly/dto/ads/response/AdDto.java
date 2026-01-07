package com.tribly.dto.ads.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.ad.Ad;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.service.asset.AssetService;
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
    @Schema(description = "Latitude") @Nullable Double latitude,
    @Schema(description = "Longitude") @Nullable Double longitude,
    @Schema(description = "Location description") @Nullable String locationDescription,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Schema(description = "Creation timestamp", required = true) Instant updatedAt,
    @Schema(description = "Creator ID (TSID)", required = true) String createdById) {

  public AdDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      Status status,
      Visibility visibility,
      AdType adType,
      @Nullable BigDecimal price,
      @Nullable RentalPeriod rentalPeriod,
      @Nullable Double latitude,
      @Nullable Double longitude,
      @Nullable String locationDescription,
      Instant createdAt,
      Instant updatedAt,
      String createdById) {
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.status = status;
    this.visibility = visibility;
    this.adType = adType;
    this.price = price;
    this.rentalPeriod = rentalPeriod;
    this.latitude = latitude;
    this.longitude = longitude;
    this.locationDescription = locationDescription;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.createdById = createdById;
  }

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
        ad.getLatitude(),
        ad.getLongitude(),
        ad.getLocationDescription(),
        ad.getCreatedAt(),
        ad.getUpdatedAt(),
        TsidUtils.toString(ad.getCreatedBy().getId()));
  }
}
