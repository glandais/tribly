package com.tribly.service.place;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.enums.AllEntityType;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PlaceService {

  private static final Logger LOG = Logger.getLogger(PlaceService.class);

  @Inject PlaceRepository placeRepository;

  @Inject TeamSecurityService securityService;

  public PlaceListResponse listPlaces(Team team, int page, int size, User user) {
    securityService.requireOrganizer(user, team);

    TriblyPage<Place> places = placeRepository.findByTeam(team.getId(), page, size);
    List<PlaceDetailDto> dtos = places.items().stream().map(PlaceDetailDto::from).toList();

    return new PlaceListResponse(dtos, places.total(), page, size);
  }

  public PlaceDetailDto getPlace(Team team, String placeId, @Nullable User user) {
    securityService.requireOrganizer(user, team);

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.PLACE, placeId));

    return PlaceDetailDto.from(place);
  }

  @Transactional
  public PlaceDetailDto createPlace(Team team, PlaceRequest request, User creator) {
    // Security check: must be admin or organizer
    securityService.requireOrganizer(creator, team);

    Place place =
        new Place(creator, team, request.name(), request.startPlace(), request.endPlace());
    updatePlaceFromRequest(place, request);

    placeRepository.persistAndFlush(place);
    LOG.infov(
        "Place '{0}' created by user {1} for team {2}",
        place.getName(), creator.getId(), team.getSlug());

    return PlaceDetailDto.from(place);
  }

  @Transactional
  public PlaceDetailDto updatePlace(Team team, String placeId, PlaceRequest request, User user) {
    // Security check: must be admin or organizer
    securityService.requireOrganizer(user, team);

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.PLACE, placeId));

    updatePlaceFromRequest(place, request);
    placeRepository.persist(place);

    LOG.infov("Place {0} updated by user {1}", placeId, user.getId());
    return PlaceDetailDto.from(place);
  }

  @Transactional
  public void deletePlace(Team team, String placeId, User user) {
    // Security check: must be admin or organizer
    securityService.requireOrganizer(user, team);

    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), team.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.PLACE, placeId));

    place.setDeleted(true);
    placeRepository.persist(place);
    LOG.infov("Place {0} deleted by user {1}", placeId, user.getId());
  }

  private void updatePlaceFromRequest(Place place, PlaceRequest request) {
    place.setName(request.name());
    place.setAddress(request.address());
    place.setLink(request.link());
    place.setStartPlace(request.startPlace());
    place.setEndPlace(request.endPlace());
    place.setGeometry(request.coordinates());
  }
}
