package com.tribly.service.post;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.exception.TriblyException;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PostServiceTest {

  @Inject PostService postService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext userService;

  private Team publicTeam;
  private Team privateTeam;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    admin = dataService.createUser("admin@example.com", "Admin");
    publicTeam = dataService.createTeam(admin, "Public Team", "public-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, publicTeam, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, publicTeam, TeamRole.MEMBER);
  }

  @Nested
  class GetPostDetail {

    @Test
    void shouldReturnPostForMember() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test Post", Instant.now(), Visibility.PUBLIC);

      userService.setContext(member);
      PostDto result = postService.getDto(publicTeam.getSlug(), post.getSlug());

      assertNotNull(result);
      assertEquals("Test Post", result.getName());
    }

    @Test
    void shouldReturnPublicPostForAnonymous() {
      Post post =
          dataService.createPost(
              publicTeam, admin, "Public Post", Instant.now(), Visibility.PUBLIC);

      userService.setContext(null);
      PostDto result = postService.getDto(publicTeam.getSlug(), post.getSlug());

      assertNotNull(result);
    }

    @Test
    void shouldThrowForTeamPostForAnonymous() {
      Post post =
          dataService.createPost(publicTeam, admin, "Team Post", Instant.now(), Visibility.TEAM);

      userService.setContext(null);
      assertThrows(
          TriblyException.class, () -> postService.getDto(publicTeam.getSlug(), post.getSlug()));
    }

    @Test
    void shouldThrowForNonexistentPost() {
      userService.setContext(admin);
      assertThrows(
          TriblyException.class, () -> postService.getDto(publicTeam.getSlug(), "nonexistent"));
    }
  }

  @Nested
  class CreatePost {

    @Test
    void shouldCreatePostForOrganizer() {
      Instant dateTime = Instant.now();
      PostRequest request =
          new PostRequest(
              "New Post",
              MediaDto.builder().markdown("Content").build(),
              dateTime,
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(organizer);
      PostDto result = postService.createPost(publicTeam.getSlug(), request);

      assertNotNull(result);
      assertEquals("New Post", result.getName());
      assertEquals(Visibility.PUBLIC, result.getVisibility());
      assertEquals(Status.PUBLISHED, result.getStatus());
    }

    @Test
    void shouldCreateDraftPost() {
      Instant dateTime = Instant.now();
      PostRequest request =
          new PostRequest(
              "Draft Post",
              MediaDto.builder().build(),
              dateTime,
              Status.DRAFT,
              Visibility.PUBLIC,
              null);

      userService.setContext(organizer);
      PostDto result = postService.createPost(publicTeam.getSlug(), request);

      assertEquals(Status.DRAFT, result.getStatus());
    }

    @Test
    void shouldCreatePostWithPublishAt() {
      Instant dateTime = Instant.now();
      Instant publishAt = dateTime.plus(1, ChronoUnit.DAYS);
      PostRequest request =
          new PostRequest(
              "Scheduled Post",
              MediaDto.builder().build(),
              dateTime,
              Status.DRAFT,
              Visibility.PUBLIC,
              publishAt);

      userService.setContext(organizer);
      PostDto result = postService.createPost(publicTeam.getSlug(), request);

      assertEquals(publishAt, result.getPublishAt());
    }

    @Test
    void shouldThrowForMember() {
      PostRequest request =
          new PostRequest(
              "Post",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(member);
      assertThrows(
          TriblyException.class, () -> postService.createPost(publicTeam.getSlug(), request));
    }

    @Test
    void shouldThrowForPublicPostInPrivateTeam() {
      PostRequest request =
          new PostRequest(
              "Public Post",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(admin);
      assertThrows(
          TriblyException.class, () -> postService.createPost(privateTeam.getSlug(), request));
    }

    @Test
    void shouldAllowTeamPostInPrivateTeam() {
      PostRequest request =
          new PostRequest(
              "Team Post",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.TEAM,
              null);

      userService.setContext(admin);
      PostDto result = postService.createPost(privateTeam.getSlug(), request);

      assertNotNull(result);
      assertEquals(Visibility.TEAM, result.getVisibility());
    }

    @Test
    void shouldGenerateUniqueSlug() {
      Instant now = Instant.now();
      PostRequest request1 =
          new PostRequest(
              "Same Name",
              MediaDto.builder().build(),
              now,
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);
      PostRequest request2 =
          new PostRequest(
              "Same Name",
              MediaDto.builder().build(),
              now,
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(organizer);
      PostDto result1 = postService.createPost(publicTeam.getSlug(), request1);
      userService.setContext(organizer);
      PostDto result2 = postService.createPost(publicTeam.getSlug(), request2);

      assertNotEquals(result1.getSlug(), result2.getSlug());
    }
  }

  @Nested
  class UpdatePost {

    @Test
    void shouldUpdateAllFields() {
      Post post =
          dataService.createPost(publicTeam, admin, "Original", Instant.now(), Visibility.PUBLIC);
      Instant newDateTime = Instant.now().plus(1, ChronoUnit.HOURS);
      PostRequest request =
          new PostRequest(
              "Updated",
              MediaDto.builder().markdown("New content").build(),
              newDateTime,
              Status.DRAFT,
              Visibility.TEAM,
              null);

      userService.setContext(organizer);
      PostDto result = postService.updatePost(publicTeam.getSlug(), post.getSlug(), request);

      assertEquals("Updated", result.getName());
      assertEquals(Visibility.TEAM, result.getVisibility());
      assertEquals(Status.DRAFT, result.getStatus());
    }

    @Test
    void shouldClearPublishAt() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test", Instant.now(), Visibility.PUBLIC);
      PostRequest request =
          new PostRequest(
              "Test",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(organizer);
      PostDto result = postService.updatePost(publicTeam.getSlug(), post.getSlug(), request);

      assertNull(result.getPublishAt());
    }

    @Test
    void shouldThrowForMember() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test", Instant.now(), Visibility.PUBLIC);
      PostRequest request =
          new PostRequest(
              "Updated",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(member);
      assertThrows(
          TriblyException.class,
          () -> postService.updatePost(publicTeam.getSlug(), post.getSlug(), request));
    }

    @Test
    void shouldThrowForPublicVisibilityInPrivateTeam() {
      Post post =
          dataService.createPost(privateTeam, admin, "Test", Instant.now(), Visibility.TEAM);
      PostRequest request =
          new PostRequest(
              "Test",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(admin);
      assertThrows(
          TriblyException.class,
          () -> postService.updatePost(privateTeam.getSlug(), post.getSlug(), request));
    }

    @Test
    void shouldAllowTeamPostUpdateInPrivateTeam() {
      Post post =
          dataService.createPost(privateTeam, admin, "Original", Instant.now(), Visibility.TEAM);
      PostRequest request =
          new PostRequest(
              "Updated Title",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.TEAM,
              null);

      userService.setContext(admin);
      PostDto result = postService.updatePost(privateTeam.getSlug(), post.getSlug(), request);

      assertNotNull(result);
      assertEquals("Updated Title", result.getName());
      assertEquals(Visibility.TEAM, result.getVisibility());
      assertEquals(Status.PUBLISHED, result.getStatus());
    }

    @Test
    void shouldThrowForNonexistentPost() {
      PostRequest request =
          new PostRequest(
              "Test",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      userService.setContext(admin);
      assertThrows(
          TriblyException.class,
          () -> postService.updatePost(publicTeam.getSlug(), "nonexistent", request));
    }
  }

  @Nested
  class DeletePost {

    @Test
    void shouldSoftDeletePost() {
      Post post =
          dataService.createPost(publicTeam, admin, "To Delete", Instant.now(), Visibility.PUBLIC);

      userService.setContext(organizer);
      postService.deletePost(publicTeam.getSlug(), post.getSlug());

      userService.setContext(admin);
      assertThrows(
          TriblyException.class, () -> postService.getDto(publicTeam.getSlug(), post.getSlug()));
    }

    @Test
    void shouldThrowForMember() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test", Instant.now(), Visibility.PUBLIC);

      userService.setContext(member);
      assertThrows(
          TriblyException.class,
          () -> postService.deletePost(publicTeam.getSlug(), post.getSlug()));
    }

    @Test
    void shouldThrowForNonexistentPost() {
      userService.setContext(admin);
      assertThrows(
          TriblyException.class, () -> postService.deletePost(publicTeam.getSlug(), "nonexistent"));
    }
  }
}
