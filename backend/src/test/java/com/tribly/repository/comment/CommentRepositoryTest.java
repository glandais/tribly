package com.tribly.repository.comment;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.comment.Comment;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CommentRepositoryTest {

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
}
