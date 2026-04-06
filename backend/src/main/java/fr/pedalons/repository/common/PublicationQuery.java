package fr.pedalons.repository.common;

import fr.pedalons.dto.publications.response.PublicationType;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PublicationQuery(
    Long domainId,
    @Nullable PublicationType type,
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size,
    boolean includeDeleted)
    implements TeamEntityQueryInterface {}
