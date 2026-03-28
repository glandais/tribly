package fr.pedalons.service.comment;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.dto.comments.response.CommentDto;
import fr.pedalons.dto.comments.response.CommentListResponse;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CommentServiceTest extends AbstractBaseTest {

  @Inject CommentService commentService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext userService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;
  private Post post;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    post = dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
  }

  @Nested
  class ListComments {

    @Test
    void shouldListCommentsForMember() {
      dataService.createComment(admin, post, "Comment 1");
      dataService.createComment(member, post, "Comment 2");

      userService.setUserForTest(member);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);

      assertEquals(2, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    void shouldReturnEmptyListForNoComments() {
      userService.setUserForTest(member);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);

      assertEquals(0, result.items().size());
      assertEquals(0, result.total());
    }

    @Test
    void shouldOrganizeReplies() {
      Comment parent = dataService.createComment(admin, post, "Parent comment");
      dataService.createReply(member, post, parent, "Reply 1");
      dataService.createReply(organizer, post, parent, "Reply 2");

      userService.setUserForTest(member);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);

      // Should have 1 parent with 2 replies, total 3 comments
      assertEquals(1, result.items().size());
      assertEquals(3, result.total());
      assertEquals(2, result.items().getFirst().replies().size());
    }

    @Test
    void shouldThrowForAnonymous() {
      userService.setUserForTest(null);
      assertThrows(
          PedalonsException.class,
          () -> commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST));
    }

    @Test
    void shouldThrowForNonMember() {
      userService.setUserForTest(nonMember);
      assertThrows(
          PedalonsException.class,
          () -> commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST));
    }

    @Test
    void shouldWorkWithRide() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      dataService.createComment(admin, ride, "Ride comment");

      userService.setUserForTest(member);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), ride.getSlug(), EntityType.RIDE);

      assertEquals(1, result.items().size());
    }
  }

  @Nested
  class CreateComment {

    @Test
    void shouldCreateCommentForMember() {
      CommentRequest request = new CommentRequest("My comment", null);

      userService.setUserForTest(member);
      CommentDto result =
          commentService.createComment(team.getSlug(), post.getSlug(), EntityType.POST, request);

      assertNotNull(result);
      assertEquals("My comment", result.content());
      assertNotNull(result.id());
    }

    @Test
    void shouldCreateReply() {
      Comment parent = dataService.createComment(admin, post, "Parent");
      String parentIdStr = TsidUtils.toString(parent.getId());
      CommentRequest request = new CommentRequest("Reply content", parentIdStr);

      userService.setUserForTest(member);
      CommentDto result =
          commentService.createComment(team.getSlug(), post.getSlug(), EntityType.POST, request);

      assertNotNull(result);
      assertEquals("Reply content", result.content());
    }

    @Test
    void shouldThrowForNestedReply() {
      Comment parent = dataService.createComment(admin, post, "Parent");
      Comment reply = dataService.createReply(admin, post, parent, "Reply");
      String replyIdStr = TsidUtils.toString(reply.getId());
      CommentRequest request = new CommentRequest("Nested reply", replyIdStr);

      userService.setUserForTest(member);
      assertThrows(
          BusinessException.class,
          () ->
              commentService.createComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, request));
    }

    @Test
    void shouldThrowForNonexistentParent() {
      CommentRequest request = new CommentRequest("Reply", "nonexistent");

      userService.setUserForTest(member);
      assertThrows(
          Exception.class,
          () ->
              commentService.createComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, request));
    }

    @Test
    void shouldThrowForAnonymous() {
      CommentRequest request = new CommentRequest("Comment", null);

      userService.setUserForTest(null);
      assertThrows(
          PedalonsException.class,
          () ->
              commentService.createComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, request));
    }

    @Test
    void shouldThrowForNonMember() {
      CommentRequest request = new CommentRequest("Comment", null);

      userService.setUserForTest(nonMember);
      assertThrows(
          PedalonsException.class,
          () ->
              commentService.createComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, request));
    }
  }

  @Nested
  class DeleteComment {

    @Test
    void shouldDeleteCommentForCreator() {
      Comment comment = dataService.createComment(member, post, "To delete");

      userService.setUserForTest(member);
      commentService.deleteComment(
          team.getSlug(), post.getSlug(), EntityType.POST, comment.getId());

      // Verify comment is deleted
      userService.setUserForTest(admin);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);
      assertEquals(0, result.total());
    }

    @Test
    void shouldDeleteCommentForOrganizer() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(organizer);
      commentService.deleteComment(
          team.getSlug(), post.getSlug(), EntityType.POST, comment.getId());

      // Verify comment is deleted
      userService.setUserForTest(admin);
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);
      assertEquals(0, result.total());
    }

    @Test
    void shouldDeleteCommentForAdmin() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(admin);
      commentService.deleteComment(
          team.getSlug(), post.getSlug(), EntityType.POST, comment.getId());

      // Verify comment is deleted
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);
      assertEquals(0, result.total());
    }

    @Test
    void shouldCascadeDeleteReplies() {
      Comment parent = dataService.createComment(admin, post, "Parent");
      dataService.createReply(member, post, parent, "Reply 1");
      dataService.createReply(organizer, post, parent, "Reply 2");

      userService.setUserForTest(admin);
      commentService.deleteComment(team.getSlug(), post.getSlug(), EntityType.POST, parent.getId());

      // All comments should be deleted
      CommentListResponse result =
          commentService.listComments(team.getSlug(), post.getSlug(), EntityType.POST);
      assertEquals(0, result.total());
    }

    @Test
    void shouldThrowForNonCreatorMember() {
      Comment comment = dataService.createComment(admin, post, "Admin comment");

      userService.setUserForTest(member);
      assertThrows(
          PedalonsException.class,
          () ->
              commentService.deleteComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
    }

    @Test
    void shouldThrowForNonexistentComment() {
      userService.setUserForTest(admin);
      assertThrows(
          Exception.class,
          () ->
              commentService.deleteComment(
                  team.getSlug(), post.getSlug(), EntityType.POST, 999999L));
    }
  }
}
