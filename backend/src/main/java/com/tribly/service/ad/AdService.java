package com.tribly.service.ad;

import com.tribly.domain.ad.Ad;
import com.tribly.domain.ad.repository.AdQuery;
import com.tribly.domain.ad.repository.AdRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.ads.request.AdRequest;
import com.tribly.dto.ads.response.AdDto;
import com.tribly.dto.ads.response.AdListResponse;
import com.tribly.enums.AdType;
import com.tribly.enums.Status;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(AdService.class);

  @Inject AdRepository adRepository;

  public AdListResponse listAds(
      String teamSlug,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable AdType adType,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .search(search)
                .adType(adType)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<AdDto> dtos = ads.items().stream().map(ad -> AdDto.from(ad, assetService)).toList();
    return new AdListResponse(dtos, ads.total(), page, size);
  }

  public Ad getAd(String teamSlug, String adSlug, @Nullable Long userId) {
    TriblyPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .slug(adSlug)
                .page(0)
                .size(1)
                .build());
    if (ads.items().isEmpty()) {
      throw BusinessException.notFound("Ad", adSlug);
    } else {
      return ads.items().getFirst();
    }
  }

  public AdDto getAdDetail(String teamSlug, String adSlug, @Nullable Long userId) {
    return AdDto.from(getAd(teamSlug, adSlug, userId), assetService);
  }

  @Transactional
  public AdDto createAd(String teamSlug, AdRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: any team member can create ads
    securityService.requireMembership(creatorId, team.getSlug());

    verifyAd(request, team);

    // Generate slug from name, ensure unique within team
    String slug =
        slugService.generateSlug(
            request.name(), s -> adRepository.existsByTeamAndSlug(team.getId(), s));

    Ad ad =
        new Ad(
            creator,
            team,
            Instant.now(),
            request.name(),
            slug,
            request.visibility(),
            request.adType());

    setProperties(request, ad);

    adRepository.persistAndFlush(ad);

    updateMedia(ad, request.media());

    adRepository.persist(ad);

    LOG.infov("Ad '{0}' created by user {1} for team {2}", ad.getName(), creatorId, teamSlug);
    return AdDto.from(ad, assetService);
  }

  @Transactional
  public AdDto updateAd(String teamSlug, String adSlug, AdRequest request, Long userId) {
    Ad ad = getAd(teamSlug, adSlug, userId);

    // Security check: only creator or admin can edit
    requireEditPermission(userId, teamSlug, ad);

    // Validate visibility: private teams can only have team-only ads
    Team team = ad.getTeam();
    verifyAd(request, team);

    setProperties(request, ad);

    updateMedia(ad, request.media());

    adRepository.persist(ad);

    LOG.infov("Ad {0} updated by user {1}", adSlug, userId);
    return AdDto.from(ad, assetService);
  }

  @Transactional
  public void deleteAd(String teamSlug, String adSlug, Long userId) {
    Ad ad = getAd(teamSlug, adSlug, userId);

    // Security check: only creator or admin can delete
    requireEditPermission(userId, teamSlug, ad);

    ad.setDeleted(true);
    adRepository.persist(ad);
    LOG.infov("Ad {0} deleted by user {1}", adSlug, userId);
  }

  private void requireEditPermission(Long userId, String teamSlug, Ad ad) {
    UserTeam membership = securityService.requireMembership(userId, teamSlug);
    boolean isCreator = ad.getCreatedBy().getId().equals(userId);
    if (!isCreator && !membership.isAdmin()) {
      throw BusinessException.forbidden("Only the creator or an admin can edit this ad");
    }
  }

  private void verifyAd(AdRequest request, Team team) {
    validateVisibility(request, team);

    // Validate rental period for rental ads
    if (request.adType() == AdType.RENTAL && request.rentalPeriod() == null) {
      throw BusinessException.validation("Rental period is required for rental ads");
    }

    if (request.status() == Status.CANCELLED) {
      throw BusinessException.validation("Ad can't be cancelled");
    }
  }

  private void setProperties(AdRequest request, Ad ad) {
    ad.setVisibility(request.visibility());
    ad.setName(request.name());
    ad.setStatus(request.status());
    ad.setPublishAt(null);
    ad.setAdType(request.adType());
    ad.setPrice(request.price());
    if (request.adType() == AdType.RENTAL) {
      ad.setRentalPeriod(request.rentalPeriod());
    } else {
      ad.setRentalPeriod(null);
    }
    ad.setLocationDescription(request.locationDescription());
  }
}
