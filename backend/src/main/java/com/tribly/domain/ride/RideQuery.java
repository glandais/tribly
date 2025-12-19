package com.tribly.domain.ride;

import com.tribly.domain.common.PageInterface;
import com.tribly.domain.common.Visibility;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record RideQuery(
    long teamId,
    int page,
    int size,
    @Nullable String slug,
    @Nullable LocalDate from,
    @Nullable LocalDate to,
    @Nullable Visibility visibility,
    List<RideStatus> statuses)
    implements PageInterface {}
