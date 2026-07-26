package fr.pedalons.repository.common;

import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.Status;
import fr.pedalons.service.team.request.MinRole;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * @param status narrows the listing to a single status. This <b>intersects</b> the visibility rules
 *     applied by {@code TeamEntityRepository.getPedalonsQuery}; it never widens them, so asking for
 *     {@code DRAFT} as a plain member still yields nothing.
 * @param participating keep only the publications the user of {@link #userId()} is registered to.
 *     Yields nothing for an anonymous caller, the same way {@link #minRole()} does.
 * @param ascending order by {@code dateTime} ascending instead of the default descending — what "my
 *     next outing" needs.
 */
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
    @Nullable Status status,
    boolean participating,
    boolean ascending,
    int page,
    int size,
    boolean includeDeleted,
    boolean platformAdmin)
    implements TeamEntityQueryInterface {}
