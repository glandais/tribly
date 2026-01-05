package com.tribly.dto.pages.request;

import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team page request")
@ValidateSchema
public record TeamPageRequest(
    @Schema(description = "Page title", required = true) @NotBlank @Size(min = 1, max = 100)
        String title,
    @Schema(description = "Page content", required = true) MediaDto media,
    @Schema(description = "Visibility level", required = true) Visibility visibility)
    implements WithVisibility {}
