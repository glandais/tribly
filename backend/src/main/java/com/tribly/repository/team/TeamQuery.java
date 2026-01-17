package com.tribly.repository.team;

import com.tribly.repository.common.PageInterface;
import com.tribly.service.team.request.MinRole;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record TeamQuery(
    int page,
    int size,
    Long domainId,
    @Nullable Long id,
    @Nullable Long userId,
    @Nullable MinRole minRole,
    @Nullable String search)
    implements PageInterface {}
