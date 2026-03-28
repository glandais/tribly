package fr.pedalons.service.team.response;

import fr.pedalons.domain.team.Team;
import fr.pedalons.enums.TeamRole;
import org.jspecify.annotations.Nullable;

public record TeamAndRole(Team team, @Nullable TeamRole teamRole, Long memberCount) {}
