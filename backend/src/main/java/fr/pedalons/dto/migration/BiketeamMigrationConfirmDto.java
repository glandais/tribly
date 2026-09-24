package fr.pedalons.dto.migration;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A confirmed biketeam migration: where to send the browser back.
 *
 * <p>No class-level {@code @Schema}: SmallRye adds every type annotated with it to the OpenAPI
 * components, and the biketeam migration endpoints are hidden from the contract. The field-level
 * {@code required} flags stay, for {@link ValidateSchema}. Mirrored by hand in the frontend's
 * pages/biketeamMigration/biketeamMigrationApi.ts.
 */
@ValidateSchema
public record BiketeamMigrationConfirmDto(
    @Schema(
            description =
                "Biketeam's callback, carrying the request id and the single-use grant. The page"
                    + " navigates to it (window.location.assign).",
            required = true)
        String redirectUrl,
    @Schema(description = "When the grant stops being redeemable", required = true)
        Instant expiresAt) {}
