package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/** One upcoming event a team zone change keeps at its wall time (docs/LEDGER_*.md API-60). */
@Schema(description = "An upcoming place-less event that keeps its wall time in the new zone")
@ValidateSchema
public record TeamTimezoneChangeItemDto(
    @Schema(description = "Kind of event", required = true) TimezoneChangeEntityType type,
    @Schema(description = "Event ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Event URL slug", required = true) String slug,
    @Schema(description = "Event title; a stage's own name", required = true) String title,
    @Schema(description = "For a stage, the title of its trip") @Nullable String tripTitle,
    @Schema(
            description =
                "Start, as stored today: read it in the team's current zone for the wall time it"
                    + " keeps",
            required = true)
        Instant dateTime) {}
