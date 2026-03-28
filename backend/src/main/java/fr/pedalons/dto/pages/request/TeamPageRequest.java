package fr.pedalons.dto.pages.request;

import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.WithVisibility;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team page request")
@ValidateSchema
public record TeamPageRequest(
    @Schema(description = "Page title", required = true) @NotBlank @Size(min = 1, max = 100)
        String title,
    @Schema(description = "Page content", required = true) @Valid MediaDto media,
    @Schema(description = "Visibility level", required = true) Visibility visibility)
    implements WithVisibility {}
