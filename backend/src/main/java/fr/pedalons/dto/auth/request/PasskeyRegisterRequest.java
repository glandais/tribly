package fr.pedalons.dto.auth.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Request to register a new passkey with device name")
@ValidateSchema
@Builder
public record PasskeyRegisterRequest(
    @Nullable
        @Size(max = 250)
        @Schema(description = "Optional device name for this passkey", examples = "MacBook Pro")
        String deviceName) {}
