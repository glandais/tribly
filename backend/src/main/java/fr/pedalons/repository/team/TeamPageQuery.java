package fr.pedalons.repository.team;

import fr.pedalons.repository.common.TeamEntityQueryInterface;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamPageQuery(
    Long domainId,
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable Boolean includeAbout,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
