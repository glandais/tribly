package fr.pedalons.service.place;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.places.request.PlaceRequest;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.places.response.PlaceListResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class PlaceService {

  @Inject PlaceRepository placeRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.LIST)
  public PlaceListResponse listPlaces(
      String teamSlug, int page, int size, String search, boolean filterStart, boolean filterEnd) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    PedalonsPage<Place> places =
        placeRepository.findByTeam(teamId, page, size, search, filterStart, filterEnd);
    List<PlaceDetailDto> dtos = places.items().stream().map(PlaceDetailDto::from).toList();

    return new PlaceListResponse(dtos, places.total(), page, size);
  }

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.READ)
  public PlaceDetailDto getPlace(String teamSlug, String placeId) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), teamId)
            .orElseThrow(() -> new NotFoundException(EntityType.PLACE, placeId));

    return PlaceDetailDto.from(place);
  }

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.CREATE)
  @Transactional
  public PlaceDetailDto createPlace(String teamSlug, PlaceRequest request) {
    var team = teamService.getTeam(teamSlug);
    var user = pedalonsContext.getUser();
    Place place = new Place(user, team, request.name(), request.startPlace(), request.endPlace());
    updatePlaceFromRequest(place, request);

    placeRepository.persistAndFlush(place);

    return PlaceDetailDto.from(place);
  }

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.UPDATE)
  @Transactional
  public PlaceDetailDto updatePlace(String teamSlug, String placeId, PlaceRequest request) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), teamId)
            .orElseThrow(() -> new NotFoundException(EntityType.PLACE, placeId));

    updatePlaceFromRequest(place, request);
    placeRepository.persist(place);

    return PlaceDetailDto.from(place);
  }

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.DELETE)
  @Transactional
  public void deletePlace(String teamSlug, String placeId) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    Place place =
        placeRepository
            .findByIdAndTeam(TsidUtils.toLong(placeId), teamId)
            .orElseThrow(() -> new NotFoundException(EntityType.PLACE, placeId));

    placeRepository.delete(place);
  }

  private void updatePlaceFromRequest(Place place, PlaceRequest request) {
    place.setName(request.name());
    place.setAddress(request.address());
    place.setLink(request.link());
    place.setStartPlace(request.startPlace());
    place.setEndPlace(request.endPlace());
    place.setGeometry(request.geometry());
  }
}
