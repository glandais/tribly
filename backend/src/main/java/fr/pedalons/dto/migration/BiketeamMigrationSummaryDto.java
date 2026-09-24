package fr.pedalons.dto.migration;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What biketeam says it will send, as signed in the request: live items only (deleted items
 * excluded).
 *
 * <p>No class-level {@code @Schema}: SmallRye adds every type annotated with it to the OpenAPI
 * components, and the biketeam migration endpoints are hidden from the contract. The field-level
 * {@code required} flags stay, for {@link ValidateSchema}. Mirrored by hand in the frontend's
 * pages/biketeamMigration/biketeamMigrationApi.ts.
 */
@ValidateSchema
public record BiketeamMigrationSummaryDto(
    @Schema(description = "Places", required = true) int places,
    @Schema(description = "Routes (biketeam maps)", required = true) int routes,
    @Schema(description = "Rides", required = true) int rides,
    @Schema(description = "Ride templates", required = true) int rideTemplates,
    @Schema(description = "Trips", required = true) int trips,
    @Schema(description = "Trip stages", required = true) int tripStages,
    @Schema(description = "Publications, which become posts", required = true) int publications,
    @Schema(description = "Whether the team has a FAQ page", required = true) boolean faqPage,
    @Schema(description = "Whether the team has a logo file", required = true) boolean logo) {}
