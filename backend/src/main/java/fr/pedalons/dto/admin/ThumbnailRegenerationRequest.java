package fr.pedalons.dto.admin;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(
    description =
        "Which map thumbnails to redraw from the geometry already stored. The criteria combine: the"
            + " date window and the size threshold narrow the thumbnails that exist, the 'missing'"
            + " flag adds the entities that have none. At least one criterion is required.")
@ValidateSchema
public record ThumbnailRegenerationRequest(
    @Schema(
            description = "Only thumbnails drawn at or after this instant",
            examples = "2026-09-14T16:00:00Z")
        @Nullable Instant renderedFrom,
    @Schema(
            description = "Only thumbnails drawn before this instant",
            examples = "2026-09-25T01:00:00Z")
        @Nullable Instant renderedTo,
    @Schema(
            description =
                "Only thumbnails whose stored file is smaller than this many bytes, or has no file."
                    + " A map drawn without its background weighs a few kB, a real one tens.",
            examples = "10000",
            minimum = "1")
        @Nullable Integer suspectBelowBytes,
    @Schema(
            description =
                "Also redraw the routes, rides and trips that have a route to draw but lack a"
                    + " light or dark thumbnail — what a failed render leaves behind",
            examples = "false")
        @Nullable Boolean includeMissing,
    @Schema(
            description = "List what would be redrawn, without redrawing anything",
            examples = "true",
            required = true)
        boolean dryRun,
    @Schema(
            description = "Redraw at most this many entities (default 100)",
            examples = "100",
            minimum = "1",
            maximum = "1000")
        @Nullable Integer limit) {}
