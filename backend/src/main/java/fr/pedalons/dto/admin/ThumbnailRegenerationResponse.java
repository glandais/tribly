package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Outcome of a thumbnail regeneration, one entry per entity")
@ValidateSchema
public record ThumbnailRegenerationResponse(
    @Schema(description = "Whether this was a dry run (nothing redrawn)", required = true)
        boolean dryRun,
    @Schema(
            description = "Number of entities matching the criteria, before the limit",
            required = true)
        int matched,
    @Schema(description = "Entities processed, at most the limit", required = true)
        List<ThumbnailOwnerReport> entities) {}
