package fr.pedalons.dto.gpx.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Paginated list of the current user's analysed GPX files. */
@Schema(description = "Paginated GPX preview list response")
@ValidateSchema
public record GpxPreviewListResponse(
    @Schema(description = "List of GPX previews", required = true)
        List<GpxPreviewSummaryDto> previews,
    @Schema(description = "Total number of previews", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
