package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.PageInterface;
import com.tribly.enums.Visibility;
import org.jspecify.annotations.Nullable;

public record RouteQuery(
    long teamId, int page, int size, @Nullable Long routeId, @Nullable Visibility visibility)
    implements PageInterface {}
