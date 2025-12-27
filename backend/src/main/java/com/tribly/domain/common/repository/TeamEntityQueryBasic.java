package com.tribly.domain.common.repository;

import com.tribly.enums.Status;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record TeamEntityQueryBasic(
    @Nullable Long userId,
    @Nullable Set<String> teamSlugs,
    @Nullable String slug,
    @Nullable Status status,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
