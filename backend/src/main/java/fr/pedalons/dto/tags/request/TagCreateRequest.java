package fr.pedalons.dto.tags.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TagColor;
import fr.pedalons.enums.TagTarget;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Creates a tag. The 32-character limit of a label (plan D16) holds once it is trimmed, so it is
 * {@code TagService.normalizeLabel}'s to check, with {@code TAG_LABEL_INVALID}; {@code @Size} here
 * only caps what a request may carry.
 */
@Schema(description = "Tag creation request")
@ValidateSchema
public record TagCreateRequest(
    @Schema(description = "Kind of content the tag applies to", required = true) @NotNull
        TagTarget type,
    @Schema(
            description =
                "Label, trimmed; unique in the team and kind whatever the case, at most 32"
                    + " characters once trimmed (TAG_LABEL_INVALID otherwise)",
            required = true)
        @NotNull
        @Size(max = TagCreateRequest.LABEL_SANITY_CAP)
        String label,
    @Schema(description = "Colour family", required = true) @NotNull TagColor color) {

  /** Raw length a label may have before it is trimmed; the real limit, 32, applies after. */
  public static final int LABEL_SANITY_CAP = 255;
}
