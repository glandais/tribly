package fr.pedalons.service.config;

import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.config.ConfigDto;
import fr.pedalons.dto.config.MapCenterDto;
import fr.pedalons.dto.config.MapStyleDto;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.ResolvedSite;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class ConfigService {

  /** Roughly a département / county — the useful scale for a site rooted on one team. */
  private static final double TEAM_CENTER_ZOOM = 9;

  @Inject DomainResolver domainResolver;

  @Inject TeamRepository teamRepository;

  @Inject MapConfig mapConfig;

  /**
   * Oldest mobile build this server still serves, blank when no floor is enforced.
   *
   * <p>Semantically a version rather than a map setting, but it rides on {@code ConfigDto} because
   * that is the one document the app reads at startup (D6).
   *
   * <p>{@code Optional} rather than {@code defaultValue = ""}: the property is declared as {@code
   * ${MIN_SUPPORTED_APP_VERSION:}}, so with no floor configured it resolves to the empty string —
   * which SmallRye hands to the converter as {@code null} and then rejects on a bare {@code String}
   * injection point (SRCFG00040), failing startup. Same reason the Strava settings are optional.
   */
  @ConfigProperty(name = "pedalons.mobile.min-supported-app-version")
  Optional<String> minSupportedAppVersion;

  @Public
  public ConfigDto getConfig() {
    ResolvedSite site = domainResolver.getResolvedSite();
    // Resolved once and read twice: the site's team gives both the pinned slug and the map centre,
    // so the cartography fields cost no query the endpoint was not already paying.
    Optional<Team> siteTeam = siteTeam(site);
    return new ConfigDto(
        site.effectiveHost(),
        site.effectiveName(),
        site.singleTeam(),
        // Read off the parent domain, not the alias: the flag governs the GPX tools, which are
        // team-independent and so belong to whoever owns the users.
        site.domain().isEnableGpxPlanner(),
        siteTeam.map(Team::getSlug).orElse(null),
        mapStyles(),
        mapConfig.tileServerBaseUrl(),
        defaultCenter(siteTeam.map(Team::getGeometry).orElse(null)),
        minSupportedAppVersion.filter(version -> !version.isBlank()).orElse(null));
  }

  private List<MapStyleDto> mapStyles() {
    return mapConfig.styles().stream()
        .map(
            style ->
                new MapStyleDto(
                    style.id(),
                    style.label(),
                    style.url(),
                    style.darkVariant().filter(url -> !url.isBlank()).orElse(null)))
        .toList();
  }

  /**
   * The team's own location when the site roots on a single team, the deployment default otherwise.
   *
   * <p>This is the only genuinely per-tenant map setting, and it is derived from a column that
   * already exists ({@code Team.geometry}), so no migration is involved. A per-<em>domain</em>
   * centre — for a multi-team domain covering one region — would need a Flyway migration adding
   * columns to {@code domains}; that has deliberately not been done here.
   */
  private MapCenterDto defaultCenter(@Nullable Point<G2D> teamGeometry) {
    MapConfig.DefaultCenter fallback = mapConfig.defaultCenter();
    if (teamGeometry == null || teamGeometry.isEmpty()) {
      return new MapCenterDto(fallback.lat(), fallback.lon(), fallback.zoom());
    }
    G2D position = teamGeometry.getPosition();
    // A site that knows its team knows its region: open closer than the country-wide fallback.
    return new MapCenterDto(position.getLat(), position.getLon(), TEAM_CENTER_ZOOM);
  }

  private Optional<Team> siteTeam(ResolvedSite site) {
    Long pinnedTeamId = site.pinnedTeamId();
    if (pinnedTeamId != null) {
      // Pinned alias: guard against a soft-deleted pinned team, fall back to no pinning.
      return teamRepository.findActiveById(pinnedTeamId);
    }
    if (site.singleTeam()) {
      // Native single-team domain: no explicit pin, but the site still roots on its one team, so
      // expose its slug too. The frontend treats both modes identically (clean team-rooted URLs).
      return teamRepository.findFirstByDomain(site.domain().getId());
    }
    return Optional.empty();
  }
}
