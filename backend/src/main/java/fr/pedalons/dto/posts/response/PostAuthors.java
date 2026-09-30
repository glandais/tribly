package fr.pedalons.dto.posts.response;

import fr.pedalons.dto.users.response.PublicUserDto;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Who wrote each post of a page, <b>for the caller asking</b> — built by {@code
 * fr.pedalons.service.post.PostAuthorLookup}, always in bulk. A post signed by the team is absent
 * unless the caller administers that team or wrote it: {@link #forPost} then answers {@code null}
 * and the field disappears from the JSON. docs/LEDGER_*.md API-6.
 *
 * @param byPostId post id → its author, only for the posts whose author the caller may know
 */
public record PostAuthors(Map<Long, PublicUserDto> byPostId) {

  /** No author known — what a caller outside every lookup gets. */
  public static final PostAuthors NONE = new PostAuthors(Map.of());

  public @Nullable PublicUserDto forPost(@Nullable Long postId) {
    return postId == null ? null : byPostId.get(postId);
  }
}
