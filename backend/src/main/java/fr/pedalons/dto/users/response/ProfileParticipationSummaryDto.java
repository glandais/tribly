package fr.pedalons.dto.users.response;

import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The counts and the next outing of the profile's « Mes sorties ». Same rows as {@code GET
 * /api/users/me/participations}: only publications the user may still see.
 */
@Schema(description = "The rides and trips the current user is registered to, summed up")
@ValidateSchema
public record ProfileParticipationSummaryDto(
    @Schema(description = "Outings starting from now on", required = true) long upcomingCount,
    @Schema(description = "Outings that started before now", required = true) long pastCount,
    @Schema(
            description =
                "The next outing, in the compact list view (no markdown body): empty when nothing"
                    + " is coming up, never more than one row",
            required = true)
        List<PublicationDto> next) {}
