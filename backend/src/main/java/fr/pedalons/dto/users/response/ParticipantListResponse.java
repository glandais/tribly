package fr.pedalons.dto.users.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * One page of the people registered to a ride (or one of its groups) or to a trip.
 *
 * <p>The ride and trip details only embed a short preview; this is where the whole list is read,
 * searched and counted (docs/LEDGER_*.md API-12).
 */
@Schema(description = "Paginated list of the people registered to a ride, a ride group or a trip")
@ValidateSchema
public record ParticipantListResponse(
    @Schema(
            description = "Participants of this page, in registration order (earliest first)",
            required = true)
        List<PublicUserDto> participants,
    @Schema(
            description =
                "Number of participants matching the search, over every page — the M of « N of M »",
            required = true)
        long total,
    @Schema(description = "Current page number (0-based)", required = true) int page,
    @Schema(description = "Page size actually applied", required = true) int size) {}
