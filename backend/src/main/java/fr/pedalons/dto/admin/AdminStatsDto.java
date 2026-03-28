package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Admin dashboard statistics")
@ValidateSchema
public record AdminStatsDto(
    @Schema(description = "Total number of domains", required = true) long totalDomains,
    @Schema(description = "Total number of teams", required = true) long totalTeams,
    @Schema(description = "Total number of users", required = true) long totalUsers) {}
