package com.tribly.service.post;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.post.Post;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PostPublishSchedulerTest {

  @Inject PostPublishScheduler postPublishScheduler;
  @Inject PostRepository postRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Test
  void autoPublishPosts_shouldPublishPostsWithPastPublishAt() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Auto Publish",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublishPosts_shouldNotPublishPostsWithFuturePublishAt() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Future Publish",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().plusSeconds(3600));

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
    assertNotNull(updated.getPublishAt());
  }

  @Test
  void autoPublishPosts_shouldNotPublishAlreadyPublishedPosts() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Already Published",
            Instant.now(),
            Visibility.PUBLIC,
            Status.PUBLISHED,
            Instant.now().minusSeconds(3600));

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
  }

  @Test
  void autoPublishPosts_shouldNotPublishCancelledPosts() {
    Post post =
        dataService.createPost(
            team,
            user,
            "Cancelled",
            Instant.now(),
            Visibility.PUBLIC,
            Status.CANCELLED,
            Instant.now().minusSeconds(3600));

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.CANCELLED, updated.getStatus());
  }

  @Test
  void autoPublishPosts_shouldHandleMultiplePosts() {
    Post post1 =
        dataService.createPost(
            team,
            user,
            "Post 1",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(7200));
    Post post2 =
        dataService.createPost(
            team,
            user,
            "Post 2",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(3600));
    Post post3 =
        dataService.createPost(
            team,
            user,
            "Post 3",
            Instant.now(),
            Visibility.PUBLIC,
            Status.DRAFT,
            Instant.now().minusSeconds(1800));

    postPublishScheduler.autoPublishPosts();

    Post updated1 = postRepository.findById(post1.getId());
    Post updated2 = postRepository.findById(post2.getId());
    Post updated3 = postRepository.findById(post3.getId());

    assertEquals(Status.PUBLISHED, updated1.getStatus());
    assertEquals(Status.PUBLISHED, updated2.getStatus());
    assertEquals(Status.PUBLISHED, updated3.getStatus());
    assertNull(updated1.getPublishAt());
    assertNull(updated2.getPublishAt());
    assertNull(updated3.getPublishAt());
  }

  @Test
  void autoPublishPosts_shouldDoNothingWhenNoPosts() {
    postPublishScheduler.autoPublishPosts();

    // Should not throw exception
    assertTrue(true);
  }

  @Test
  void autoPublishPosts_shouldNotPublishPostsWithoutPublishAt() {
    Post post =
        dataService.createPost(
            team, user, "No PublishAt", Instant.now(), Visibility.PUBLIC, Status.DRAFT);

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.DRAFT, updated.getStatus());
  }

  @Test
  void autoPublishPosts_shouldPublishAtExactPublishTime() {
    Instant publishTime = Instant.now();
    Post post =
        dataService.createPost(
            team, user, "Exact Time", Instant.now(), Visibility.PUBLIC, Status.DRAFT, publishTime);

    // Wait a moment to ensure we're past publishTime
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertNull(updated.getPublishAt());
  }

  @Test
  void autoPublishPosts_shouldUpdateDateTimeToPublishAt() {
    Instant originalDateTime = Instant.now().minusSeconds(86400); // yesterday
    Instant publishAt =
        Instant.now().minusSeconds(3600).truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    Post post =
        dataService.createPost(
            team,
            user,
            "DateTime Update",
            originalDateTime,
            Visibility.PUBLIC,
            Status.DRAFT,
            publishAt);

    postPublishScheduler.autoPublishPosts();

    Post updated = postRepository.findById(post.getId());
    assertEquals(Status.PUBLISHED, updated.getStatus());
    assertEquals(publishAt, updated.getDateTime());
  }
}
