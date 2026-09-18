package fr.pedalons.service.publication;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.post.Post;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.common.AllPublicationRepository;
import fr.pedalons.service.notification.NotificationPublisher;
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

  @Inject NotificationPublisher notificationPublisher;

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
      // Nobody pressed "publish": the author is the closest thing to an actor, and is spared
      // being told about their own publication.
      notificationPublisher.publicationStatusChanged(
          publication, Status.DRAFT, publication.getCreatedBy());
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
