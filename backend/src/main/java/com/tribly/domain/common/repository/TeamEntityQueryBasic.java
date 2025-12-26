package com.tribly.domain.common.repository;

import com.tribly.enums.Status;
import java.time.Instant;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record TeamEntityQueryBasic(
    @Nullable String slug,
    Set<Long> memberTeamIds,
    Set<Long> organizerTeamIds,
    @Nullable Status status,
    @Nullable String search,
    // Teams where user is a member
    // Teams where user is organizer/admin
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size)
    implements TeamEntityQueryInterface {}
