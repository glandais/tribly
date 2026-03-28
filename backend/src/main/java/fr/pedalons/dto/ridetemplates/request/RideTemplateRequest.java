package fr.pedalons.dto.ridetemplates.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Ride template request")
@ValidateSchema
public record RideTemplateRequest(
    @Schema(description = "Template name", required = true) @NotBlank @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Template description (markdown)", required = true) String markdown,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Default status for rides created from this template", required = true)
        Status status,
    @Schema(description = "Template groups", required = true)
        List<@Valid RideTemplateGroupRequest> groups) {}
