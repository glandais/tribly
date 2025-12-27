package com.tribly.service.post;

import com.tribly.domain.post.Post;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.enums.Status;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Scheduled job that auto-publishes posts when their publishAt time has passed.
 */
@ApplicationScoped
public class PostPublishScheduler {

  private static final Logger LOG = Logger.getLogger(PostPublishScheduler.class);

  @Inject PostRepository postRepository;

  /**
   * Runs every minute to check for posts that should be auto-published.
   * A post is auto-published if:
   * - status is DRAFT
   * - publishAt is not null
   * - publishAt <= current time
   */
  @Scheduled(every = "1m")
  @Transactional
  void autoPublishPosts() {
    List<Post> posts = postRepository.findPublicationsToAutoPublish();

    for (Post post : posts) {
      post.setStatus(Status.PUBLISHED);
      post.setDateTime(post.getPublishAt());
      post.setPublishAt(null); // Clear after publishing
      postRepository.persist(post);
      LOG.infov(
          "Auto-published post {0} '{1}' for team {2}",
          post.getId(), post.getName(), post.getTeam().getId());
    }

    if (!posts.isEmpty()) {
      LOG.infov("Auto-published {0} post(s)", posts.size());
    }
  }
}
