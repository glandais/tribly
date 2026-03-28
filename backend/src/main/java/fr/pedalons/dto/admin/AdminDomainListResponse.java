package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated admin domain list response")
@ValidateSchema
public record AdminDomainListResponse(
    @Schema(description = "List of domains", required = true) List<AdminDomainDto> domains,
    @Schema(description = "Total number of domains", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
