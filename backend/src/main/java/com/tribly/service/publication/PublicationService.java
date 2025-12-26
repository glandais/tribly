package com.tribly.service.publication;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.PublicationQuery;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService extends TeamEntityService {

  @Inject AllPublicationRepository publicationRepository;

  @Inject TeamRepository teamRepository;

  public PublicationListResponse listPublications(
      String slug,
      @Nullable Long userId,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      int page,
      int size) {
    Team team =
        teamRepository.findBySlug(slug).orElseThrow(() -> BusinessException.notFound("Team", slug));

    PublicationQueryParams publicationQueryParams = getPublicationQueryParams(userId, status, team);

    PublicationQuery publicationQuery =
        new PublicationQuery(
            team.getId(),
            page,
            size,
            null,
            publicationQueryParams.visibility(),
            from,
            to,
            publicationQueryParams.statuses());
    TriblyPage<Publication> publicationTriblyPage = publicationRepository.find(publicationQuery);

    List<PublicationDto> dtos =
        publicationTriblyPage.items().stream().map(PublicationDto::from).toList();
    return new PublicationListResponse(dtos, publicationTriblyPage.total(), page, size);
  }

  private record PublicationQueryParams(List<Status> statuses, @Nullable Visibility visibility) {}

  private PublicationQueryParams getPublicationQueryParams(
      @Nullable Long userId, @Nullable Status status, Team team) {
    boolean canSeeDrafts = securityService.canSeeDrafts(userId, team);
    List<Status> statuses = getPublicationStatuses(status, canSeeDrafts);

    Visibility visibility = getVisibility(userId, team);
    return new PublicationQueryParams(statuses, visibility);
  }

  private List<Status> getPublicationStatuses(@Nullable Status status, boolean canSeeDrafts) {
    List<Status> statuses;
    if (status == null) {
      if (canSeeDrafts) {
        statuses = Arrays.asList(Status.values());
      } else {
        statuses = List.of(Status.PUBLISHED, Status.CANCELLED);
      }
    } else {
      if (status.equals(Status.DRAFT) && !canSeeDrafts) {
        statuses = List.of();
      } else {
        statuses = List.of(status);
      }
    }
    return statuses;
  }
}
