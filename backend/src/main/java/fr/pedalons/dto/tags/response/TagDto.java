package fr.pedalons.dto.tags.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TagColor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** A tag as a content carries it (docs/LEDGER_*.md API-59). */
@Schema(description = "A team tag on a content")
@ValidateSchema
public record TagDto(
    @Schema(description = "Tag ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Label, at most 32 characters", required = true) String label,
    @Schema(description = "Colour family", required = true) TagColor color) {

  public static TagDto from(Tag tag) {
    return new TagDto(TsidUtils.toString(tag.getId()), tag.getLabel(), tag.getColor());
  }
}
