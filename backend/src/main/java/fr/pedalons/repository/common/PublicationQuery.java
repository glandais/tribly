package fr.pedalons.repository.common;

import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.service.team.request.MinRole;
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
    @Nullable Long pinnedTeamId,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    @Nullable MinRole minRole,
    int page,
    int size,
    boolean includeDeleted,
    boolean platformAdmin)
    implements TeamEntityQueryInterface {}
