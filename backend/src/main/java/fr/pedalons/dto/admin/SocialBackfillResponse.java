package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Result of a social-identity backfill")
@ValidateSchema
public record SocialBackfillResponse(
    @Schema(description = "Number of identity rows created", required = true)
        int identitiesCreated) {}
