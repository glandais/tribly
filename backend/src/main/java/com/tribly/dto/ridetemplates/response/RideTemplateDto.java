package com.tribly.dto.ridetemplates.response;

import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Ride template response")
@ValidateSchema
public record RideTemplateDto(
    @Schema(description = "Template ID (TSID)", required = true) String id,
    @Schema(description = "Template slug", required = true) String slug,
    @Schema(description = "Template name", required = true) String name,
    @Schema(description = "Template description (markdown)", required = true) String markdown,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Default status", required = true) Status status,
    @Schema(description = "Creation timestamp", required = true) Instant createdAt,
    @Schema(description = "Last update timestamp", required = true) Instant updatedAt,
    @Schema(description = "Number of groups", required = true) int groupCount,
    @Schema(description = "Template groups", required = true) List<RideTemplateGroupDto> groups) {

  public static RideTemplateDto from(RideTemplate template) {
    List<RideTemplateGroupDto> groupDtos =
        template.getGroups().stream()
            .filter(g -> !g.isDeleted())
            .map(RideTemplateGroupDto::from)
            .toList();

    return new RideTemplateDto(
        TsidUtils.toString(template.getId()),
        template.getSlug(),
        template.getName(),
        template.getMarkdown(),
        template.getVisibility(),
        template.getStatus(),
        template.getCreatedAt(),
        template.getUpdatedAt(),
        groupDtos.size(),
        groupDtos);
  }
}
