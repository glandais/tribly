package com.tribly.dto.ridetemplates.response;

import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated ride template list response")
@ValidateSchema
public record RideTemplateListResponse(
    @Schema(description = "List of templates", required = true) List<RideTemplateDto> templates,
    @Schema(description = "Total number of templates", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
