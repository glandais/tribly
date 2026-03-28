package fr.pedalons.dto.common.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Slug change request")
@ValidateSchema
public record SlugChangeRequest(
    @Schema(
            description = "New slug (lowercase letters, numbers, and hyphens only)",
            required = true,
            examples = "my-new-slug")
        @NotBlank
        @Size(max = 200)
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$")
        String slug) {}
