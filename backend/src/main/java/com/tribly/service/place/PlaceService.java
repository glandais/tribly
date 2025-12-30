package com.tribly.service.place;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PlaceService {

  private static final Logger LOG = Logger.getLogger(PlaceService.class);

  @Inject PlaceRepository placeRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  public PlaceListResponse listPlaces(String teamSlug, int page, int size, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    securityService.requireOrganizer(userId, team.getSlug());

    TriblyPage<Place> places = placeRepository.findByTeam(team.getId(), page, size);
    List<PlaceDetailDto> dtos = places.items().stream().map(PlaceDetailDto::from).toList();

    return new PlaceListResponse(dtos, places.total(), page, size);
  }

  public PlaceDetailDto getPlace(String teamSlug, String placeId, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    securityService.requireOrganizer(userId, team.getSlug());

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> BusinessException.notFound("Place", placeId));

    return PlaceDetailDto.from(place);
  }

  @Transactional
  public PlaceDetailDto createPlace(String teamSlug, PlaceRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security check: must be admin or organizer
    securityService.requireOrganizer(creatorId, team.getSlug());

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    Place place =
        new Place(creator, team, request.name(), request.startPlace(), request.endPlace());
    updatePlaceFromRequest(place, request);

    placeRepository.persistAndFlush(place);
    LOG.infov("Place '{0}' created by user {1} for team {2}", place.getName(), creatorId, teamSlug);

    return PlaceDetailDto.from(place);
  }

  @Transactional
  public PlaceDetailDto updatePlace(
      String teamSlug, String placeId, PlaceRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security check: must be admin or organizer
    securityService.requireOrganizer(userId, team.getSlug());

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> BusinessException.notFound("Place", placeId));

    updatePlaceFromRequest(place, request);
    placeRepository.persist(place);

    LOG.infov("Place {0} updated by user {1}", placeId, userId);
    return PlaceDetailDto.from(place);
  }

  @Transactional
  public void deletePlace(String teamSlug, String placeId, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security check: must be admin or organizer
    securityService.requireOrganizer(userId, team.getSlug());

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> BusinessException.notFound("Place", placeId));

    place.setDeleted(true);
    placeRepository.persist(place);
    LOG.infov("Place {0} deleted by user {1}", placeId, userId);
  }

  private void updatePlaceFromRequest(Place place, PlaceRequest request) {
    place.setName(request.name());
    place.setAddress(request.address());
    place.setLink(request.link());
    place.setStartPlace(request.startPlace());
    place.setEndPlace(request.endPlace());

    if (request.coordinates() != null && request.coordinates().length == 2) {
      // coordinates are [longitude, latitude]
      Point<G2D> point = point(WGS84, g(request.coordinates()[0], request.coordinates()[1]));
      place.setGeometry(point);
    } else {
      place.setGeometry(null);
    }
  }
}
