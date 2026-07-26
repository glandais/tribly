package fr.pedalons.service.ad;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.InternalException;
import fr.pedalons.common.exception.TooManyRequestsException;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.ad.AdContact;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.ads.request.AdContactRequest;
import fr.pedalons.dto.ads.request.AdRequest;
import fr.pedalons.dto.ads.request.AdSearchParams;
import fr.pedalons.dto.ads.response.AdDto;
import fr.pedalons.dto.ads.response.AdEditDto;
import fr.pedalons.dto.ads.response.AdListResponse;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.*;
import fr.pedalons.repository.ad.AdContactRepository;
import fr.pedalons.repository.ad.AdQuery;
import fr.pedalons.repository.ad.AdRepository;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.security.annotation.Logged;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdService extends TeamEntityService<Ad, AdRepository, AdDto> {

  @Inject AdRepository adRepository;

  @Inject AdContactRepository adContactRepository;

  @Inject AdContactEmailService adContactEmailService;

  @ConfigProperty(name = "pedalons.ads.contact.max-per-window", defaultValue = "10")
  int contactMaxPerWindow;

  @ConfigProperty(name = "pedalons.ads.contact.rate-limit-window-minutes", defaultValue = "60")
  int contactWindowMinutes;

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
    return listAds(teamSlug, search, adType, from, to, ListViewMode.FULL, page, size);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} strips the description and the asset inventory from every
   *     row; the rows are otherwise identical, and the query is exactly the same
   */
  @CheckAccess(entityType = EntityType.AD, action = ActionType.LIST)
  public AdListResponse listAds(
      String teamSlug,
      @Nullable String search,
      @Nullable AdType adType,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable ListViewMode view,
      int page,
      int size) {
    return listAds(
        teamSlug,
        AdSearchParams.builder().search(search).adType(adType).from(from).to(to).build(),
        view,
        page,
        size);
  }

  /**
   * The classifieds of a team, filtered and sorted.
   *
   * <p>The filters live in {@link AdSearchParams} rather than in the signature so that adding one
   * does not ripple through every caller — the route search learnt the same lesson at fifteen
   * parameters.
   */
  @CheckAccess(entityType = EntityType.AD, action = ActionType.LIST)
  public AdListResponse listAds(
      String teamSlug, AdSearchParams params, @Nullable ListViewMode view, int page, int size) {
    Team team = teamService.getTeam(teamSlug);
    PedalonsPage<Ad> ads =
        adRepository.find(
            AdQuery.builder()
                .domainId(pedalonsContext.getDomainId())
                .userId(pedalonsContext.getUserIdNullable())
                .teamIds(Set.of(team.getId()))
                .search(params.search())
                .adType(params.adType())
                .from(params.from())
                .to(params.to())
                .minPrice(params.minPrice())
                .maxPrice(params.maxPrice())
                .nearLat(params.nearLat())
                .nearLon(params.nearLon())
                .nearRadius(params.nearRadius())
                .sortBy(params.sortBy())
                .sortDir(params.sortDir())
                .page(page)
                .size(size)
                .platformAdmin(isPlatformAdmin())
                .build());
    List<AdDto> dtos = ads.items().stream().map(ad -> AdDto.from(ad, assetService, view)).toList();
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

  /**
   * Relays a message to an ad's author without publishing anyone's address.
   *
   * <p>The alternative shape — a {@code contact} column rendered on the ad — is cheaper to build
   * and worse to live with: it hands an email address or a phone number to every member of the
   * team, permanently, and no later change of mind takes it back. On a 1999-member team that is an
   * irrevocable disclosure. Here the contract carries no contact field at all: the server knows
   * both addresses, the caller learns neither, and the author can switch the channel off.
   *
   * <p>{@link ActionType#READ} is the right gate and not a lax one — being able to write to an ad
   * is exactly being able to see it. Anything looser would let a non-member address a team's
   * sellers; anything stricter would stop members answering ads they can read.
   */
  @Logged
  @Transactional
  @CheckAccess(entityType = EntityType.AD, action = ActionType.READ)
  public void contactAuthor(String teamSlug, String adSlug, AdContactRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Ad ad = findBySlug(team, adSlug);
    User sender = pedalonsContext.getUser();
    User author = ad.getCreatedBy();

    if (author.getId().equals(sender.getId())) {
      throw new BusinessException(ErrorCode.AD_CONTACT_SELF);
    }
    if (!author.isContactableByMembers()) {
      throw new BusinessException(ErrorCode.AD_CONTACT_OPTED_OUT);
    }

    // Per sender, not per ad: a member who may read the classifieds may write to all of them, so a
    // per-ad cap caps nothing — three per ad across twenty ads is sixty relayed emails from one
    // account. What the threshold protects is the domain's sending reputation, and one account
    // consumes that whoever it writes to.
    long recent = adContactRepository.countRecentBySender(sender.getId(), contactWindowMinutes);
    if (recent >= contactMaxPerWindow) {
      Log.warnf(
          "Ad contact rate limit exceeded for user=%d ad=%d (%d in %d min)",
          sender.getId(), ad.getId(), recent, contactWindowMinutes);
      throw new TooManyRequestsException(
          ErrorCode.AD_CONTACT_RATE_LIMITED, Duration.ofMinutes(contactWindowMinutes).toSeconds());
    }

    adContactRepository.persist(new AdContact(sender, ad));

    try {
      adContactEmailService.sendContactMessage(ad, author, sender, request.message());
    } catch (RuntimeException e) {
      // Deliberately not swallowed, and the reason the persist above sits inside the transaction:
      // a relay that answers 204 and drops the message is worse than one that fails, because the
      // sender waits for a reply that was never going to come. Rolling back also means a failed
      // attempt neither counts against the quota nor leaves a row claiming a delivery.
      Log.errorf(e, "Ad contact delivery failed for ad=%d sender=%d", ad.getId(), sender.getId());
      throw new InternalException(ErrorCode.AD_CONTACT_DELIVERY_FAILED, e);
    }
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
