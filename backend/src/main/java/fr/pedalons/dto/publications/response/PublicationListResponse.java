package fr.pedalons.dto.publications.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated publication list response")
@ValidateSchema
public record PublicationListResponse(
    @Schema(description = "List of publications", required = true)
        List<PublicationDto> publications,
    @Schema(description = "Total number of publications", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
