package com.tribly.service.publication;

import com.tribly.domain.common.Publication;
import com.tribly.domain.post.Post;
import com.tribly.enums.Status;
import com.tribly.repository.common.AllPublicationRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Scheduled job that auto-publishes publications (rides, posts, trips) when their publishAt time
 * has passed.
 */
@ApplicationScoped
public class PublicationPublishScheduler {

  private static final Logger LOG = Logger.getLogger(PublicationPublishScheduler.class);

  @Inject AllPublicationRepository publicationRepository;

  /**
   * Runs every minute to check for publications that should be auto-published. A publication is
   * auto-published if:
   *
   * <ul>
   *   <li>status is DRAFT
   *   <li>publishAt is not null
   *   <li>publishAt <= current time
   * </ul>
   */
  @Scheduled(every = "1m")
  @Transactional
  void autoPublishPublications() {
    List<Publication> publications = publicationRepository.findPublicationsToAutoPublish();

    for (Publication publication : publications) {
      publication.setStatus(Status.PUBLISHED);

      // Only Posts update dateTime to publishAt
      if (publication instanceof Post post) {
        post.setDateTime(post.getPublishAt());
      }

      publication.setPublishAt(null); // Clear after publishing
      publicationRepository.persist(publication);
      LOG.infov(
          "Auto-published {0} {1} ''{2}'' for team {3}",
          publication.getClass().getSimpleName(),
          publication.getId(),
          publication.getName(),
          publication.getTeam().getId());
    }

    if (!publications.isEmpty()) {
      LOG.infov("Auto-published {0} publication(s)", publications.size());
    }
  }
}
