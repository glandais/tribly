package fr.pedalons.dto.calendar.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * One row of an agenda: enough to draw a calendar cell and an agenda card without going back to the
 * server for the ride behind it.
 *
 * <p>The first ten components are the original event identity (who, when, where to navigate). The
 * ones after are the render payload — start place, distance, elevation, thumbnail, status — plus the
 * two "me" fields, {@code registered} and {@code groupName}. Every added component is nullable or
 * defaulted, so a client built against the ten-field shape keeps working.
 *
 * <p>Because {@code registered} and {@code groupName} answer "am <em>I</em> signed up?", any
 * response carrying this DTO is specific to the caller and must not be stored by a shared cache.
 */
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
    @Nullable @Schema(description = "Parent trip slug (for stages only)") String tripSlug,
    @Nullable
        @Schema(
            description =
                "Name of the meeting place, null when the ride or stage has no start place")
        String startPlaceName,
    @Nullable
        @Schema(
            description = "Distance in meters of the attached route, null when there is no route")
        Float distance,
    @Nullable
        @Schema(
            description =
                "Total elevation gain in meters of the attached route, null when there is no route")
        Float elevationGain,
    @Nullable
        @Schema(
            description =
                "Thumbnail image URL template (contains a {size} placeholder). Falls back to the"
                    + " route's thumbnail when the ride or stage has none of its own.")
        String thumbnailUrl,
    @Schema(
            description =
                "Whether the current user is registered to this ride, or to the trip this stage"
                    + " belongs to. False for an anonymous caller.",
            required = true)
        boolean registered,
    @Nullable
        @Schema(
            description =
                "Name of the ride group the current user joined. Null when not registered, and"
                    + " always null for trip stages, which have no groups.")
        String groupName,
    @Schema(description = "Publication status of the ride or stage", required = true)
        Status status) {}
