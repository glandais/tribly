package fr.pedalons.dto.routes.request;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.WithVisibility;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route update request")
@ValidateSchema
public record RouteRequest(
    @Schema(description = "Route name", required = true) @NotBlank @Size(min = 3, max = 200)
        String name,
    @Schema(description = "Media", required = true) @Valid MediaDto media,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is publicly visible", required = true)
        Visibility visibility,
    @Nullable @Schema(description = "Points from frontend routing") List<GeoPoint> points)
    implements WithVisibility {}
