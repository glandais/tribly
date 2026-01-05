package com.tribly.service.common;

import com.ibm.icu.text.Transliterator;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.TeamEntitySlugRedirect;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.common.repository.TeamEntitySlugRedirectRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamSlugRedirect;
import com.tribly.domain.team.repository.TeamSlugRedirectRepository;
import com.tribly.enums.EntityType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ApplicationScoped
public class SlugService {
  private static final String ASCII =
      "Cyrillic-Latin; Any-Latin; Latin-ASCII; [^\\p{Print}] Remove";
  private static final Pattern PATTERN_NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]+");
  private static final Pattern PATTERN_TRIM_DASH = Pattern.compile("^-|-$");
  private static final Pattern PATTERN_VALID_SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
  private static final int MAX_SLUG_LENGTH = 200;
  private static final String EMPTY = "";
  private static final String HYPHEN = "-";

  @Inject TeamSlugRedirectRepository teamSlugRedirectRepository;

  @Inject TeamEntitySlugRedirectRepository teamEntitySlugRedirectRepository;

  public String generateSlug(String name, Long teamId, TeamEntityRepository<?, ?> repository) {
    String slug = generateSlug(name, s -> repository.existsByTeamAndSlug(teamId, s));
    clearEntityRedirect(teamId, repository.getEntityType(), slug);
    return slug;
  }

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
    // transliterate or normalize
    String transliterate = Transliterator.getInstance(ASCII).transliterate(input);
    // replace non-alphanumeric chars with hyphen
    String hyphened = PATTERN_NON_ALPHANUMERIC.matcher(transliterate).replaceAll(HYPHEN);
    // remove leading and trailing dashes
    return PATTERN_TRIM_DASH.matcher(hyphened).replaceAll(EMPTY);
  }

  public boolean isValidSlug(String slug) {
    return !slug.isEmpty()
        && slug.length() <= MAX_SLUG_LENGTH
        && PATTERN_VALID_SLUG.matcher(slug).matches();
  }

  // ========== Team Redirects ==========

  @Transactional
  public void createTeamRedirect(Team team, String oldSlug) {
    TeamSlugRedirect redirect = new TeamSlugRedirect(oldSlug, team);
    teamSlugRedirectRepository.persist(redirect);
  }

  public Optional<TeamSlugRedirect> resolveTeamRedirect(String oldSlug) {
    return teamSlugRedirectRepository.findByOldSlug(oldSlug);
  }

  @Transactional
  public void clearTeamRedirect(String oldSlug) {
    teamSlugRedirectRepository.deleteByOldSlug(oldSlug);
  }

  // ========== TeamEntity Redirects ==========

  @Transactional
  public void createEntityRedirect(TeamEntity entity, String oldSlug) {
    EntityType entityType = EntityType.fromEntity(entity);
    TeamEntitySlugRedirect redirect =
        new TeamEntitySlugRedirect(
            oldSlug, entity.getTeam(), entityType.getValue(), entity.getId());
    teamEntitySlugRedirectRepository.persist(redirect);
  }

  public Optional<TeamEntitySlugRedirect> resolveEntityRedirect(
      Long teamId, EntityType entityType, String oldSlug) {
    return teamEntitySlugRedirectRepository.findByTeamAndEntityTypeAndOldSlug(
        teamId, entityType.getValue(), oldSlug);
  }

  @Transactional
  public void clearEntityRedirect(Long teamId, EntityType entityType, String oldSlug) {
    teamEntitySlugRedirectRepository.deleteByTeamAndEntityTypeAndOldSlug(
        teamId, entityType.getValue(), oldSlug);
  }
}
