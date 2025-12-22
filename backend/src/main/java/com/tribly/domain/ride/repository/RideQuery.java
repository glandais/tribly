package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.PageInterface;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record RideQuery(
    long teamId,
    int page,
    int size,
    @Nullable String slug,
    @Nullable Visibility visibility,
    @Nullable Instant from,
    @Nullable Instant to,
    List<Status> statuses)
    implements PageInterface {}
