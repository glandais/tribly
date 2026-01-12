package com.tribly.dto.calendar.response;

import com.tribly.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Calendar token information")
@ValidateSchema
public record CalendarTokenDto(
    @Schema(description = "ICS feed token (include in URL)", required = true) String token,
    @Schema(description = "Global ICS feed URL", required = true) String globalFeedUrl,
    @Schema(description = "Team-specific feed URL template", required = true)
        String teamFeedUrlTemplate) {}
