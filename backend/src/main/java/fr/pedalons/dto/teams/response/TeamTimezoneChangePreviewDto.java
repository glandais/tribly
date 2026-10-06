package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What changing the team's zone would do (docs/LEDGER_*.md API-60, plan §9): its rides, trips,
 * stages and posts that no place locates move to the new zone — the upcoming ones at constant wall
 * time, the past ones at constant instant.
 */
@Schema(description = "Preview of a change of the team's zone; nothing is written")
@ValidateSchema
public record TeamTimezoneChangePreviewDto(
    @Schema(description = "Team's current zone", examples = "Europe/Paris", required = true)
        String from,
    @Schema(description = "Zone asked for", examples = "America/Montreal", required = true)
        String to,
    @Schema(
            description = "How many upcoming place-less events keep their wall time",
            required = true)
        int upcomingCount,
    @Schema(
            description = "How many past place-less events keep their instant, relabelled",
            required = true)
        int pastCount,
    @Schema(
            description =
                "The first upcoming ones, soonest first, at most "
                    + TeamTimezoneChangePreviewDto.MAX_ITEMS,
            required = true)
        List<TeamTimezoneChangeItemDto> upcoming) {

  public static final int MAX_ITEMS = 10;
}
