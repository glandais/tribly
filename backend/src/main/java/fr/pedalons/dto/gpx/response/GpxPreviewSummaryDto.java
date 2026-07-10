package fr.pedalons.dto.gpx.response;

import fr.pedalons.domain.gpx.GpxPreview;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Lightweight view of an analysed GPX file for the "my files" list: the stats and identifiers only,
 * without the track and waypoint geometry that {@link GpxPreviewDto} carries.
 */
@Schema(description = "Summary of an analysed GPX file, for listing")
@ValidateSchema
public record GpxPreviewSummaryDto(
    @Schema(description = "Public identifier used in URLs", required = true) String id,
    @Schema(description = "Track name", required = true) String name,
    @Schema(description = "Distance in meters", required = true) Float distance,
    @Schema(description = "Total elevation gain in meters", required = true) Float elevationGain,
    @Schema(description = "Total elevation loss in meters", required = true) Float elevationLoss,
    @Schema(description = "Elevation gain per kilometer", required = true) Float hilliness,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt) {

  public static GpxPreviewSummaryDto from(GpxPreview preview) {
    return new GpxPreviewSummaryDto(
        preview.getPublicId().toString(),
        preview.getName(),
        preview.getDistance(),
        preview.getElevationGain(),
        preview.getElevationLoss(),
        preview.getHilliness(),
        preview.getCreatedAt());
  }
}
