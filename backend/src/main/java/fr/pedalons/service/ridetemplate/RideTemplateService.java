package fr.pedalons.service.ridetemplate;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.ridetemplate.RideTemplateGroup;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.ridetemplates.request.RideTemplateGroupRequest;
import fr.pedalons.dto.ridetemplates.request.RideTemplateRequest;
import fr.pedalons.dto.ridetemplates.response.RideTemplateDto;
import fr.pedalons.dto.ridetemplates.response.RideTemplateListResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.ridetemplate.RideTemplateGroupRepository;
import fr.pedalons.repository.ridetemplate.RideTemplateRepository;
import fr.pedalons.service.common.SlugService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideTemplateService {

  @Inject RideTemplateRepository templateRepository;

  @Inject RideTemplateGroupRepository groupRepository;

  @Inject SlugService slugService;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.RIDE_TEMPLATE, action = ActionType.LIST)
  public RideTemplateListResponse listTemplates(
      String teamSlug, @Nullable String search, int page, int size) {
    Team team = teamService.getTeam(teamSlug);
    PedalonsPage<RideTemplate> templates =
        templateRepository.findByTeam(team.getId(), search, page, size);
    var dtos = templates.items().stream().map(RideTemplateDto::from).toList();
    return new RideTemplateListResponse(dtos, templates.total(), page, size);
  }

  @CheckAccess(entityType = EntityType.RIDE_TEMPLATE, action = ActionType.READ)
  public RideTemplateDto getTemplate(String teamSlug, String templateSlug) {
    Team team = teamService.getTeam(teamSlug);
    RideTemplate template =
        templateRepository
            .findByTeamAndSlug(team.getId(), templateSlug)
            .orElseThrow(() -> new NotFoundException(EntityType.RIDE_TEMPLATE, templateSlug));

    return RideTemplateDto.from(template);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE_TEMPLATE, action = ActionType.CREATE)
  public RideTemplateDto createTemplate(String teamSlug, RideTemplateRequest request) {
    Team team = teamService.getTeam(teamSlug);
    User creator = pedalonsContext.getUser();
    // Validate visibility: private teams can only have team-only templates
    Visibility visibility = request.visibility();
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw new BusinessException(ErrorCode.INVALID_VISIBILITY);
    }

    // Generate slug from name, ensure unique within team
    Long teamId = team.getId();
    String slug =
        slugService.generateSlug(
            request.name(), s -> templateRepository.existsByTeamAndSlug(teamId, s));

    RideTemplate template =
        new RideTemplate(
            creator, team, request.name(), slug, request.markdown(), visibility, request.status());

    templateRepository.persistAndFlush(template);

    int sortOrder = 0;
    for (RideTemplateGroupRequest groupRequest : request.groups()) {
      createTemplateGroup(creator, template, groupRequest, sortOrder);
      sortOrder++;
    }

    return RideTemplateDto.from(template);
  }

  private void createTemplateGroup(
      User user, RideTemplate template, RideTemplateGroupRequest groupRequest, int sortOrder) {
    RideTemplateGroup group = new RideTemplateGroup(user, template, groupRequest.name());
    group.setTime(groupRequest.time());
    group.setAverageSpeed(groupRequest.averageSpeed());
    group.setMaxParticipants(groupRequest.maxParticipants());
    group.setSortOrder(sortOrder);
    template.addGroup(group);
    groupRepository.persist(group);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE_TEMPLATE, action = ActionType.UPDATE)
  public RideTemplateDto updateTemplate(
      String teamSlug, String templateSlug, RideTemplateRequest request) {
    Team team = teamService.getTeam(teamSlug);
    User user = pedalonsContext.getUser();
    RideTemplate template =
        templateRepository
            .findByTeamAndSlug(team.getId(), templateSlug)
            .orElseThrow(() -> new NotFoundException(EntityType.RIDE_TEMPLATE, templateSlug));

    // Validate visibility: private teams can only have team-only templates
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw new BusinessException(ErrorCode.INVALID_VISIBILITY);
    }

    template.setName(request.name());
    template.setMarkdown(request.markdown());
    template.setVisibility(request.visibility());
    template.setStatus(request.status());

    // Update groups - clear and re-add existing or create new
    Map<Long, RideTemplateGroup> existingGroups =
        template.getGroups().stream()
            .collect(Collectors.toMap(RideTemplateGroup::getId, Function.identity()));

    template.getGroups().clear();

    int sortOrder = 0;
    for (RideTemplateGroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createTemplateGroup(user, template, groupRequest, sortOrder);
      } else {
        RideTemplateGroup existingGroup = existingGroups.remove(groupId);
        if (existingGroup != null) {
          existingGroup.setName(groupRequest.name());
          existingGroup.setTime(groupRequest.time());
          existingGroup.setAverageSpeed(groupRequest.averageSpeed());
          existingGroup.setMaxParticipants(groupRequest.maxParticipants());
          existingGroup.setSortOrder(sortOrder);
          template.getGroups().add(existingGroup);
        } else {
          throw new NotFoundException(EntityType.RIDE_TEMPLATE_GROUP, groupRequest.id());
        }
      }
      sortOrder++;
    }

    templateRepository.persist(template);

    return RideTemplateDto.from(template);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE_TEMPLATE, action = ActionType.DELETE)
  public void deleteTemplate(String teamSlug, String templateSlug) {
    Team team = teamService.getTeam(teamSlug);
    RideTemplate template =
        templateRepository
            .findByTeamAndSlug(team.getId(), templateSlug)
            .orElseThrow(() -> new NotFoundException(EntityType.RIDE_TEMPLATE, templateSlug));
    templateRepository.delete(template);
  }
}
