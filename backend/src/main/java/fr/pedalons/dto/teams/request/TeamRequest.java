package fr.pedalons.dto.teams.request;

import fr.pedalons.dto.common.GeoJsonPoint;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@Schema(description = "Team creation request")
@ValidateSchema
public record TeamRequest(
    @Schema(description = "Team name", examples = "Awesome Cycling Team", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Media", required = true) @Valid MediaDto media,
    @Schema(description = "Team visibility", examples = "PUBLIC", required = true)
        Visibility visibility,
    @Schema(description = "Trips enabled for team", examples = "true", required = true)
        boolean enableTrips,
    @Schema(description = "Ads enabled for team", examples = "true", required = true)
        boolean enableAds,
    @Schema(description = "Posts enabled for team", examples = "true", required = true)
        boolean enablePosts,
    @Schema(description = "Rides enabled for team", examples = "true", required = true)
        boolean enableRides,
    @Schema(description = "Routes enabled for team", examples = "true", required = true)
        boolean enableRoutes,
    @Schema(
            description =
                "Member directory readable by every member, not just administrators. Organisers"
                    + " always see the directory; what this flag adds for them is the role and join"
                    + " date of each member.",
            examples = "false",
            required = true)
        boolean enableMemberDirectory,
    @Schema(
            description = "Team location coordinates [longitude, latitude]",
            implementation = GeoJsonPoint.class)
        @Nullable Point<G2D> geometry) {}
