package com.tribly.domain.team.repository;

import com.tribly.domain.common.repository.PageInterface;
import org.jspecify.annotations.Nullable;

public record TeamQuery(
    int page,
    int size,
    @Nullable String slug,
    @Nullable Long userId,
    @Nullable Boolean member,
    @Nullable String search)
    implements PageInterface {}
