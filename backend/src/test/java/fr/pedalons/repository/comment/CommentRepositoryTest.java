package fr.pedalons.repository.comment;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CommentRepositoryTest extends AbstractBaseTest {

  @Inject CommentRepository commentRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  private Team team;
  private User user;
  private Post post;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    post = dataService.createPost(team, user, "Test Post", Instant.now());
  }

  @Nested
  @DisplayName("findByTeamEntityId")
  class FindByTeamEntityId {

    @Test
    void shouldReturnCommentsForTeamEntity() {
      dataService.createComment(user, post, "Comment 1");
      dataService.createComment(user, post, "Comment 2");

      List<Comment> result = commentRepository.findByTeamEntityId(post.getId());

      assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoComments() {
      List<Comment> result = commentRepository.findByTeamEntityId(post.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldExcludeDeletedComments() {
      dataService.createComment(user, post, "Visible Comment");
      Comment deletedComment = dataService.createComment(user, post, "Deleted Comment");
      dataService.deleteComment(deletedComment);

      List<Comment> result = commentRepository.findByTeamEntityId(post.getId());

      assertEquals(1, result.size());
      assertEquals("Visible Comment", result.getFirst().getContent());
    }

    @Test
    void shouldNotReturnCommentsFromOtherEntities() {
      dataService.createComment(user, post, "Comment on post");
      Post otherPost = dataService.createPost(team, user, "Other Post", Instant.now());
      dataService.createComment(user, otherPost, "Comment on other post");

      List<Comment> result = commentRepository.findByTeamEntityId(post.getId());

      assertEquals(1, result.size());
      assertEquals("Comment on post", result.getFirst().getContent());
    }

    @Test
    void shouldReturnCommentsOrderedByCreatedAt() {
      Comment comment1 = dataService.createComment(user, post, "First");
      Comment comment2 = dataService.createComment(user, post, "Second");
      Comment comment3 = dataService.createComment(user, post, "Third");

      List<Comment> result = commentRepository.findByTeamEntityId(post.getId());

      assertEquals(3, result.size());
      assertEquals(comment1.getId(), result.get(0).getId());
      assertEquals(comment2.getId(), result.get(1).getId());
      assertEquals(comment3.getId(), result.get(2).getId());
    }
  }

  @Nested
  @DisplayName("findByTeamIdAndId")
  class FindByTeamIdAndId {

    @Test
    void shouldReturnCommentWhenFound() {
      Comment comment = dataService.createComment(user, post, "Test Comment");

      Optional<Comment> result = commentRepository.findByTeamIdAndId(team.getId(), comment.getId());

      assertTrue(result.isPresent());
      assertEquals("Test Comment", result.get().getContent());
    }

    @Test
    void shouldReturnEmptyWhenCommentNotFound() {
      Optional<Comment> result = commentRepository.findByTeamIdAndId(team.getId(), 999999L);

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenCommentIsDeleted() {
      Comment comment = dataService.createComment(user, post, "Deleted Comment");
      dataService.deleteComment(comment);

      Optional<Comment> result = commentRepository.findByTeamIdAndId(team.getId(), comment.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenTeamIsDeleted() {
      Comment comment = dataService.createComment(user, post, "Comment on deleted team");
      dataService.deleteTeam(team);

      Optional<Comment> result = commentRepository.findByTeamIdAndId(team.getId(), comment.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenCommentBelongsToDifferentTeam() {
      Comment comment = dataService.createComment(user, post, "Comment");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      Optional<Comment> result =
          commentRepository.findByTeamIdAndId(otherTeam.getId(), comment.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findReplies")
  class FindReplies {

    @Test
    void shouldReturnRepliesForParentComment() {
      Comment parent = dataService.createComment(user, post, "Parent");
      dataService.createReply(user, post, parent, "Reply 1");
      dataService.createReply(user, post, parent, "Reply 2");

      List<Comment> result = commentRepository.findReplies(parent.getId());

      assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoReplies() {
      Comment parent = dataService.createComment(user, post, "Parent without replies");

      List<Comment> result = commentRepository.findReplies(parent.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldExcludeDeletedReplies() {
      Comment parent = dataService.createComment(user, post, "Parent");
      dataService.createReply(user, post, parent, "Visible Reply");
      Comment deletedReply = dataService.createReply(user, post, parent, "Deleted Reply");
      dataService.deleteComment(deletedReply);

      List<Comment> result = commentRepository.findReplies(parent.getId());

      assertEquals(1, result.size());
      assertEquals("Visible Reply", result.getFirst().getContent());
    }

    @Test
    void shouldNotReturnRepliesFromOtherParents() {
      Comment parent1 = dataService.createComment(user, post, "Parent 1");
      Comment parent2 = dataService.createComment(user, post, "Parent 2");
      dataService.createReply(user, post, parent1, "Reply to Parent 1");
      dataService.createReply(user, post, parent2, "Reply to Parent 2");

      List<Comment> result = commentRepository.findReplies(parent1.getId());

      assertEquals(1, result.size());
      assertEquals("Reply to Parent 1", result.getFirst().getContent());
    }
  }

  @Nested
  @DisplayName("countByTeamEntityId")
  class CountByTeamEntityId {

    @Test
    void shouldReturnCorrectCount() {
      dataService.createComment(user, post, "Comment 1");
      dataService.createComment(user, post, "Comment 2");
      dataService.createComment(user, post, "Comment 3");

      long count = commentRepository.countByTeamEntityId(post.getId());

      assertEquals(3, count);
    }

    @Test
    void shouldReturnZeroWhenNoComments() {
      long count = commentRepository.countByTeamEntityId(post.getId());

      assertEquals(0, count);
    }

    @Test
    void shouldExcludeDeletedComments() {
      dataService.createComment(user, post, "Visible Comment");
      Comment deletedComment = dataService.createComment(user, post, "Deleted Comment");
      dataService.deleteComment(deletedComment);

      long count = commentRepository.countByTeamEntityId(post.getId());

      assertEquals(1, count);
    }

    @Test
    void shouldNotCountCommentsFromOtherEntities() {
      dataService.createComment(user, post, "Comment on post");
      Post otherPost = dataService.createPost(team, user, "Other Post", Instant.now());
      dataService.createComment(user, otherPost, "Comment on other post");

      long count = commentRepository.countByTeamEntityId(post.getId());

      assertEquals(1, count);
    }

    @Test
    void shouldCountBothCommentsAndReplies() {
      Comment parent = dataService.createComment(user, post, "Parent");
      dataService.createReply(user, post, parent, "Reply 1");
      dataService.createReply(user, post, parent, "Reply 2");

      long count = commentRepository.countByTeamEntityId(post.getId());

      assertEquals(3, count);
    }
  }

  @Nested
  @DisplayName("countByTeamEntityIds")
  class CountByTeamEntityIds {

    @Test
    void shouldCountEveryEntityOfThePageInOneQuery() {
      Post otherPost = dataService.createPost(team, user, "Other Post", Instant.now());
      Comment parent = dataService.createComment(user, post, "Parent");
      dataService.createReply(user, post, parent, "Reply");
      dataService.createComment(user, otherPost, "Alone");

      Map<Long, Integer> counts =
          commentRepository.countByTeamEntityIds(
              dataService.getOrCreateDefaultDomain().getId(),
              List.of(post.getId(), otherPost.getId()));

      assertEquals(2, counts.get(post.getId()));
      assertEquals(1, counts.get(otherPost.getId()));
    }

    @Test
    void shouldLeaveEntitiesWithoutCommentsOutOfTheMap() {
      Map<Long, Integer> counts =
          commentRepository.countByTeamEntityIds(
              dataService.getOrCreateDefaultDomain().getId(), List.of(post.getId()));

      assertTrue(counts.isEmpty());
    }

    /**
     * The tenancy guard. This method takes a set of ids rather than one already-resolved id, so it
     * carries its own domain clause: handed the id of another tenant's entity, it must answer
     * nothing rather than that tenant's activity.
     */
    @Test
    void shouldNotCountAcrossDomains() {
      Domain other = dataService.createDomain("other-count.localhost", "Other", "http://other");
      User otherUser = dataService.createUser(other, "test@example.com", "Same email elsewhere");
      Team otherTeam =
          dataService.createTeam(other, otherUser, "Other Team", "other-team", Visibility.PUBLIC);
      Post otherPost = dataService.createPost(otherTeam, otherUser, "Other Post", Instant.now());
      dataService.createComment(otherUser, otherPost, "Other domain comment");
      dataService.createComment(user, post, "Home domain comment");

      Map<Long, Integer> counts =
          commentRepository.countByTeamEntityIds(
              dataService.getOrCreateDefaultDomain().getId(),
              List.of(post.getId(), otherPost.getId()));

      assertEquals(1, counts.get(post.getId()));
      assertNull(counts.get(otherPost.getId()));
    }
  }

  @Nested
  @DisplayName("pagination")
  class Pagination {

    @Test
    void pageRootsShouldIgnoreReplies() {
      Comment root1 = dataService.createComment(user, post, "Root 1");
      dataService.createReply(user, post, root1, "Reply");
      dataService.createComment(user, post, "Root 2");

      List<Comment> firstPage = commentRepository.pageRoots(post.getId(), 0, 1, SortDirection.ASC);

      assertEquals(1, firstPage.size());
      assertEquals("Root 1", firstPage.getFirst().getContent());
      assertEquals(2, commentRepository.countRoots(post.getId()));
      assertEquals(3, commentRepository.countByTeamEntityId(post.getId()));
    }

    @Test
    void pageRootsDescShouldStartFromTheNewest() {
      dataService.createComment(user, post, "Root 1");
      Comment root2 = dataService.createComment(user, post, "Root 2");

      List<Comment> page = commentRepository.pageRoots(post.getId(), 0, 1, SortDirection.DESC);

      assertEquals(root2.getId(), page.getFirst().getId());
    }

    @Test
    void findRepliesByParentIdsShouldLoadAWholePageOfThreadsAtOnce() {
      Comment root1 = dataService.createComment(user, post, "Root 1");
      Comment root2 = dataService.createComment(user, post, "Root 2");
      dataService.createReply(user, post, root1, "Reply to 1");
      dataService.createReply(user, post, root2, "Reply to 2");

      List<Comment> replies =
          commentRepository.findRepliesByParentIds(
              List.of(root1.getId(), root2.getId()), SortDirection.ASC);

      assertEquals(2, replies.size());
    }

    @Test
    void pageRepliesShouldRefuseAParentFromAnotherEntity() {
      Post otherPost = dataService.createPost(team, user, "Other Post", Instant.now());
      Comment foreignRoot = dataService.createComment(user, otherPost, "Elsewhere");
      dataService.createReply(user, otherPost, foreignRoot, "Elsewhere reply");

      List<Comment> replies =
          commentRepository.pageReplies(
              post.getId(), foreignRoot.getId(), 0, 20, SortDirection.ASC);

      assertTrue(replies.isEmpty());
      assertEquals(0, commentRepository.countReplies(post.getId(), foreignRoot.getId()));
    }
  }
}
