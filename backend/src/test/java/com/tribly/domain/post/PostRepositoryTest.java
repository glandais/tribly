package com.tribly.domain.post;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PostRepository}.
 */
@QuarkusTest
class PostRepositoryTest {

  @Inject PostRepository postRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Team team;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
  }

  @Nested
  @DisplayName("findByTeamAndSlug")
  class FindByTeamAndSlug {

    @Test
    @DisplayName("Should return post when team and slug match")
    void findByTeamAndSlug_shouldReturnPostWhenMatches() {
      Post post = dataService.createPost(team, user, "My First Post", now);

      Optional<Post> result = postRepository.findByTeamAndSlug(team.getId(), "my-first-post");

      assertTrue(result.isPresent());
      assertEquals(post.getId(), result.get().getId());
      assertEquals("My First Post", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when slug doesn't exist")
    void findByTeamAndSlug_shouldReturnEmptyWhenSlugNotExists() {
      dataService.createPost(team, user, "Existing Post", now);

      Optional<Post> result = postRepository.findByTeamAndSlug(team.getId(), "non-existent");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when team doesn't match")
    void findByTeamAndSlug_shouldReturnEmptyWhenTeamNotMatches() {
      dataService.createPost(team, user, "Post", now);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      Optional<Post> result = postRepository.findByTeamAndSlug(otherTeam.getId(), "post");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when post is deleted")
    void findByTeamAndSlug_shouldReturnEmptyWhenDeleted() {
      Post post = dataService.createPost(team, user, "Deleted Post", now);
      dataService.deletePost(post);

      Optional<Post> result = postRepository.findByTeamAndSlug(team.getId(), "deleted-post");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find posts with same slug in different teams")
    void findByTeamAndSlug_shouldFindPostsWithSameSlugInDifferentTeams() {
      Post post1 = dataService.createPost(team, user, "Announcement", now);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      Post post2 = dataService.createPost(otherTeam, user, "Announcement", now);

      Optional<Post> result1 = postRepository.findByTeamAndSlug(team.getId(), "announcement");
      Optional<Post> result2 = postRepository.findByTeamAndSlug(otherTeam.getId(), "announcement");

      assertTrue(result1.isPresent());
      assertTrue(result2.isPresent());
      assertEquals(post1.getId(), result1.get().getId());
      assertEquals(post2.getId(), result2.get().getId());
      assertNotEquals(result1.get().getId(), result2.get().getId());
    }
  }

  @Nested
  @DisplayName("existsByTeamAndSlug")
  class ExistsByTeamAndSlug {

    @Test
    @DisplayName("Should return true when post exists")
    void existsByTeamAndSlug_shouldReturnTrueWhenExists() {
      dataService.createPost(team, user, "Existing Post", now);

      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "existing-post");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when post doesn't exist")
    void existsByTeamAndSlug_shouldReturnFalseWhenNotExists() {
      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "non-existent");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false when slug exists in different team")
    void existsByTeamAndSlug_shouldReturnFalseWhenInDifferentTeam() {
      dataService.createPost(team, user, "Post", now);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      boolean exists = postRepository.existsByTeamAndSlug(otherTeam.getId(), "post");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true even for deleted posts (counts all)")
    void existsByTeamAndSlug_shouldReturnTrueForDeletedPosts() {
      // Note: existsByTeamAndSlug does NOT filter by deleted status
      // This is by design for slug uniqueness checking
      Post post = dataService.createPost(team, user, "Post", now);
      dataService.deletePost(post);

      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "post");

      assertTrue(exists);
    }
  }

  @Nested
  @DisplayName("find")
  class Find {

    @Test
    @DisplayName("Should return posts")
    void find_shouldReturnPosts() {
      dataService.createPost(team, user, "Test Post", now);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Post> result = postRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Test Post", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return deleted posts")
    void find_shouldNotReturnDeleted() {
      Post post = dataService.createPost(team, user, "Deleted Post", now);
      dataService.createPost(team, user, "Active Post", now.plus(1, ChronoUnit.HOURS));
      dataService.deletePost(post);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Post> result = postRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Active Post", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return draft posts for anonymous users")
    void find_shouldNotReturnDraftForAnonymous() {
      dataService.createPost(
          team, user, "Published Post", now, Visibility.PUBLIC, Status.PUBLISHED);
      dataService.createPost(team, user, "Draft Post", now, Visibility.PUBLIC, Status.DRAFT);

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Post> result = postRepository.find(query);

      assertEquals(1, result.total());
      assertEquals("Published Post", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should support pagination")
    void find_shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createPost(team, user, "Post " + i, now.plus(i, ChronoUnit.HOURS));
      }

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 2);
      TriblyPage<Post> result = postRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(5, result.total());
    }

    @Test
    @DisplayName("Should return posts from multiple teams")
    void find_shouldReturnPostsFromMultipleTeams() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createPost(team, user, "Post in Team 1", now);
      dataService.createPost(otherTeam, user, "Post in Team 2", now.plus(1, ChronoUnit.HOURS));

      TeamEntityQueryBasic query =
          new TeamEntityQueryBasic(null, null, null, null, null, null, null, 0, 10);
      TriblyPage<Post> result = postRepository.find(query);

      assertEquals(2, result.total());
    }
  }

  @Nested
  @DisplayName("Post Entity Properties")
  class PostEntityProperties {

    @Test
    @DisplayName("Should create post with default PUBLISHED status")
    void createPost_shouldHaveDefaultPublishedStatus() {
      Post post = dataService.createPost(team, user, "Default Post", now);

      assertEquals(Status.PUBLISHED, post.getStatus());
      assertNull(post.getPublishAt());
    }

    @Test
    @DisplayName("Should create post with custom status and publishAt")
    void createPost_shouldAllowCustomStatusAndPublishAt() {
      Instant publishTime = now.plus(1, ChronoUnit.DAYS);
      Post post =
          dataService.createPost(
              team, user, "Scheduled Post", now, Visibility.PUBLIC, Status.DRAFT, publishTime);

      assertEquals(Status.DRAFT, post.getStatus());
      assertEquals(publishTime, post.getPublishAt());
    }

    @Test
    @DisplayName("Should generate slug from name")
    void createPost_shouldGenerateSlugFromName() {
      Post post = dataService.createPost(team, user, "My Awesome Post Title", now);

      assertEquals("my-awesome-post-title", post.getSlug());
    }

    @Test
    @DisplayName("Should preserve team association")
    void createPost_shouldPreserveTeamAssociation() {
      Post post = dataService.createPost(team, user, "Post", now);

      Optional<Post> found = postRepository.findByTeamAndSlug(team.getId(), "post");

      assertTrue(found.isPresent());
      assertEquals(team.getId(), found.get().getTeam().getId());
    }

    @Test
    @DisplayName("Should preserve visibility setting")
    void createPost_shouldPreserveVisibilitySetting() {
      Post publicPost = dataService.createPost(team, user, "Public Post", now, Visibility.PUBLIC);
      Post teamPost = dataService.createPost(team, user, "Team Post", now, Visibility.TEAM);

      assertEquals(Visibility.PUBLIC, publicPost.getVisibility());
      assertEquals(Visibility.TEAM, teamPost.getVisibility());
    }

    @Test
    @DisplayName("Should preserve dateTime")
    void createPost_shouldPreserveDateTime() {
      Instant specificTime = Instant.parse("2025-06-15T10:30:00Z");
      Post post = dataService.createPost(team, user, "Timed Post", specificTime);

      assertEquals(specificTime, post.getDateTime());
    }
  }
}
