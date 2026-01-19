package com.tribly.service.comment;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.comment.Comment;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CommentAccessCheckerTest {

  @Inject CommentAccessChecker commentAccessChecker;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext userService;
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

  @Test
  void getType_shouldReturnComment() {
    assertEquals(EntityType.COMMENT, commentAccessChecker.getType());
  }

  @Nested
  class ListAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.LIST, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.LIST, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.LIST, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }
  }

  @Nested
  class ReadAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.READ, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.READ, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }
  }

  @Nested
  class CreateAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.CREATE, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.CREATE, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.CREATE, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }
  }

  @Nested
  class UpdateAction {

    @Test
    void shouldAllowForCreator() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForOrganizer() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(organizer);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(admin);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForNonCreatorMember() {
      Comment comment = dataService.createComment(admin, post, "Admin comment");

      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForMissingCommentId() {
      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), post.getSlug(), EntityType.POST));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonexistentComment() {
      userService.setUserForTest(admin);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.UPDATE, List.of(team.getSlug(), post.getSlug(), EntityType.POST, 999999L));
      assertFalse(result);
    }
  }

  @Nested
  class DeleteAction {

    @Test
    void shouldAllowForCreator() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.DELETE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForOrganizer() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(organizer);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.DELETE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      Comment comment = dataService.createComment(member, post, "Member comment");

      userService.setUserForTest(admin);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.DELETE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForNonCreatorMember() {
      Comment comment = dataService.createComment(admin, post, "Admin comment");

      userService.setUserForTest(member);
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.DELETE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForCommentOnDifferentEntity() {
      Post otherPost =
          dataService.createPost(team, admin, "Other Post", Instant.now(), Visibility.PUBLIC);
      Comment comment = dataService.createComment(admin, otherPost, "Comment on other post");

      userService.setUserForTest(admin);
      // Trying to delete comment using wrong entity slug
      boolean result =
          commentAccessChecker.hasRights(
              ActionType.DELETE,
              List.of(team.getSlug(), post.getSlug(), EntityType.POST, comment.getId()));
      assertFalse(result);
    }
  }

  @Nested
  class UnsupportedActions {

    @Test
    void shouldDenyListAllTeams() {
      userService.setUserForTest(admin);
      boolean result =
          commentAccessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyJoin() {
      userService.setUserForTest(admin);
      boolean result = commentAccessChecker.hasRights(ActionType.JOIN, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyLeave() {
      userService.setUserForTest(admin);
      boolean result = commentAccessChecker.hasRights(ActionType.LEAVE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }
}
