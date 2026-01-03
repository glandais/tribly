package com.tribly.dto.teams.request;

import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team creation request")
@ValidateSchema
public record TeamRequest(
    @Schema(description = "Team name", examples = "Awesome Cycling Team", required = true)
        @NotBlank
        @Size(min = 2, max = 200)
        String name,
    @Schema(description = "Media", required = true) MediaDto media,
    @Schema(description = "Team visibility", examples = "PUBLIC", required = true)
        Visibility visibility,
    @Schema(description = "Trips enabled for team", examples = "true", required = true)
        boolean enableTrips,
    @Schema(description = "Ads enabled for team", examples = "true", required = true)
        boolean enableAds) {}
