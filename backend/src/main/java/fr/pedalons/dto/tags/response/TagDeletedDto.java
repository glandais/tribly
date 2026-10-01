package fr.pedalons.dto.tags.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** What deleting a tag did (plan D11). */
@Schema(description = "Result of a tag deletion")
@ValidateSchema
public record TagDeletedDto(
    @Schema(
            description =
                "Contents the tag was detached from, counted like usageCount (trashed contents"
                    + " lose it too, uncounted)",
            required = true)
        long detachedCount) {}
