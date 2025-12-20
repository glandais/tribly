package com.tribly.service.user;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.users.response.UserDto;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  public PublicUserDto getPublicUserDto(Long userId) {
    return PublicUserDto.from(getUserEntity(userId));
  }

  public UserDto getUserDto(Long userId) {
    return UserDto.from(getUserEntity(userId));
  }

  private User getUserEntity(Long userId) {
    return userRepository
        .findActiveById(userId)
        .orElseThrow(() -> BusinessException.notFound("User", userId));
  }

  public List<PublicUserDto> searchByDisplayName(String query, int limit) {
    List<User> users = userRepository.searchByDisplayName(query, limit);
    return users.stream().map(PublicUserDto::from).toList();
  }

  @Transactional
  public UserDto updateUser(
      Long userId,
      @Nullable String displayName,
      @Nullable String locale,
      @Nullable String timezone) {
    User user = getUserEntity(userId);

    if (displayName != null) {
      user.setDisplayName(displayName);
    }
    if (locale != null) {
      user.setLocale(locale);
    }
    if (timezone != null) {
      user.setTimezone(timezone);
    }

    userRepository.persist(user);
    return UserDto.from(user);
  }

  @Transactional
  public void deleteUser(Long userId) {
    User user = getUserEntity(userId);
    user.setDeleted(true);
    userRepository.persist(user);
  }
}
