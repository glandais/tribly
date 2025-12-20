package com.tribly.service.team.request;

import com.tribly.enums.Visibility;
import org.jspecify.annotations.Nullable;

public record UpdateTeamRequest(
    @Nullable String name,
    @Nullable String description,
    @Nullable Visibility visibility,
    @Nullable String logoUrl,
    @Nullable String coverImageUrl,
    @Nullable Integer maxMembers) {}
