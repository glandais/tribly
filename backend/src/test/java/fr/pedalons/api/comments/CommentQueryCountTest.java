package fr.pedalons.api.comments;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.post.Post;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the paginated comment list.
 *
 * <p>See {@link AbstractQueryCountTest}. The page carries every masking input — a blocked author,
 * reported comments, a comment hidden by reports — so the in-memory masking of {@code
 * CommentService} is measured with the rest: it must cost a fixed number of queries per page, never
 * one per comment.
 */
@QuarkusTest
class CommentQueryCountTest extends AbstractQueryCountTest {

  private Post post;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    post =
        dataService.createPost(
            team1,
            user2,
            "Fil de discussion",
            Instant.now().minus(1, ChronoUnit.DAYS),
            Visibility.PUBLIC);
  }

  private void seedComments(int count) {
    // user1 reads: they blocked user3 and reported some of user2's comments.
    dataService.createBlock(user1, user3);
    for (int i = 0; i < count; i++) {
      Comment root = dataService.createComment(i % 2 == 0 ? user2 : user3, post, "Racine " + i);
      if (i % 2 == 0) {
        // Every other thread answered: a reply per root would sit right at the hydration budget.
        dataService.createReply(user2, post, root, "Réponse " + i);
      }
      if (i % 5 == 0) {
        dataService.createReport(
            team1,
            user1,
            ReportTargetType.COMMENT,
            root.getId(),
            root.getCreatedBy(),
            ReportReason.SPAM);
      }
      if (i % 7 == 0) {
        dataService.hideCommentForModeration(root);
      }
    }
  }

  private String commentsPath() {
    return "/api/teams/" + team1Slug + "/posts/" + post.getSlug() + "/comments?page=0";
  }

  @Test
  void listComments_paginated_costDoesNotScaleWithRowCount() {
    seedComments(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/posts/{slug}/comments?page=", asUser1(), commentsPath());
  }

  /** A member, not a moderator: the hidden comments cost them nothing more either. */
  @Test
  void listComments_paginated_asMember_costDoesNotScaleWithRowCount() {
    seedComments(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/teams/{teamSlug}/posts/{slug}/comments?page= (member)",
        () -> io.restassured.RestAssured.given().auth().oauth2(getAccessToken(USER3)),
        commentsPath());
  }
}
