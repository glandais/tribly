package fr.pedalons.dto.sitemap;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Every page of the site a search engine may index: the public content of public teams,"
            + " without classified ads nor routes. Capped at 50,000 entries, the sitemap protocol's"
            + " limit; newest first within each type.")
@ValidateSchema
public record SitemapDto(
    @Schema(description = "Indexable pages", required = true) List<SitemapEntryDto> entries) {}
