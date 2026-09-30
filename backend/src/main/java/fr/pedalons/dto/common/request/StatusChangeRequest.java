package fr.pedalons.dto.common.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Changes the status of a publication or an ad and nothing else (docs/LEDGER_DONE.md WEB-33).
 *
 * <p>A list row is a compact projection: sending it back through the full update would drop what it
 * does not carry — a ride's groups, a trip's stages. This request carries the status alone.
 */
@Schema(description = "Status change request")
@ValidateSchema
public record StatusChangeRequest(
    @Schema(description = "New status", required = true) @NotNull Status status) {}
