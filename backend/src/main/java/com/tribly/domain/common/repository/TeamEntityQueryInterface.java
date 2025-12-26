package com.tribly.domain.common.repository;

import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface TeamEntityQueryInterface extends PageInterface {
  @Nullable String search();

  @Nullable String slug();

  Set<Long> memberTeamIds();

  Set<Long> organizerTeamIds();

  @Nullable Instant from();

  @Nullable Instant to();
}
