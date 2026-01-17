package com.tribly.repository.common;

import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface TeamEntityQueryInterface extends PageInterface {
  Long domainId();

  @Nullable Long id();

  @Nullable Long userId();

  @Nullable Set<Long> teamIds();

  @Nullable String slug();

  @Nullable String search();

  @Nullable Instant from();

  @Nullable Instant to();
}
