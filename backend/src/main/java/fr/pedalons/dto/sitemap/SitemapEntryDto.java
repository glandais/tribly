package fr.pedalons.dto.sitemap;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(
    description =
        "One indexable page. The API returns slugs, not URLs: the path is localised and belongs to"
            + " the client's route table.")
@ValidateSchema
public record SitemapEntryDto(
    @Schema(description = "Which page this is", required = true) SitemapEntryType type,
    @Schema(description = "Slug of the team the page belongs to", required = true) String teamSlug,
    @Nullable
        @Schema(description = "Slug of the trip a TRIP_STAGE belongs to; null for every other type")
        String tripSlug,
    @Nullable @Schema(description = "Slug of the page itself; null for TEAM and TEAM_ABOUT")
        String slug,
    @Schema(description = "Last modification of the page's content", required = true)
        Instant lastModified) {}
