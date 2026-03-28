package fr.pedalons.dto.ads.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated ad list response")
@ValidateSchema
public record AdListResponse(
    @Schema(description = "List of ads", required = true) List<AdDto> ads,
    @Schema(description = "Total number of ads", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
