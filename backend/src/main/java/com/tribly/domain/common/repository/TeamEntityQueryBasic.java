package com.tribly.domain.common.repository;

import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamEntityQueryBasic(
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
