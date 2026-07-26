package fr.pedalons.dto.comments.response;

import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * How many comments each entity of a page carries, <b>for the caller asking</b>.
 *
 * <p>Comments are a members-only surface: {@code CommentAccessChecker} only grants {@code LIST} to a
 * user holding a role in the owning team. A comment count is therefore not a public property of an
 * entity — publishing "42 comments" to a visitor who cannot open a single one of them would leak
 * activity about a private team. An entity the caller may not read the comments of is simply
 * <b>absent</b> from the map, and {@link #forEntity} answers {@code null}: Jackson is configured
 * {@code NON_NULL}, so the field disappears from the JSON rather than reading as a real zero.
 *
 * <p>Absent and zero are deliberately distinguishable: an entity the caller may read with no comment
 * yet maps to {@code 0}.
 *
 * <p>Built by {@code fr.pedalons.service.comment.CommentCountLookup}, always in bulk for a whole
 * page — two queries, whatever the page size.
 *
 * @param countByTeamEntityId entity id → its comment count, replies included; only contains entities
 *     whose comments the caller may read
 */
public record CommentCounts(Map<Long, Integer> countByTeamEntityId) {

  /** Nothing readable — what an anonymous caller, or a non-member, sees. */
  public static final CommentCounts NONE = new CommentCounts(Map.of());

  /** This entity's comment count, or {@code null} if the caller may not read its comments. */
  public @Nullable Integer forEntity(@Nullable Long teamEntityId) {
    return teamEntityId == null ? null : countByTeamEntityId.get(teamEntityId);
  }
}
