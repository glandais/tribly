package fr.pedalons.service.comment;

import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.trip.TripStage;
import org.hibernate.Hibernate;

/**
 * What a comment thread belongs to, as the rest of the app knows it.
 *
 * <p>A stage carries its own thread (docs/LEDGER_*.md API-11), but a stage is not a publication:
 * it has no author of its own worth notifying, no page a notification can name with a single slug,
 * and no report target type. Notifications, reports and the moderation queue therefore treat a
 * stage's thread as its trip's — the trip's author is told, the trip's visibility decides who may
 * report, the moderator opens the trip. A deleted stage is returned as is, so that callers which
 * ignore deleted content keep ignoring its orphaned thread.
 */
public final class CommentThreads {

  private CommentThreads() {}

  /** Unproxied: a lazy {@code TeamEntity} proxy matches none of the subtypes callers switch on. */
  public static TeamEntity publicationOf(Comment comment) {
    TeamEntity on = Hibernate.unproxy(comment.getTeamEntity(), TeamEntity.class);
    if (on instanceof TripStage stage && !stage.isDeleted()) {
      return Hibernate.unproxy(stage.getTrip(), TeamEntity.class);
    }
    return on;
  }
}
