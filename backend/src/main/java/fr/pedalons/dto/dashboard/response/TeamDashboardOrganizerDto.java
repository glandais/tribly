package fr.pedalons.dto.dashboard.response;

import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.ridetemplates.response.RideTemplateListResponse;
import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/** The « À traiter » band and the templates panel of an organizer's dashboard. */
@Schema(
    description =
        "The organizer part of a team dashboard: the « À traiter » tiles and the ride templates."
            + " Each list is a short page (at most 5 rows); its total is the tile's figure.")
@ValidateSchema
public record TeamDashboardOrganizerDto(
    @Schema(
            description =
                "The team's drafts (rides, posts, trips), newest first. total is the number of"
                    + " drafts, the rows their names.",
            required = true)
        PublicationListResponse drafts,
    @Nullable
        @Schema(
            description =
                "Published rides starting from now routed nowhere — neither the ride nor any of"
                    + " its groups has a route — soonest first. Same rows as GET"
                    + " …/publications?type=RIDE&withoutRoute=true. Null when rides are disabled.")
        PublicationListResponse ridesWithoutRoute,
    @Nullable
        @Schema(
            description =
                "Published rides starting from now with at least one group at capacity, soonest"
                    + " first. Same rows as GET …/publications?type=RIDE&withFullGroup=true. Null"
                    + " when rides are disabled.")
        PublicationListResponse ridesWithFullGroup,
    @Schema(description = "The open reports of the team's moderation queue", required = true)
        TeamDashboardReportsDto reports,
    @Nullable
        @Schema(
            description =
                "« Créer depuis un modèle »: the team's ride templates (at most 5), each with its"
                    + " groupCount. Null when rides are disabled.")
        RideTemplateListResponse rideTemplates) {}
