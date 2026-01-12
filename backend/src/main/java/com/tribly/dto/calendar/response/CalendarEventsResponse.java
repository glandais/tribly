package com.tribly.dto.calendar.response;

import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Calendar events response")
@ValidateSchema
public record CalendarEventsResponse(
    @Schema(description = "List of calendar events", required = true)
        List<CalendarEventDto> events) {}
