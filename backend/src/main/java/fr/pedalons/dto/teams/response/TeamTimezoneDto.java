package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** The zone a point would give an event of the team (docs/LEDGER_*.md API-60). */
@Schema(description = "The zone of a point, else the team's")
@ValidateSchema
public record TeamTimezoneDto(
    @Schema(
            description =
                "IANA zone of the point; the team's own when no point is given or the point lies"
                    + " outside every zone",
            examples = "Asia/Tokyo",
            required = true)
        String timezone) {}
