package com.tribly.service.common;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService extends TeamEntityService {

  @Inject AllPublicationRepository allPublicationRepository;

  public PublicationListResponse list(
      @Nullable Set<String> teamSlugs,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Publication> publications =
        allPublicationRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
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
