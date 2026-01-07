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
import com.tribly.dto.error.ErrorCode;
import com.tribly.enums.ActionType;
import com.tribly.enums.AdType;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.Status;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.NotFoundException;
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
public class AdService extends TeamEntityService<Ad, AdRepository, AdDto> {

  private static final Logger LOG = Logger.getLogger(AdService.class);

  @Inject AdRepository adRepository;

  @Override
  protected AdRepository getRepository() {
    return adRepository;
  }

  @Override
  protected AdDto toDto(Ad entity) {
    return AdDto.from(entity, assetService);
  }

  @Override
  protected boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable Ad entity) {
    return switch (action) {
      case CREATE -> securityService.getMembership(user, team) != null && team.isEnableTrips();
      // SQL
      case READ -> true;
      case JOIN -> false;
      case UPDATE, DELETE -> {
        if (entity == null || user == null || !team.isEnableTrips()) {
          yield false;
        }
        UserTeam membership = securityService.getMembership(user, team);
        if (membership == null) {
          yield false;
        }
        boolean isCreator = entity.getCreatedBy().getId().equals(user.getId());
        yield isCreator || membership.isAdmin();
      }
    };
  }

  @Override
  protected Ad getBySlug(Team team, String adSlug, @Nullable User user) {
    TriblyPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
                .slug(adSlug)
                .page(0)
                .size(1)
                .build());
    if (ads.items().isEmpty()) {
      throw new NotFoundException(AllEntityType.AD, adSlug);
    } else {
      return ads.items().getFirst();
    }
  }

  public AdListResponse listAds(
      Team team,
      @Nullable User user,
      @Nullable String search,
      @Nullable AdType adType,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
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

  @Transactional
  public AdDto createAd(Team team, AdRequest request, User creator) {
    // Security check: any team member can create ads
    checkRights(ActionType.CREATE, team, creator, null);

    verifyAd(request, team);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), adRepository);

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

    LOG.infov(
        "Ad '{0}' created by user {1} for team {2}", ad.getName(), creator.getId(), team.getSlug());
    return AdDto.from(ad, assetService);
  }

  @Transactional
  public AdDto updateAd(Team team, String adSlug, AdRequest request, User user) {
    Ad ad = get(ActionType.UPDATE, team, adSlug, user);

    // Validate visibility: private teams can only have team-only ads
    verifyAd(request, team);

    setProperties(request, ad);

    updateMedia(ad, request.media());

    adRepository.persist(ad);

    LOG.infov("Ad {0} updated by user {1}", adSlug, user.getId());
    return AdDto.from(ad, assetService);
  }

  @Transactional
  public void deleteAd(Team team, String adSlug, User user) {
    Ad ad = get(ActionType.DELETE, team, adSlug, user);

    ad.setDeleted(true);
    adRepository.persist(ad);
    LOG.infov("Ad {0} deleted by user {1}", adSlug, user.getId());
  }

  private void verifyAd(AdRequest request, Team team) {
    validateVisibility(request, team);

    // Validate rental period for rental ads
    if (request.adType() == AdType.RENTAL && request.rentalPeriod() == null) {
      throw new BusinessException(ErrorCode.RENTAL_PERIOD_MISSING);
    }

    if (request.status() == Status.CANCELLED) {
      throw new BusinessException(ErrorCode.STATUS_INVALID);
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
