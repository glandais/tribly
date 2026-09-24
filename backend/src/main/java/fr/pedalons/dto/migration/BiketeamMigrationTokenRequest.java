package fr.pedalons.dto.migration;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A signed biketeam migration request.
 *
 * <p>No class-level {@code @Schema}: SmallRye adds every type annotated with it to the OpenAPI
 * components, and the biketeam migration endpoints are hidden from the contract. The field-level
 * {@code required} flags stay, for {@link ValidateSchema}. Mirrored by hand in the frontend's
 * pages/biketeamMigration/biketeamMigrationApi.ts.
 */
@ValidateSchema
public record BiketeamMigrationTokenRequest(
    @Schema(
            description =
                "The request token biketeam put in the ?request= query parameter of the"
                    + " migration page, as received.",
            required = true)
        @NotBlank
        @Size(max = 8000)
        String requestToken) {}
