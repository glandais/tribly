package fr.pedalons.dto.common;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * How many rows a filter set matches, with none of them read.
 *
 * <p>Answers the "Voir N parcours" call to action of a filter sheet, which needs the count and
 * nothing else: the paginated listings already return the same {@code total}, but they pay for a
 * page of rows to get it.
 */
@Schema(description = "Number of items matching a filter set")
@ValidateSchema
public record CountResponse(
    @Schema(description = "Total number of matching items", required = true) long total) {}
