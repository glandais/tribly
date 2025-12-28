package com.tribly.domain.common.repository;

import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface TeamEntityQueryInterface extends PageInterface {
  @Nullable Long id();

  @Nullable Long userId();

  @Nullable Set<String> teamSlugs();

  @Nullable String slug();

  @Nullable String search();

  @Nullable Instant from();

  @Nullable Instant to();
}
