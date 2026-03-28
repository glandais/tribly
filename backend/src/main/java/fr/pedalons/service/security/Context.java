package fr.pedalons.service.security;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TeamRole;
import org.jspecify.annotations.Nullable;

public record Context(@Nullable Team team, @Nullable User user, @Nullable TeamRole teamRole) {}
