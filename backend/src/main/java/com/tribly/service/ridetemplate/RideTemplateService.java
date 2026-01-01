package com.tribly.service.ridetemplate;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.domain.ridetemplate.RideTemplateGroup;
import com.tribly.domain.ridetemplate.repository.RideTemplateGroupRepository;
import com.tribly.domain.ridetemplate.repository.RideTemplateRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.ridetemplates.request.RideTemplateGroupRequest;
import com.tribly.dto.ridetemplates.request.RideTemplateRequest;
import com.tribly.dto.ridetemplates.response.RideTemplateDto;
import com.tribly.dto.ridetemplates.response.RideTemplateListResponse;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.SlugService;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideTemplateService {

  private static final Logger LOG = Logger.getLogger(RideTemplateService.class);

  @Inject RideTemplateRepository templateRepository;

  @Inject RideTemplateGroupRepository groupRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  @Inject SlugService slugService;

  public RideTemplateListResponse listTemplates(
      String teamSlug, Long userId, @Nullable String search, int page, int size) {
    // Any team member can list templates
    securityService.requireOrganizer(userId, teamSlug);

    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    TriblyPage<RideTemplate> templates =
        templateRepository.findByTeam(team.getId(), search, page, size);
    var dtos = templates.items().stream().map(RideTemplateDto::from).toList();
    return new RideTemplateListResponse(dtos, templates.total(), page, size);
  }

  public RideTemplateDto getTemplate(String teamSlug, String templateSlug, Long userId) {
    // Any team member can view templates
    securityService.requireOrganizer(userId, teamSlug);

    RideTemplate template =
        templateRepository
            .findByTeamSlugAndTemplateSlug(teamSlug, templateSlug)
            .orElseThrow(() -> BusinessException.notFound("Template", templateSlug));

    return RideTemplateDto.from(template);
  }

  @Transactional
  public RideTemplateDto createTemplate(
      String teamSlug, RideTemplateRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: must be admin or organizer to create templates
    securityService.requireOrganizer(creatorId, team.getSlug());

    // Validate visibility: private teams can only have team-only templates
    Visibility visibility = request.visibility();
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only templates");
    }

    // Generate slug from name, ensure unique within team
    String slug =
        slugService.generateSlug(
            request.name(), s -> templateRepository.existsByTeamAndSlug(team.getId(), s));

    RideTemplate template = new RideTemplate(creator, team, request.name(), slug);
    template.setMarkdown(request.markdown());
    template.setVisibility(visibility);
    template.setStatus(request.status());

    templateRepository.persistAndFlush(template);

    int sortOrder = 0;
    for (RideTemplateGroupRequest groupRequest : request.groups()) {
      createTemplateGroup(creator, template, groupRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov(
        "Template '{0}' created by user {1} for team {2}", template.getName(), creatorId, teamSlug);
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
  public RideTemplateDto updateTemplate(
      String teamSlug, String templateSlug, RideTemplateRequest request, Long userId) {
    RideTemplate template =
        templateRepository
            .findByTeamSlugAndTemplateSlug(teamSlug, templateSlug)
            .orElseThrow(() -> BusinessException.notFound("Template", templateSlug));

    // Security check: must be admin or organizer to update templates
    securityService.requireOrganizer(userId, teamSlug);

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Validate visibility: private teams can only have team-only templates
    Team team = template.getTeam();
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only templates");
    }

    template.setName(request.name());
    template.setMarkdown(request.markdown());
    template.setVisibility(request.visibility());
    template.setStatus(request.status());

    // Update groups - mark all as deleted first, then restore/create
    Map<Long, RideTemplateGroup> existingGroups =
        template.getGroups().stream()
            .collect(Collectors.toMap(RideTemplateGroup::getId, Function.identity()));

    for (RideTemplateGroup group : template.getGroups()) {
      group.setDeleted(true);
      group.setSortOrder(0);
    }

    int sortOrder = 0;
    for (RideTemplateGroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createTemplateGroup(user, template, groupRequest, sortOrder);
      } else {
        RideTemplateGroup existingGroup = existingGroups.remove(groupId);
        if (existingGroup != null) {
          existingGroup.setDeleted(false);
          existingGroup.setName(groupRequest.name());
          existingGroup.setTime(groupRequest.time());
          existingGroup.setAverageSpeed(groupRequest.averageSpeed());
          existingGroup.setMaxParticipants(groupRequest.maxParticipants());
          existingGroup.setSortOrder(sortOrder);
        } else {
          throw BusinessException.notFound("Group", groupRequest.id());
        }
      }
      sortOrder++;
    }

    templateRepository.persist(template);

    LOG.infov("Template {0} updated by user {1}", templateSlug, userId);
    return RideTemplateDto.from(template);
  }

  @Transactional
  public void deleteTemplate(String teamSlug, String templateSlug, Long userId) {
    RideTemplate template =
        templateRepository
            .findByTeamSlugAndTemplateSlug(teamSlug, templateSlug)
            .orElseThrow(() -> BusinessException.notFound("Template", templateSlug));

    // Security check: must be admin or organizer to delete templates
    securityService.requireOrganizer(userId, teamSlug);

    template.setDeleted(true);
    templateRepository.persist(template);
    LOG.infov("Template {0} deleted by user {1}", templateSlug, userId);
  }
}
