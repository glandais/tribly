package com.tribly.service.common;

import com.tribly.enums.Status;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface Query {
  @Nullable Long userId();

  @Nullable Set<String> teamSlugs();

  @Nullable String search();

  @Nullable Status status();

  @Nullable String slug();

  @Nullable Instant from();

  @Nullable Instant to();

  int page();

  int size();
}
