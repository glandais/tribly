package com.tribly.domain.route;

import com.tribly.domain.common.PageInterface;
import com.tribly.domain.common.Visibility;
import org.jspecify.annotations.Nullable;

public record RouteQuery(
    long teamId, int page, int size, @Nullable Long routeId, @Nullable Visibility visibility)
    implements PageInterface {}
