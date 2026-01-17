package com.tribly.service.user;

import com.tribly.domain.user.User;
import com.tribly.dto.users.request.UpdateUserRequest;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.users.response.UserDto;
import com.tribly.enums.UnitSystem;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import com.tribly.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  @Inject TriblyQueryContext triblyContext;

  @Logged
  public UserDto getUserDto() {
    return UserDto.from(triblyContext.getUser());
  }

  @Public
  public List<PublicUserDto> searchByDisplayName(String query, int limit) {
    Long domainId = triblyContext.getDomainId();
    List<User> users = userRepository.searchByDisplayNameAndDomain(domainId, query, limit);
    return users.stream().map(PublicUserDto::from).toList();
  }

  @Logged
  @Transactional
  public UserDto updateUser(UpdateUserRequest request) {
    User user = triblyContext.getUser();

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
    User user = triblyContext.getUser();
    user.setDeleted(true);
    userRepository.persist(user);
  }

  /**
   * Lookup user by email and domain without creating/updating. Used by TriblyQueryContext.
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
