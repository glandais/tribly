package com.tribly.service.post;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.posts.response.PostListResponse;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
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
    dataService.addUserToTeam(admin, publicTeam, TeamRole.ADMIN);
    dataService.addUserToTeam(organizer, publicTeam, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, publicTeam, TeamRole.MEMBER);
    dataService.addUserToTeam(admin, privateTeam, TeamRole.ADMIN);
  }

  @Nested
  class ListPosts {

    @Test
    void shouldReturnPublicPostsForAnonymous() {
      Instant now = Instant.now();
      dataService.createPost(publicTeam, admin, "Public Post", now, Visibility.PUBLIC);
      dataService.createPost(publicTeam, admin, "Team Post", now, Visibility.TEAM);

      PostListResponse result = postService.listPosts("public-team", null, null, null, null, 0, 10);

      assertEquals(1, result.posts().size());
      assertEquals("Public Post", result.posts().getFirst().getName());
    }

    @Test
    void shouldReturnAllPostsForMember() {
      Instant now = Instant.now();
      dataService.createPost(publicTeam, admin, "Public Post", now, Visibility.PUBLIC);
      dataService.createPost(publicTeam, admin, "Team Post", now, Visibility.TEAM);

      PostListResponse result =
          postService.listPosts("public-team", member.getId(), null, null, null, 0, 10);

      assertEquals(2, result.posts().size());
    }

    @Test
    void shouldFilterBySearch() {
      Instant now = Instant.now();
      dataService.createPost(publicTeam, admin, "Important Update", now, Visibility.PUBLIC);
      dataService.createPost(publicTeam, admin, "Weekly News", now, Visibility.PUBLIC);

      PostListResponse result =
          postService.listPosts("public-team", null, "Important", null, null, 0, 10);

      assertEquals(1, result.posts().size());
      assertEquals("Important Update", result.posts().getFirst().getName());
    }

    @Test
    void shouldFilterByDateRange() {
      Instant now = Instant.now();
      Instant lastWeek = now.minus(7, ChronoUnit.DAYS);
      dataService.createPost(publicTeam, admin, "Recent Post", now, Visibility.PUBLIC);
      dataService.createPost(publicTeam, admin, "Old Post", lastWeek, Visibility.PUBLIC);

      PostListResponse result =
          postService.listPosts(
              "public-team",
              null,
              null,
              now.minus(1, ChronoUnit.DAYS),
              now.plus(1, ChronoUnit.DAYS),
              0,
              10);

      assertEquals(1, result.posts().size());
      assertEquals("Recent Post", result.posts().getFirst().getName());
    }

    @Test
    void shouldSupportPagination() {
      Instant now = Instant.now();
      for (int i = 1; i <= 5; i++) {
        dataService.createPost(
            publicTeam, admin, "Post " + i, now.plus(i, ChronoUnit.HOURS), Visibility.PUBLIC);
      }

      PostListResponse result = postService.listPosts("public-team", null, null, null, null, 0, 3);

      assertEquals(3, result.posts().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldExcludeDeletedPosts() {
      Instant now = Instant.now();
      dataService.createPost(publicTeam, admin, "Active Post", now, Visibility.PUBLIC);
      Post deletedPost =
          dataService.createPost(publicTeam, admin, "Deleted Post", now, Visibility.PUBLIC);
      dataService.deletePost(deletedPost);

      PostListResponse result = postService.listPosts("public-team", null, null, null, null, 0, 10);

      assertEquals(1, result.posts().size());
    }

    @Test
    void shouldExcludeDraftPostsForNonOrganizer() {
      Instant now = Instant.now();
      dataService.createPost(
          publicTeam, admin, "Published Post", now, Visibility.PUBLIC, Status.PUBLISHED);
      dataService.createPost(publicTeam, admin, "Draft Post", now, Visibility.PUBLIC, Status.DRAFT);

      PostListResponse result =
          postService.listPosts("public-team", member.getId(), null, null, null, 0, 10);

      assertEquals(1, result.posts().size());
      assertEquals("Published Post", result.posts().getFirst().getName());
    }
  }

  @Nested
  class GetPostDetail {

    @Test
    void shouldReturnPostForMember() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test Post", Instant.now(), Visibility.PUBLIC);

      PostDto result = postService.getPostDetail("public-team", post.getSlug(), member.getId());

      assertNotNull(result);
      assertEquals("Test Post", result.getName());
    }

    @Test
    void shouldReturnPublicPostForAnonymous() {
      Post post =
          dataService.createPost(
              publicTeam, admin, "Public Post", Instant.now(), Visibility.PUBLIC);

      PostDto result = postService.getPostDetail("public-team", post.getSlug(), null);

      assertNotNull(result);
    }

    @Test
    void shouldThrowForTeamPostForAnonymous() {
      Post post =
          dataService.createPost(publicTeam, admin, "Team Post", Instant.now(), Visibility.TEAM);

      assertThrows(
          BusinessException.class,
          () -> postService.getPostDetail("public-team", post.getSlug(), null));
    }

    @Test
    void shouldThrowForNonexistentPost() {
      assertThrows(
          BusinessException.class,
          () -> postService.getPostDetail("public-team", "nonexistent", admin.getId()));
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

      PostDto result = postService.createPost("public-team", request, organizer.getId());

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

      PostDto result = postService.createPost("public-team", request, organizer.getId());

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

      PostDto result = postService.createPost("public-team", request, organizer.getId());

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

      assertThrows(
          BusinessException.class,
          () -> postService.createPost("public-team", request, member.getId()));
    }

    @Test
    void shouldThrowForNonexistentTeam() {
      PostRequest request =
          new PostRequest(
              "Post",
              MediaDto.builder().build(),
              Instant.now(),
              Status.PUBLISHED,
              Visibility.PUBLIC,
              null);

      assertThrows(
          BusinessException.class,
          () -> postService.createPost("nonexistent", request, admin.getId()));
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

      assertThrows(
          BusinessException.class,
          () -> postService.createPost("private-team", request, admin.getId()));
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

      PostDto result = postService.createPost("private-team", request, admin.getId());

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

      PostDto result1 = postService.createPost("public-team", request1, organizer.getId());
      PostDto result2 = postService.createPost("public-team", request2, organizer.getId());

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

      PostDto result =
          postService.updatePost("public-team", post.getSlug(), request, organizer.getId());

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

      PostDto result =
          postService.updatePost("public-team", post.getSlug(), request, organizer.getId());

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

      assertThrows(
          BusinessException.class,
          () -> postService.updatePost("public-team", post.getSlug(), request, member.getId()));
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

      assertThrows(
          BusinessException.class,
          () -> postService.updatePost("private-team", post.getSlug(), request, admin.getId()));
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

      PostDto result =
          postService.updatePost("private-team", post.getSlug(), request, admin.getId());

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

      assertThrows(
          BusinessException.class,
          () -> postService.updatePost("public-team", "nonexistent", request, admin.getId()));
    }
  }

  @Nested
  class DeletePost {

    @Test
    void shouldSoftDeletePost() {
      Post post =
          dataService.createPost(publicTeam, admin, "To Delete", Instant.now(), Visibility.PUBLIC);

      postService.deletePost("public-team", post.getSlug(), organizer.getId());

      assertThrows(
          BusinessException.class,
          () -> postService.getPostDetail("public-team", post.getSlug(), admin.getId()));
    }

    @Test
    void shouldThrowForMember() {
      Post post =
          dataService.createPost(publicTeam, admin, "Test", Instant.now(), Visibility.PUBLIC);

      assertThrows(
          BusinessException.class,
          () -> postService.deletePost("public-team", post.getSlug(), member.getId()));
    }

    @Test
    void shouldThrowForNonexistentPost() {
      assertThrows(
          BusinessException.class,
          () -> postService.deletePost("public-team", "nonexistent", admin.getId()));
    }
  }
}
