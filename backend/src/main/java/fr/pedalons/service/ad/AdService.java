package fr.pedalons.service.ad;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.ads.request.AdRequest;
import fr.pedalons.dto.ads.response.AdDto;
import fr.pedalons.dto.ads.response.AdEditDto;
import fr.pedalons.dto.ads.response.AdListResponse;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.*;
import fr.pedalons.repository.ad.AdQuery;
import fr.pedalons.repository.ad.AdRepository;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdService extends TeamEntityService<Ad, AdRepository, AdDto> {

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
  protected Ad findBySlug(Team team, String entitySlug) {
    return super.findBySlug(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.AD, action = ActionType.READ)
  public AdDto getDto(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.getDto(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.AD, action = ActionType.UPDATE)
  public AdEditDto getDtoEdit(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    Ad entity = findBySlug(team, entitySlug);
    return AdEditDto.from(entity, assetService);
  }

  @CheckAccess(entityType = EntityType.AD, action = ActionType.LIST)
  public AdListResponse listAds(
      String teamSlug,
      @Nullable String search,
      @Nullable AdType adType,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    Team team = teamService.getTeam(teamSlug);
    PedalonsPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .domainId(pedalonsContext.getDomainId())
                .userId(pedalonsContext.getUserIdNullable())
                .teamIds(Set.of(team.getId()))
                .search(search)
                .adType(adType)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .platformAdmin(isPlatformAdmin())
                .build());
    List<AdDto> dtos = ads.items().stream().map(ad -> AdDto.from(ad, assetService)).toList();
    return new AdListResponse(dtos, ads.total(), page, size);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.AD, action = ActionType.CREATE)
  public AdDto createAd(String teamSlug, AdRequest request) {
    Team team = teamService.getTeam(teamSlug);
    verifyAd(team, request);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), adRepository);

    Ad ad =
        new Ad(
            pedalonsContext.getUser(), team, Instant.now(), request.name(), slug, request.adType());

    setProperties(request, ad);

    adRepository.persistAndFlush(ad);

    updateMedia(ad, request.media());

    adRepository.persist(ad);

    return AdDto.from(ad, assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.AD, action = ActionType.UPDATE)
  public AdDto updateAd(String teamSlug, String adSlug, AdRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Ad ad = findBySlug(team, adSlug);

    // Validate visibility: private teams can only have team-only ads
    verifyAd(team, request);

    setProperties(request, ad);

    updateMedia(ad, request.media());

    adRepository.persist(ad);

    return AdDto.from(ad, assetService);
  }

  @CheckAccess(entityType = EntityType.AD, action = ActionType.UPDATE)
  @Transactional
  public AdDto updateSlug(String teamSlug, String slug, String newSlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.updateSlug(team, slug, newSlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.AD, action = ActionType.DELETE)
  public void deleteAd(String teamSlug, String adSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ad ad = findBySlug(team, adSlug);
    ad.setDeleted(true);
    adRepository.persist(ad);
  }

  @CheckAccess(entityType = EntityType.AD, action = ActionType.DELETE)
  @Transactional
  public AdEditDto undeleteAd(String teamSlug, String adSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ad ad = findBySlugIncludeDeleted(team, adSlug);
    ad.setDeleted(false);
    adRepository.persist(ad);
    return AdEditDto.from(ad, assetService);
  }

  private void verifyAd(Team team, AdRequest request) {
    // Validate rental period for rental ads
    if (request.adType() == AdType.RENTAL && request.rentalPeriod() == null) {
      throw new BusinessException(ErrorCode.RENTAL_PERIOD_MISSING);
    }

    if (request.status() == Status.CANCELLED) {
      throw new BusinessException(ErrorCode.STATUS_INVALID);
    }
  }

  private void setProperties(AdRequest request, Ad ad) {
    ad.setVisibility(Visibility.TEAM);
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
    ad.setLocationGeometry(request.locationGeometry());
  }
}
