package fr.pedalons.repository.ad;

import fr.pedalons.enums.AdSortBy;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.repository.common.TeamEntityQueryInterface;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record AdQuery(
    Long domainId,
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long pinnedTeamId,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    @Nullable AdType adType,
    // Price range. An ad with no price ("à négocier") drops out of either bound — a SQL NULL
    // comparison is UNKNOWN — which is the intended reading of "under 500 €".
    @Nullable BigDecimal minPrice,
    @Nullable BigDecimal maxPrice,
    // Proximity, on the ad's own location. An ad with no location drops out of the filter.
    @Nullable Double nearLat,
    @Nullable Double nearLon,
    @Nullable Double nearRadius,
    // Sorting
    @Nullable AdSortBy sortBy,
    @Nullable SortDirection sortDir,
    int page,
    int size,
    boolean includeDeleted,
    boolean platformAdmin)
    implements TeamEntityQueryInterface {}
