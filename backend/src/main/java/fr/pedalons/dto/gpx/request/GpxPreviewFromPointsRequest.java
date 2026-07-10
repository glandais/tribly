package fr.pedalons.dto.gpx.request;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Creation request for a GPX preview drawn from scratch with the planner: no GPX file, just a name
 * and the already-routed points. A sibling of {@link GpxPreviewUpdateRequest}, but here the points
 * are mandatory — there is nothing else to build the track from.
 */
@Schema(description = "GPX preview creation request from planner points")
@ValidateSchema
public record GpxPreviewFromPointsRequest(
    @Schema(description = "Preview name", required = true) @NotBlank @Size(min = 3, max = 250)
        String name,
    @Schema(description = "Points from frontend routing", required = true) @NotEmpty
        List<GeoPoint> points) {}
