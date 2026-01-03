package com.tribly.dto.ads.response;

import com.tribly.domain.ad.Ad;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.publications.response.TeamPublicationDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.AssetService;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ad data")
@ValidateSchema
@Getter
public class AdDto {

  @Schema(description = "Team", required = true)
  final TeamPublicationDto team;

  @Schema(description = "Ad ID (TSID)", required = true)
  final String id;

  @Schema(description = "Ad URL slug", required = true)
  final String slug;

  @Schema(description = "Ad name", required = true)
  final String name;

  @Schema(description = "Ad media", required = true)
  final MediaDto media;

  @Schema(description = "Ad date/time", required = true)
  final Instant dateTime;

  @Schema(description = "Ad status", required = true)
  final Status status;

  @Schema(description = "Visibility level", required = true)
  final Visibility visibility;

  @Schema(description = "Ad type", required = true)
  final AdType adType;

  @Nullable
  @Schema(description = "Price")
  final BigDecimal price;

  @Nullable
  @Schema(description = "Rental period")
  final RentalPeriod rentalPeriod;

  @Nullable
  @Schema(description = "Latitude")
  final Double latitude;

  @Nullable
  @Schema(description = "Longitude")
  final Double longitude;

  @Nullable
  @Schema(description = "Location description")
  final String locationDescription;

  @Nullable
  @Schema(description = "Publication timestamp")
  final Instant publishAt;

  @Nullable
  @Schema(description = "Creation timestamp")
  final Instant createdAt;

  @Schema(description = "Creator ID (TSID)", required = true)
  final String createdById;

  public AdDto(
      TeamPublicationDto team,
      String id,
      String slug,
      String name,
      MediaDto media,
      Instant dateTime,
      Status status,
      Visibility visibility,
      AdType adType,
      @Nullable BigDecimal price,
      @Nullable RentalPeriod rentalPeriod,
      @Nullable Double latitude,
      @Nullable Double longitude,
      @Nullable String locationDescription,
      @Nullable Instant publishAt,
      @Nullable Instant createdAt,
      String createdById) {
    this.team = team;
    this.id = id;
    this.slug = slug;
    this.name = name;
    this.media = media;
    this.dateTime = dateTime;
    this.status = status;
    this.visibility = visibility;
    this.adType = adType;
    this.price = price;
    this.rentalPeriod = rentalPeriod;
    this.latitude = latitude;
    this.longitude = longitude;
    this.locationDescription = locationDescription;
    this.publishAt = publishAt;
    this.createdAt = createdAt;
    this.createdById = createdById;
  }

  public static AdDto from(Ad ad, AssetService assetService) {
    return new AdDto(
        TeamPublicationDto.from(ad.getTeam()),
        TsidUtils.toString(ad.getId()),
        ad.getSlug(),
        ad.getName(),
        MediaDto.from(ad, assetService),
        ad.getDateTime(),
        ad.getStatus(),
        ad.getVisibility(),
        ad.getAdType(),
        ad.getPrice(),
        ad.getRentalPeriod(),
        ad.getLatitude(),
        ad.getLongitude(),
        ad.getLocationDescription(),
        ad.getPublishAt(),
        ad.getCreatedAt(),
        TsidUtils.toString(ad.getCreatedBy().getId()));
  }
}
