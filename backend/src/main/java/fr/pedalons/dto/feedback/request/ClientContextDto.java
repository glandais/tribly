package fr.pedalons.dto.feedback.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.ClientPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Where a feedback or an error report comes from: client, device, screen")
@ValidateSchema
public record ClientContextDto(
    @Schema(description = "The client", required = true) ClientPlatform platform,
    @Schema(description = "Version of the client, e.g. 1.0.0 or a git commit", required = true)
        @NotBlank
        @Size(max = 50)
        String appVersion,
    @Nullable @Schema(description = "Build number of a mobile client") @Size(max = 50)
        String buildNumber,
    @Nullable @Schema(description = "OS name and version, e.g. Android 15") @Size(max = 100)
        String osVersion,
    @Nullable @Schema(description = "Device model") @Size(max = 200) String device,
    @Nullable @Schema(description = "Browser user agent") @Size(max = 500) String userAgent,
    @Nullable
        @Schema(description = "Path of the current page or screen, without its query string")
        @Size(max = 500)
        String route,
    @Nullable @Schema(description = "UI language, e.g. fr") @Size(max = 20) String locale,
    @Nullable @Schema(description = "IANA time zone, e.g. Europe/Paris") @Size(max = 64)
        String timezone,
    @Nullable @Schema(description = "Slug of the team being browsed, if any") @Size(max = 100)
        String teamSlug) {}
