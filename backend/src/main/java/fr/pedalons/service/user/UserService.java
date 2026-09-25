package fr.pedalons.service.user;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.social.response.SocialIdentityDto;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.request.UserPreferencesRequest;
import fr.pedalons.dto.users.response.AccountDeletionImpactDto;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.enums.ThemePreference;
import fr.pedalons.enums.UnitSystem;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.repository.social.UserSocialIdentityRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject GpsServiceConnectionRepository gpsConnectionRepository;

  @Inject UserSocialIdentityRepository socialIdentityRepository;

  @Inject AccountErasureService accountErasureService;

  @Inject UserTeamRepository userTeamRepository;

  @Inject TeamService teamService;

  @Logged
  public UserDto getUserDto() {
    User user = pedalonsContext.getUser();
    List<GpsServiceConnectionDto> connections =
        gpsConnectionRepository.findByUser(user.getId()).stream()
            .map(GpsServiceConnectionDto::from)
            .toList();
    List<SocialIdentityDto> socialIdentities =
        socialIdentityRepository.findByUser(user.getId()).stream()
            .map(SocialIdentityDto::from)
            .toList();
    return UserDto.from(user, connections, socialIdentities);
  }

  @Logged
  @Transactional
  public UserDto updateUser(UpdateUserRequest request) {
    User user = pedalonsContext.getUser();

    String displayName = request.displayName();
    if (displayName != null) {
      user.setDisplayName(displayName);
    }

    UnitSystem unitSystem = request.unitSystem();
    if (unitSystem != null) {
      user.setUnitSystem(unitSystem);
    }

    userRepository.persist(user);
    return UserDto.from(user);
  }

  /**
   * Writes the display preferences the profile screen owns, leaving the rest of the profile alone.
   *
   * <p>A null field means "unchanged", not "clear": see {@link UserPreferencesRequest}.
   */
  @Logged
  @Transactional
  public UserDto updatePreferences(UserPreferencesRequest request) {
    User user = pedalonsContext.getUser();

    UnitSystem unitSystem = request.unitSystem();
    if (unitSystem != null) {
      user.setUnitSystem(unitSystem);
    }
    ThemePreference theme = request.theme();
    if (theme != null) {
      user.setTheme(theme);
    }
    String language = request.language();
    if (language != null) {
      user.setLanguage(language);
    }
    String timezone = request.timezone();
    if (timezone != null) {
      try {
        ZoneId.of(timezone);
      } catch (DateTimeException e) {
        throw new BadRequestException(ErrorCode.INVALID_TIMEZONE, e);
      }
      user.setTimezone(timezone);
    }
    Boolean contactable = request.contactableByMembers();
    if (contactable != null) {
      user.setContactableByMembers(contactable);
    }

    userRepository.persist(user);
    return UserDto.from(user);
  }

  /** What {@link #deleteUser} would do to the current user's teams, for the confirmation. */
  @Logged
  @Transactional
  public AccountDeletionImpactDto getDeletionImpact() {
    User user = pedalonsContext.getUser();
    Long domainId = pedalonsContext.getDomainId();
    List<TeamPublicationDto> blockingTeams =
        userTeamRepository.findTeamsLeftWithoutAdmin(user.getId(), domainId).stream()
            .map(TeamPublicationDto::from)
            .toList();
    List<TeamPublicationDto> deletedTeams =
        userTeamRepository.findTeamsAdministeredAlone(user.getId(), domainId).stream()
            .map(TeamPublicationDto::from)
            .toList();
    return new AccountDeletionImpactDto(!blockingTeams.isEmpty(), blockingTeams, deletedTeams);
  }

  @Logged
  @Transactional
  public void deleteUser() {
    User user = pedalonsContext.getUser();
    Long domainId = pedalonsContext.getDomainId();
    // The erasure drops every membership and promotes nobody: the last admin of a team others
    // still belong to would leave them a team no one can run. They name another admin, or delete
    // the team, first — the same rule as leaving the team (LAST_ADMIN).
    if (!userTeamRepository.findTeamsLeftWithoutAdmin(user.getId(), domainId).isEmpty()) {
      throw new BusinessException(ErrorCode.SOLE_TEAM_ADMIN);
    }
    // A team they administer alone would be left with no member at all: it goes with the account,
    // through the same deletion as DELETE /teams/{slug}. The confirmation named these teams
    // (GET /users/me/deletion-impact).
    for (Team team : userTeamRepository.findTeamsAdministeredAlone(user.getId(), domainId)) {
      teamService.delete(team);
    }
    // Erased now, not flagged for later: nothing ever came back for a flagged account, and the
    // policy, the app and the store listings all promise the data is gone.
    accountErasureService.erase(user);
    // The request context memoizes the active user; it is no longer active.
    pedalonsContext.invalidateUser();
  }

  /**
   * Lookup user by email and domain without creating/updating. Used by PedalonsQueryContext.
   *
   * @param domainId the domain ID
   * @param email the user's email
   * @return Optional containing the user if found, empty otherwise
   */
  @Transactional
  public Optional<User> lookupUserByEmailAndDomain(Long domainId, String email) {
    return userRepository.findByEmailAndDomain(domainId, email);
  }
}
