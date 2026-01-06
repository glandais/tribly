package com.tribly.domain.team.repository;

import com.tribly.domain.common.repository.PageInterface;
import com.tribly.service.team.request.MinRole;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamQuery(
    int page,
    int size,
    @Nullable Long id,
    @Nullable Long userId,
    @Nullable MinRole minRole,
    @Nullable String search)
    implements PageInterface {}
