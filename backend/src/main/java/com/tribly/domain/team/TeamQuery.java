package com.tribly.domain.team;

import com.tribly.domain.common.PageInterface;
import org.jspecify.annotations.Nullable;

public record TeamQuery(
    int page,
    int size,
    @Nullable String slug,
    @Nullable Long userId,
    @Nullable Boolean member,
    @Nullable String search)
    implements PageInterface {}
