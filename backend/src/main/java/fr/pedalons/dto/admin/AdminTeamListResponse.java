package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated admin team list response")
@ValidateSchema
public record AdminTeamListResponse(
    @Schema(description = "List of teams", required = true) List<AdminTeamDto> teams,
    @Schema(description = "Total number of teams", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
