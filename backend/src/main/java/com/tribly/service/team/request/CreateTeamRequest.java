package com.tribly.service.team.request;

import com.tribly.enums.Visibility;
import org.jspecify.annotations.Nullable;

public record CreateTeamRequest(
    String name,
    @Nullable String description,
    Visibility visibility,
    @Nullable Integer maxMembers) {}
