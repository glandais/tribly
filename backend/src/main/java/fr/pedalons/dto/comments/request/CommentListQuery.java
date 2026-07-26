package fr.pedalons.dto.comments.request;

import fr.pedalons.enums.SortDirection;
import org.jspecify.annotations.Nullable;

/**
 * What {@code CommentService.listComments} was actually asked for, with every bound already applied.
 *
 * <p>Produced by {@link CommentListParams}, which is where the raw query parameters are clamped —
 * the service never sees an unbounded {@code size} or a negative {@code page}.
 *
 * @param paginated {@code false} when the caller passed no pagination parameter at all. The endpoint
 *     answered with the whole comment tree long before it took parameters, and the web client still
 *     relies on that; asking for nothing must keep returning exactly that.
 * @param parentId when set, {@code items} are the replies of that comment instead of the top-level
 *     comments — the "load this thread" mode
 */
public record CommentListQuery(
    boolean paginated, int page, int size, SortDirection sort, @Nullable Long parentId) {

  /** The legacy call: whole tree, oldest first. */
  public static final CommentListQuery ALL =
      new CommentListQuery(false, 0, 0, SortDirection.ASC, null);
}
