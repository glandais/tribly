package com.tribly.service.common;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.enums.Status;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record BasicQuery(
    @Nullable String slug,
    @Nullable Set<String> teamSlugs,
    @Nullable Long userId,
    @Nullable Status status,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements Query {

  public TeamEntityQueryBasic getTeamEntityQueryBasic(
      Set<Long> memberTeamIds, Set<Long> organizerTeamIds) {
    return new TeamEntityQueryBasic(
        slug(), memberTeamIds, organizerTeamIds, status(), search(), from(), to(), page(), size());
  }
}
