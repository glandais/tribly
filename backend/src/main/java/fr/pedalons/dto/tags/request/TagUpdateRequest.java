package fr.pedalons.dto.tags.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TagColor;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * Renames and/or recolours a tag; an absent field is left as it is. The kind never changes. As on
 * creation, the label's 32 characters are counted once it is trimmed, by {@code
 * TagService.normalizeLabel} ({@code TAG_LABEL_INVALID}).
 */
@Schema(description = "Tag update request — absent fields are unchanged")
@ValidateSchema
public record TagUpdateRequest(
    @Schema(
            description =
                "New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID"
                    + " otherwise)")
        @Size(max = TagCreateRequest.LABEL_SANITY_CAP)
        @Nullable String label,
    @Schema(description = "New colour family") @Nullable TagColor color) {}
