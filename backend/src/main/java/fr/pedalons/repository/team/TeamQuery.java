package fr.pedalons.repository.team;

import fr.pedalons.repository.common.PageInterface;
import fr.pedalons.service.team.request.MinRole;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamQuery(
    int page,
    int size,
    Long domainId,
    @Nullable Long id,
    @Nullable Long userId,
    @Nullable MinRole minRole,
    @Nullable String search,
    boolean includeDeleted)
    implements PageInterface {}
