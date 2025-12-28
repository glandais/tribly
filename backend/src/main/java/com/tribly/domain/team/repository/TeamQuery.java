package com.tribly.domain.team.repository;

import com.tribly.domain.common.repository.PageInterface;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamQuery(
    int page,
    int size,
    @Nullable String slug,
    @Nullable Long userId,
    @Nullable Boolean member,
    @Nullable String search)
    implements PageInterface {}
