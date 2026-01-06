package com.tribly.domain.ad.repository;

import com.tribly.domain.common.repository.TeamEntityQueryInterface;
import com.tribly.enums.AdType;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record AdQuery(
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    @Nullable AdType adType,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
