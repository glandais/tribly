package com.tribly.service.security;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.TeamRole;
import org.jspecify.annotations.Nullable;

public record Context(@Nullable Team team, @Nullable User user, @Nullable TeamRole teamRole) {}
