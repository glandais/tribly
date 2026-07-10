package fr.pedalons.repository.common;

import fr.pedalons.service.team.request.MinRole;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface TeamEntityQueryInterface extends PageInterface {
  Long domainId();

  /**
   * Restrict to the teams the current user belongs to with at least this role. Null means no
   * membership restriction, which is what every query that does not offer the filter wants.
   */
  default @Nullable MinRole minRole() {
    return null;
  }

  @Nullable Long id();

  @Nullable Long userId();

  @Nullable Set<Long> teamIds();

  /**
   * The team this site is pinned to, when the request arrived on a domain alias. A site scope, not a
   * user filter: it narrows every query the way {@link #domainId()} does, and it ANDs with {@link
   * #teamIds()} rather than replacing it. Null on a regular domain.
   */
  @Nullable Long pinnedTeamId();

  @Nullable String slug();

  @Nullable String search();

  @Nullable Instant from();

  @Nullable Instant to();

  boolean includeDeleted();

  boolean platformAdmin();
}
