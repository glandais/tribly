package com.tribly.domain.common.repository;

import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import java.time.LocalDateTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record TeamPublicationQuery(
    long teamId,
    int page,
    int size,
    @Nullable String slug,
    @Nullable Visibility visibility,
    @Nullable LocalDateTime from,
    @Nullable LocalDateTime to,
    List<Status> statuses)
    implements PageInterface {}
