package com.tribly.service.team;

import com.tribly.domain.common.Visibility;
import org.jspecify.annotations.Nullable;

public record CreateTeamRequest(
    String name,
    @Nullable String description,
    Visibility visibility,
    @Nullable Integer maxMembers) {}
