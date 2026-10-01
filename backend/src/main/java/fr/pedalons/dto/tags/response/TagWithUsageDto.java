package fr.pedalons.dto.tags.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TagColor;
import fr.pedalons.enums.TagTarget;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** A tag of the team's vocabulary, with how many contents carry it (plan D12). */
@Schema(description = "A team tag with its usage count")
@ValidateSchema
public record TagWithUsageDto(
    @Schema(description = "Tag ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Label, at most 32 characters", required = true) String label,
    @Schema(description = "Colour family", required = true) TagColor color,
    @Schema(description = "Kind of content the tag applies to", required = true) TagTarget type,
    @Schema(
            description =
                "Contents carrying the tag: rides, posts, trips, routes and ads out of the trash,"
                    + " plus ride templates — what a deletion would detach it from",
            required = true)
        long usageCount) {

  public static TagWithUsageDto from(Tag tag, long usageCount) {
    return new TagWithUsageDto(
        TsidUtils.toString(tag.getId()), tag.getLabel(), tag.getColor(), tag.getType(), usageCount);
  }
}
