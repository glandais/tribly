package fr.pedalons.dto.ads.request;

import fr.pedalons.enums.AdSortBy;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.SortDirection;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * What the classifieds listing filters and sorts on.
 *
 * <p>Grouped in a record rather than spelled out in the service signature, for the reason the route
 * search discovered the hard way: filters accrete, and every new one otherwise touches every caller.
 * Mirrors {@code RouteSearchParams}.
 */
@Builder
public record AdSearchParams(
    @Nullable String search,
    @Nullable AdType adType,
    @Nullable Instant from,
    @Nullable Instant to,
    @Nullable BigDecimal minPrice,
    @Nullable BigDecimal maxPrice,
    @Nullable Double nearLat,
    @Nullable Double nearLon,
    @Nullable Double nearRadius,
    @Nullable AdSortBy sortBy,
    @Nullable SortDirection sortDir) {}
