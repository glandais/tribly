package fr.pedalons.service.post;

import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.posts.response.PostAuthors;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Resolves {@code PostDto.createdBy} for a set of posts, in bulk and under the signature rule
 * (docs/LEDGER_*.md API-6).
 *
 * <ul>
 *   <li><b>A post signed by its author</b> names them to whoever may read the post.
 *   <li><b>A post signed by the team</b> names them only to the team's administrators — a platform
 *       administrator included — and to the author themself.
 * </ul>
 *
 * <p>At most two queries whatever the page size: the teams the caller administers among those that
 * signed a post of the page, then the authors to name. {@code getCreatedBy().getId()} and {@code
 * getTeam().getId()} read foreign keys already in hand, never the lazy associations.
 */
@ApplicationScoped
public class PostAuthorLookup {

  @Inject UserRepository userRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  /** The detail path: same rules, same queries. */
  public PostAuthors forPost(Post post) {
    return forPosts(List.of(post));
  }

  public PostAuthors forPosts(Collection<Post> posts) {
    if (posts.isEmpty()) {
      return PostAuthors.NONE;
    }
    Set<Long> adminTeamIds = adminTeamIds(posts);
    @Nullable Long userId = pedalonsContext.getUserIdNullable();
    boolean platformAdmin = pedalonsContext.isPlatformAdmin();

    Map<Long, Long> authorIdByPostId = new HashMap<>();
    for (Post post : posts) {
      Long authorId = post.getCreatedBy().getId();
      boolean named =
          !post.isSignedAsTeam()
              || platformAdmin
              || authorId.equals(userId)
              || adminTeamIds.contains(post.getTeam().getId());
      if (named) {
        authorIdByPostId.put(post.getId(), authorId);
      }
    }
    if (authorIdByPostId.isEmpty()) {
      return PostAuthors.NONE;
    }

    Map<Long, PublicUserDto> authors = new HashMap<>();
    for (User user : userRepository.list("id in ?1", new HashSet<>(authorIdByPostId.values()))) {
      authors.put(user.getId(), PublicUserDto.from(user));
    }
    Map<Long, PublicUserDto> byPostId = new HashMap<>();
    authorIdByPostId.forEach(
        (postId, authorId) -> {
          PublicUserDto author = authors.get(authorId);
          if (author != null) {
            byPostId.put(postId, author);
          }
        });
    return new PostAuthors(byPostId);
  }

  /** The teams, among those that signed a post of the page, the caller administers. */
  private Set<Long> adminTeamIds(Collection<Post> posts) {
    @Nullable Long userId = pedalonsContext.getUserIdNullable();
    if (userId == null || pedalonsContext.isPlatformAdmin()) {
      return Set.of();
    }
    Set<Long> teamIds = new HashSet<>();
    for (Post post : posts) {
      if (post.isSignedAsTeam()) {
        teamIds.add(post.getTeam().getId());
      }
    }
    return userTeamRepository.findAdminTeamIds(userId, pedalonsContext.getDomainId(), teamIds);
  }
}
