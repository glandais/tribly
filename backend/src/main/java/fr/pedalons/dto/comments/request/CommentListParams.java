package fr.pedalons.dto.comments.request;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.SortDirection;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jspecify.annotations.Nullable;

/**
 * The listing parameters the four comment endpoints share, as a single {@code @BeanParam}.
 *
 * <p>Rides, posts, trips and routes each expose {@code GET …/comments} with the same contract;
 * declaring the parameters once is what keeps them from drifting apart.
 *
 * <p><b>Backwards compatibility.</b> Every parameter is optional and, crucially, {@code page} and
 * {@code size} have <em>no</em> {@code @DefaultValue}: a request that passes none of them is
 * indistinguishable from the requests the web client sends today, and {@link #toQuery()} answers
 * {@link CommentListQuery#ALL} — the whole tree, exactly as before.
 */
public class CommentListParams {

  /** A page nobody can scroll past; the ceiling exists so no request can ask for the whole table. */
  public static final int MAX_SIZE = 100;

  /** What a caller that paginates without saying how much gets. */
  public static final int DEFAULT_SIZE = 20;

  @Parameter(
      description =
          "Page number (0-indexed). Omit both page and size to get the whole comment tree, as"
              + " before this endpoint took parameters.")
  @QueryParam("page")
  public @Nullable Integer page;

  @Parameter(description = "Page size, capped at " + MAX_SIZE + " (default " + DEFAULT_SIZE + ")")
  @QueryParam("size")
  public @Nullable Integer size;

  @Parameter(description = "Sort by creation date: ASC (oldest first, default) or DESC")
  @QueryParam("sort")
  public @Nullable SortDirection sort;

  @Parameter(
      description =
          "Load only the replies of this comment (TSID) instead of the top-level comments. Use it"
              + " to expand one thread without re-reading the others.")
  @QueryParam("parentId")
  public @Nullable String parentId;

  /** Folds the raw parameters into the bounded query the service takes. */
  public CommentListQuery toQuery() {
    Long parent;
    try {
      parent = TsidUtils.toLongNullable(parentId);
    } catch (IllegalArgumentException e) {
      // A malformed id is a client mistake, not a server failure.
      throw new BadRequestException(ErrorCode.BAD_REQUEST, e);
    }
    if (page == null && size == null && parent == null) {
      return CommentListQuery.ALL;
    }
    int effectivePage = page == null ? 0 : Math.max(0, page);
    int effectiveSize = size == null ? DEFAULT_SIZE : Math.clamp(size, 1, MAX_SIZE);
    SortDirection effectiveSort = sort == null ? SortDirection.ASC : sort;
    return new CommentListQuery(true, effectivePage, effectiveSize, effectiveSort, parent);
  }
}
