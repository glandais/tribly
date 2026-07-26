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
    @Nullable Long pinnedTeamId,
    @Nullable Long id,
    @Nullable Long userId,
    @Nullable MinRole minRole,
    @Nullable String search,
    /**
     * Restricts to teams that do, or do not, accept a join request from any domain user. Null keeps
     * both. A filter only — it never widens what the visibility rules already allow.
     */
    @Nullable Boolean joinable,
    boolean platformAdmin)
    implements PageInterface {}
