package com.tribly.service.common;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.PublicationQuery;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.EntityType;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService extends TeamEntityService<Publication> {

  @Inject AllPublicationRepository allPublicationRepository;

  @Override
  protected EntityType getEntityType() {
    // not redirectable at this level
    return EntityType.POST;
  }

  @Override
  protected Optional<Publication> findByIdOptional(Long entityId) {
    return Optional.empty();
  }

  @Override
  @Nullable
  protected Publication getWithoutRedirect(
      String teamSlug, String entitySlug, @Nullable Long userId) {
    // not redirectable at this level
    throw BusinessException.notFound("Not found");
  }

  public PublicationListResponse list(
      @Nullable PublicationType type,
      @Nullable Set<String> teamSlugs,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Publication> publications =
        allPublicationRepository.find(
            PublicationQuery.builder()
                .userId(userId)
                .type(type)
                .teamSlugs(teamSlugs)
                .search(search)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<PublicationDto> dtos =
        publications.items().stream()
            .map(publication -> PublicationDto.from(publication, assetService))
            .toList();
    return new PublicationListResponse(dtos, publications.total(), page, size);
  }
}
