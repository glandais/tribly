package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.PageInterface;
import com.tribly.enums.RideStatus;
import com.tribly.enums.Visibility;
import java.time.LocalDateTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record RideQuery(
    long teamId,
    int page,
    int size,
    @Nullable String slug,
    @Nullable Visibility visibility,
    @Nullable LocalDateTime from,
    @Nullable LocalDateTime to,
    List<RideStatus> statuses)
    implements PageInterface {}
