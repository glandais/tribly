package fr.pedalons.dto.users.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.UnitSystem;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "User profile update request")
@ValidateSchema
@Builder
public record UpdateUserRequest(
    @Nullable
        @Schema(description = "User display name", examples = "John Doe")
        @Size(min = 1, max = 200)
        String displayName,
    @Nullable @Schema(description = "Preferred unit system", examples = "metric")
        UnitSystem unitSystem) {}
