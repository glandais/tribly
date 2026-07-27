package fr.pedalons.service.common;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.common.TeamEntitySlugRedirect;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.TeamSlugRedirect;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.common.TeamEntitySlugRedirectRepository;
import fr.pedalons.repository.team.TeamSlugRedirectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ApplicationScoped
public class SlugService {
  private static final Pattern PATTERN_DIACRITICS =
      Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  private static final Pattern PATTERN_NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]+");
  private static final Pattern PATTERN_TRIM_DASH = Pattern.compile("^-|-$");
  private static final Pattern PATTERN_VALID_SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
  private static final int MAX_SLUG_LENGTH = 200;
  private static final String EMPTY = "";
  private static final String HYPHEN = "-";

  /**
   * Slugs a well-formed {@code newSlug} may never take, because some sibling resource declares that
   * literal segment ahead of the {@code /{entitySlug}} template it would otherwise collide with —
   * JAX-RS always matches the literal segment first, so an entity slugged that way becomes
   * unreachable by every route that goes through its slug rather than 404ing cleanly.
   *
   * <p>Scoped per {@link TeamEntityType} because the literal segments differ by resource:
   *
   * <ul>
   *   <li>{@link TeamEntityType#ROUTE} — {@code RouteResource}'s {@code /bulk}, {@code /count},
   *       {@code /bounds} and {@code /tiles/{z}/{x}/{y}.mvt}, all declared under {@code
   *       /api/teams/{teamSlug}/routes} alongside {@code /{routeSlug}}.
   *   <li>{@link TeamEntityType#TEAM_PAGE} — {@code TeamPageResource}'s {@code PUT /reorder},
   *       declared under {@code /api/teams/{teamSlug}/pages} alongside {@code PUT /{pageSlug}}.
   * </ul>
   *
   * <p>Every other {@link TeamEntityType} (ride, post, trip, ad, trip stage) has no such literal
   * sibling today and so has no entry here — add one the day a resource grows a literal path
   * segment under its slug template.
   */
  private static final Map<TeamEntityType, Set<String>> RESERVED_SLUGS = reservedSlugs();

  private static Map<TeamEntityType, Set<String>> reservedSlugs() {
    Map<TeamEntityType, Set<String>> reserved = new EnumMap<>(TeamEntityType.class);
    reserved.put(TeamEntityType.ROUTE, Set.of("bulk", "count", "bounds", "tiles"));
    reserved.put(TeamEntityType.TEAM_PAGE, Set.of("reorder"));
    return Map.copyOf(reserved);
  }

  @Inject TeamSlugRedirectRepository teamSlugRedirectRepository;

  @Inject TeamEntitySlugRedirectRepository teamEntitySlugRedirectRepository;

  public String generateSlug(String name, Long teamId, TeamEntityRepository<?, ?> repository) {
    TeamEntityType entityType = repository.getEntityType();
    String slug =
        generateSlug(
            name, s -> repository.existsByTeamAndSlug(teamId, s) || isReservedSlug(entityType, s));
    clearEntityRedirect(teamId, entityType, slug);
    return slug;
  }

  /**
   * For a uniqueness probe that has no {@link TeamEntityRepository} to hand — a plain repository
   * ({@link fr.pedalons.service.trip.TripService#stageSlug} for {@code TripStage}), or a candidate
   * that is not scoped by any {@link TeamEntityType} at all ({@link
   * fr.pedalons.service.team.TeamService}'s team slugs; {@code RideTemplate}, which is a plain
   * {@link fr.pedalons.domain.common.BaseEntity} rather than a {@link
   * fr.pedalons.domain.common.TeamEntity}, so it has no entry in {@link #RESERVED_SLUGS} to check).
   *
   * <p>This overload does <b>not</b> reject a reserved slug — only {@link #generateSlug(String,
   * Long, TeamEntityRepository)} and {@link #generateSlug(String, TeamEntityType, Function)} do. A
   * caller generating a slug for a {@link TeamEntityType} that is or later becomes a key of {@link
   * #RESERVED_SLUGS} must use {@link #generateSlug(String, TeamEntityType, Function)} instead, or
   * this check is silently skipped.
   */
  public String generateSlug(String name, Function<String, Boolean> checkExists) {
    String baseSlug = slugify(name);
    String slug = baseSlug;
    int i = 1;
    while (checkExists.apply(slug)) {
      slug = baseSlug + "-" + i;
      i++;
    }
    return slug;
  }

  /**
   * Same as {@link #generateSlug(String, Function)}, but also rejects a candidate reserved for
   * {@code entityType} — see {@link #RESERVED_SLUGS}. For a {@link TeamEntityType}-scoped
   * uniqueness probe whose {@link TeamEntityRepository} is out of reach, so it is not stuck
   * choosing between {@link #generateSlug(String, Long, TeamEntityRepository)} and silently
   * skipping the reserved-slug check that overload gets for free.
   */
  public String generateSlug(
      String name, TeamEntityType entityType, Function<String, Boolean> checkExists) {
    return generateSlug(name, s -> checkExists.apply(s) || isReservedSlug(entityType, s));
  }

  public static String slugify(String name) {
    return Optional.of(name)
        // remove leading and trailing whitespaces
        .map(String::trim)
        // run subsequent calls only if string is not empty
        .filter(Predicate.not(EMPTY::equals))
        .map(SlugService::prepare)
        .map(str -> PATTERN_NON_ALPHANUMERIC.matcher(str).replaceAll(HYPHEN))
        .map(str -> PATTERN_TRIM_DASH.matcher(str).replaceAll(EMPTY))
        // convert to lower case if needed
        .map(String::toLowerCase)
        // return empty string if input is null or empty
        .orElse(EMPTY);
  }

  private static String prepare(final String input) {
    // Normalize to NFKD (compatibility decomposition) to handle ligatures and diacritics
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFKD);
    // Remove diacritical marks (accents, etc.)
    String ascii = PATTERN_DIACRITICS.matcher(normalized).replaceAll(EMPTY);
    // Replace non-alphanumeric chars with hyphen
    String hyphened = PATTERN_NON_ALPHANUMERIC.matcher(ascii).replaceAll(HYPHEN);
    // Remove leading and trailing dashes
    return PATTERN_TRIM_DASH.matcher(hyphened).replaceAll(EMPTY);
  }

  public boolean isValidSlug(String slug) {
    return !slug.isEmpty()
        && slug.length() <= MAX_SLUG_LENGTH
        && PATTERN_VALID_SLUG.matcher(slug).matches();
  }

  /**
   * Whether {@code slug} is reserved for {@code entityType} — see {@link #RESERVED_SLUGS}. A
   * reserved slug is well-formed ({@link #isValidSlug} would accept it); it is unavailable for the
   * same practical reason a taken one is: a route already answers that URL. Callers that reject a
   * reserved slug on write should therefore use the same "slug taken" error shape as a genuine
   * uniqueness conflict, not the "invalid format" one.
   *
   * <p>This check is prevention-only, applied on write. It does <b>not</b> make an existing row
   * that already carries a reserved slug (grandfathered in before this check existed) "keep
   * reading fine" — the opposite: such a row would be permanently unreachable by its slug, because
   * JAX-RS always matches the literal path segment first. A pre-existing {@code
   * routes(team_id=T, slug='bulk')} means {@code GET /api/teams/{T}/routes/bulk} (and every
   * sibling literal — {@code count}, {@code bounds}, {@code tiles}) always matches {@code
   * RouteResource}'s {@code @Path("/bulk")} and never resolves that row; it can only be reached by
   * id or by a redirect created after a later rename. This method does not query the database, so
   * it cannot tell you whether such a row exists today — see the "known remaining work" note in
   * {@code docs/NEXT.md} for the backfill this would need if one is ever found.
   */
  public boolean isReservedSlug(TeamEntityType entityType, String slug) {
    return RESERVED_SLUGS.getOrDefault(entityType, Set.of()).contains(slug);
  }

  // ========== Team Redirects ==========

  @Transactional
  public void createTeamRedirect(Team team, String oldSlug) {
    TeamSlugRedirect redirect = new TeamSlugRedirect(oldSlug, team);
    teamSlugRedirectRepository.persist(redirect);
  }

  public Optional<TeamSlugRedirect> resolveTeamRedirect(Long domainId, String oldSlug) {
    return teamSlugRedirectRepository.findByOldSlugAndDomain(domainId, oldSlug);
  }

  @Transactional
  public void clearTeamRedirect(Long domainId, String oldSlug) {
    teamSlugRedirectRepository.deleteByOldSlugAndDomain(domainId, oldSlug);
  }

  // ========== TeamEntity Redirects ==========

  @Transactional
  public void createEntityRedirect(TeamEntity entity, String oldSlug) {
    TeamEntityType entityType = TeamEntityType.fromEntity(entity);
    TeamEntitySlugRedirect redirect =
        new TeamEntitySlugRedirect(
            oldSlug, entity.getTeam(), entityType.getValue(), entity.getId());
    teamEntitySlugRedirectRepository.persist(redirect);
  }

  public Optional<TeamEntitySlugRedirect> resolveEntityRedirect(
      Long teamId, TeamEntityType entityType, String oldSlug) {
    return teamEntitySlugRedirectRepository.findByTeamAndEntityTypeAndOldSlug(
        teamId, entityType.getValue(), oldSlug);
  }

  @Transactional
  public void clearEntityRedirect(Long teamId, TeamEntityType entityType, String oldSlug) {
    teamEntitySlugRedirectRepository.deleteByTeamAndEntityTypeAndOldSlug(
        teamId, entityType.getValue(), oldSlug);
  }
}
