package com.tribly.domain.common.repository;

import com.tribly.dto.publications.response.PublicationType;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PublicationQuery(
    @Nullable List<PublicationType> types,
    @Nullable Long userId,
    @Nullable Set<String> teamSlugs,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
