package fr.pedalons.service.moderation;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.users.response.BlockedUsersResponse;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.enums.EntityType;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.moderation.UserBlockRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;

/**
 * The current member's blocks: domain-wide, one-way and silent.
 *
 * <p>The blocker no longer sees the blocked member's comments, posts and ads, nor the comment
 * notifications they cause — see {@code TeamEntityRepository}, {@code CommentService} and {@code
 * NotificationRecipientResolver}. Rides, trips, routes and participant lists do not change: they
 * are the team's organisation. The blocked member sees nothing change, and no endpoint tells them.
 */
@ApplicationScoped
public class UserBlockService {

  @Inject PedalonsQueryContext pedalonsContext;
  @Inject UserBlockRepository userBlockRepository;
  @Inject UserRepository userRepository;

  @Logged
  @Transactional
  public BlockedUsersResponse listBlockedUsers() {
    return new BlockedUsersResponse(
        userBlockRepository.findByBlocker(pedalonsContext.getUserId()).stream()
            .map(block -> PublicUserDto.from(block.getBlocked()))
            .toList());
  }

  /** Idempotent: blocking someone already blocked changes nothing. */
  @Logged
  @Transactional
  public void blockUser(String userId) {
    User blocker = pedalonsContext.getUser();
    User blocked = findBlockable(blocker, userId);
    // Idempotent, a double tap included: see UserBlockRepository#insertIfAbsent.
    userBlockRepository.insertIfAbsent(blocker.getId(), blocked.getId(), Instant.now());
  }

  /** Idempotent: unblocking someone not blocked changes nothing. */
  @Logged
  @Transactional
  public void unblockUser(String userId) {
    User blocker = pedalonsContext.getUser();
    User blocked = findBlockable(blocker, userId);
    userBlockRepository
        .findByBlockerAndBlocked(blocker.getId(), blocked.getId())
        .ifPresent(userBlockRepository::delete);
  }

  /** A live account of the caller's domain, other than the caller. */
  private User findBlockable(User blocker, String userId) {
    Long id = TsidUtils.toLong(userId);
    if (id.equals(blocker.getId())) {
      throw new BadRequestException(ErrorCode.BLOCK_SELF);
    }
    return userRepository
        .findActiveByIdAndDomain(pedalonsContext.getDomainId(), id)
        .orElseThrow(() -> new NotFoundException(EntityType.USER, id));
  }
}
