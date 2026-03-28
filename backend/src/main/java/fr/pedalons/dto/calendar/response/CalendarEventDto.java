package fr.pedalons.dto.calendar.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Calendar event data")
@ValidateSchema
public record CalendarEventDto(
    @Schema(description = "Event ID (TSID)", required = true) String id,
    @Schema(description = "Event title", required = true) String title,
    @Schema(description = "Event start date/time", required = true) Instant start,
    @Nullable @Schema(description = "Event end date/time") Instant end,
    @Schema(description = "Is all-day event", required = true) boolean allDay,
    @Schema(description = "Event type", required = true) CalendarEventType type,
    @Schema(description = "Team slug", required = true) String teamSlug,
    @Schema(description = "Team name", required = true) String teamName,
    @Schema(description = "Entity slug (ride or stage)", required = true) String entitySlug,
    @Nullable @Schema(description = "Parent trip slug (for stages only)") String tripSlug) {}
