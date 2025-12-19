package com.tribly.service.user;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  public User getActiveById(Long userId) {
    return userRepository
        .findActiveById(userId)
        .orElseThrow(() -> BusinessException.notFound("User", userId));
  }

  public List<User> searchByDisplayName(String query, int limit) {
    return userRepository.searchByDisplayName(query, limit);
  }

  @Transactional
  public User updateUser(
      Long userId,
      @Nullable String displayName,
      @Nullable String locale,
      @Nullable String timezone) {
    User user = getActiveById(userId);

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
    return user;
  }

  @Transactional
  public void deleteUser(Long userId) {
    User user = getActiveById(userId);
    user.softDelete();
    userRepository.persist(user);
  }
}
