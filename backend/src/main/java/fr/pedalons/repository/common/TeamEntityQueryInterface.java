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

  @Nullable String slug();

  @Nullable String search();

  @Nullable Instant from();

  @Nullable Instant to();

  boolean includeDeleted();

  boolean platformAdmin();
}
