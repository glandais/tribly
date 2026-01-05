package com.tribly.dto.posts.request;

import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Post request")
@ValidateSchema
public record PostRequest(
    @Schema(description = "Post name", required = true) @NotBlank @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Post description", required = true) MediaDto media,
    @Schema(description = "Post date/time", required = true) Instant dateTime,
    @Schema(description = "Post status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt)
    implements WithVisibility {}
