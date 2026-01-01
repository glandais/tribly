package com.tribly.dto.ridetemplates.request;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride template request")
@ValidateSchema
public record RideTemplateRequest(
    @Schema(description = "Template name", required = true) @NotBlank @Size(min = 3, max = 200)
        String name,
    @Nullable @Schema(description = "Template description (markdown)") String markdown,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Default status for rides created from this template", required = true)
        Status status,
    @Schema(description = "Template groups", required = true)
        List<@Valid RideTemplateGroupRequest> groups) {}
