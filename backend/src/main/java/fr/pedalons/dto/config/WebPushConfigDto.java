package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What the site needs to obtain an FCM token in a browser. Every value here is public by design —
 * Firebase's web configuration ships in every page that uses it — so it rides on the anonymous
 * {@code GET /api/config}.
 */
@Schema(description = "Firebase web configuration, for push notifications in the browser")
@ValidateSchema
public record WebPushConfigDto(
    @Schema(description = "Firebase web API key", required = true) String apiKey,
    @Schema(description = "Firebase project id, the one the server sends through", required = true)
        String projectId,
    @Schema(description = "Firebase web app id", required = true) String appId,
    @Schema(description = "FCM sender id (the project number)", required = true)
        String messagingSenderId,
    @Schema(description = "Public VAPID key of the project's web push certificate", required = true)
        String vapidKey) {}
