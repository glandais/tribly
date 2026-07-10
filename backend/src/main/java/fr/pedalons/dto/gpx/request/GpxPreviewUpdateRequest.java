package fr.pedalons.dto.gpx.request;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Update request for a GPX preview: rename it, and optionally redraw its track from planner points. A
 * new GPX file, when present, travels as a separate multipart part rather than in this JSON.
 */
@Schema(description = "GPX preview update request")
@ValidateSchema
public record GpxPreviewUpdateRequest(
    @Schema(description = "Preview name", required = true) @NotBlank @Size(min = 3, max = 250)
        String name,
    @Nullable @Schema(description = "Points from frontend routing") List<GeoPoint> points) {}
