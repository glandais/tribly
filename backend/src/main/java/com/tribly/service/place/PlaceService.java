package com.tribly.service.place;

import com.tribly.common.TsidUtils;
import com.tribly.domain.place.Place;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.repository.common.TriblyPage;
import com.tribly.repository.place.PlaceRepository;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class PlaceService {

  @Inject PlaceRepository placeRepository;

  @Inject TriblyQueryContext triblyContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.PLACE, action = ActionType.LIST)
  public PlaceListResponse listPlaces(String teamSlug, int page, int size) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    TriblyPage<Place> places = placeRepository.findByTeam(teamId, page, size);
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
    var user = triblyContext.getUser();
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

    place.setDeleted(true);
    placeRepository.persist(place);
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
