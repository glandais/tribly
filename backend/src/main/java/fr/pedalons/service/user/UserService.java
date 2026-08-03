package fr.pedalons.service.user;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.social.response.SocialIdentityDto;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.request.UserPreferencesRequest;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.enums.ThemePreference;
import fr.pedalons.enums.UnitSystem;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.repository.social.UserSocialIdentityRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
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

  @Logged
  @Transactional
  public void deleteUser() {
    User user = pedalonsContext.getUser();
    user.setDeleted(true);
    userRepository.persist(user);
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
