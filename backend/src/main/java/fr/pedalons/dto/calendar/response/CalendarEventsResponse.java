package fr.pedalons.dto.calendar.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Calendar events response")
@ValidateSchema
public record CalendarEventsResponse(
    @Schema(description = "List of calendar events", required = true)
        List<CalendarEventDto> events) {}
