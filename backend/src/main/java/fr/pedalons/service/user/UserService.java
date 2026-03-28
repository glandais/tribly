package fr.pedalons.service.user;

import fr.pedalons.domain.user.User;
import fr.pedalons.dto.gps.response.GpsServiceConnectionDto;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.enums.UnitSystem;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject GpsServiceConnectionRepository gpsConnectionRepository;

  @Logged
  public UserDto getUserDto() {
    User user = pedalonsContext.getUser();
    List<GpsServiceConnectionDto> connections =
        gpsConnectionRepository.findByUser(user.getId()).stream()
            .map(GpsServiceConnectionDto::from)
            .toList();
    return UserDto.from(user, connections);
  }

  @Public
  public List<PublicUserDto> searchByDisplayName(String query, int limit) {
    Long domainId = pedalonsContext.getDomainId();
    List<User> users = userRepository.searchByDisplayNameAndDomain(domainId, query, limit);
    return users.stream().map(PublicUserDto::from).toList();
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

  @Logged
  @Transactional
  public void deleteUser() {
    User user = pedalonsContext.getUser();
    user.setDeleted(true);
    userRepository.persist(user);
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
