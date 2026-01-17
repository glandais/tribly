package com.tribly.service.ridetemplate;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.TriblyException;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.ridetemplates.request.RideTemplateGroupRequest;
import com.tribly.dto.ridetemplates.request.RideTemplateRequest;
import com.tribly.dto.ridetemplates.response.RideTemplateDto;
import com.tribly.dto.ridetemplates.response.RideTemplateListResponse;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideTemplateServiceTest {

  @Inject RideTemplateService templateService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private Team privateTeam;
  private User admin;
  private User organizer;
  private User member;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
  }

  @Nested
  class ListTemplates {

    @Test
    void shouldListTemplatesForOrganizer() {
      dataService.createRideTemplate(team, admin, "Template 1", "template-1");
      dataService.createRideTemplate(team, admin, "Template 2", "template-2");

      queryContext.setUserForTest(organizer);
      RideTemplateListResponse result = templateService.listTemplates(team.getSlug(), null, 0, 10);

      assertEquals(2, result.templates().size());
      assertEquals(2, result.total());
    }

    @Test
    void shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createRideTemplate(team, admin, "Template " + i, "template-" + i);
      }

      queryContext.setUserForTest(organizer);
      RideTemplateListResponse result = templateService.listTemplates(team.getSlug(), null, 0, 3);

      assertEquals(3, result.templates().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldFilterBySearch() {
      dataService.createRideTemplate(team, admin, "Morning Ride", "morning-ride");
      dataService.createRideTemplate(team, admin, "Evening Ride", "evening-ride");
      dataService.createRideTemplate(team, admin, "Weekend Trip", "weekend-trip");

      queryContext.setUserForTest(organizer);
      RideTemplateListResponse result =
          templateService.listTemplates(team.getSlug(), "ride", 0, 10);

      assertEquals(2, result.templates().size());
    }

    @Test
    void shouldThrowForMember() {
      queryContext.setUserForTest(member);
      assertThrows(
          TriblyException.class, () -> templateService.listTemplates(team.getSlug(), null, 0, 10));
    }

    @Test
    void shouldExcludeDeletedTemplates() {
      dataService.createRideTemplate(team, admin, "Active Template", "active-template");
      RideTemplate deletedTemplate =
          dataService.createRideTemplate(team, admin, "Deleted Template", "deleted-template");
      dataService.deleteRideTemplate(deletedTemplate);

      queryContext.setUserForTest(organizer);
      RideTemplateListResponse result = templateService.listTemplates(team.getSlug(), null, 0, 10);

      assertEquals(1, result.templates().size());
      assertEquals("Active Template", result.templates().getFirst().name());
    }
  }

  @Nested
  class GetTemplate {

    @Test
    void shouldReturnTemplateForOrganizer() {
      dataService.createRideTemplate(team, admin, "Test Template", "test-template");

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.getTemplate(team.getSlug(), "test-template");

      assertNotNull(result);
      assertEquals("Test Template", result.name());
      assertEquals("test-template", result.slug());
    }

    @Test
    void shouldThrowForNonexistentTemplate() {
      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class, () -> templateService.getTemplate(team.getSlug(), "nonexistent"));
    }

    @Test
    void shouldThrowForMember() {
      dataService.createRideTemplate(team, admin, "Test Template", "test-template");

      queryContext.setUserForTest(member);
      assertThrows(
          TriblyException.class,
          () -> templateService.getTemplate(team.getSlug(), "test-template"));
    }
  }

  @Nested
  class CreateTemplate {

    @Test
    void shouldCreateTemplateForOrganizer() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "New Template",
              "Description in **markdown**",
              Visibility.TEAM,
              Status.PUBLISHED,
              List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.createTemplate(team.getSlug(), request);

      assertNotNull(result);
      assertEquals("New Template", result.name());
      assertEquals("new-template", result.slug());
      assertEquals("Description in **markdown**", result.markdown());
      assertEquals(Visibility.TEAM, result.visibility());
      assertEquals(Status.PUBLISHED, result.status());
    }

    @Test
    void shouldCreateTemplateWithGroups() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template with Groups",
              "markdown",
              Visibility.TEAM,
              Status.PUBLISHED,
              List.of(
                  RideTemplateGroupRequest.builder()
                      .name("Group A")
                      .averageSpeed(25.0f)
                      .maxParticipants(20)
                      .build(),
                  RideTemplateGroupRequest.builder()
                      .name("Group B")
                      .averageSpeed(30.0f)
                      .maxParticipants(15)
                      .build()));

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.createTemplate(team.getSlug(), request);

      assertNotNull(result);
      assertEquals(2, result.groups().size());
      assertEquals("Group A", result.groups().get(0).name());
      assertEquals(25, result.groups().get(0).averageSpeed());
      assertEquals(20, result.groups().get(0).maxParticipants());
      assertEquals("Group B", result.groups().get(1).name());
    }

    @Test
    void shouldGenerateUniqueSlug() {
      dataService.createRideTemplate(team, admin, "Test Template", "test-template");

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Test Template", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.createTemplate(team.getSlug(), request);

      assertNotNull(result);
      assertNotEquals("test-template", result.slug());
      assertTrue(result.slug().startsWith("test-template"));
    }

    @Test
    void shouldCreatePublicTemplateOnPublicTeam() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Public Template", "markdown", Visibility.PUBLIC, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.createTemplate(team.getSlug(), request);

      assertNotNull(result);
      assertEquals(Visibility.PUBLIC, result.visibility());
    }

    @Test
    void shouldThrowForPublicTemplateOnPrivateTeam() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Public Template", "markdown", Visibility.PUBLIC, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class,
          () -> templateService.createTemplate(privateTeam.getSlug(), request));
    }

    @Test
    void shouldAllowTeamVisibilityOnPrivateTeam() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Team Template", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result = templateService.createTemplate(privateTeam.getSlug(), request);

      assertNotNull(result);
      assertEquals(Visibility.TEAM, result.visibility());
    }

    @Test
    void shouldThrowForMember() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(member);
      assertThrows(
          TriblyException.class, () -> templateService.createTemplate(team.getSlug(), request));
    }
  }

  @Nested
  class UpdateTemplate {

    @Test
    void shouldUpdateAllFields() {
      dataService.createRideTemplate(
          team, admin, "Original", "original-slug", Visibility.TEAM, Status.DRAFT);

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Updated Name", "New description", Visibility.PUBLIC, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result =
          templateService.updateTemplate(team.getSlug(), "original-slug", request);

      assertEquals("Updated Name", result.name());
      assertEquals("New description", result.markdown());
      assertEquals(Visibility.PUBLIC, result.visibility());
      assertEquals(Status.PUBLISHED, result.status());
    }

    @Test
    void shouldAddNewGroups() {
      dataService.createRideTemplate(team, admin, "Template", "template-slug");

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template",
              "markdown",
              Visibility.TEAM,
              Status.PUBLISHED,
              List.of(
                  RideTemplateGroupRequest.builder()
                      .name("New Group")
                      .averageSpeed(28.0f)
                      .maxParticipants(12)
                      .build()));

      queryContext.setUserForTest(organizer);
      RideTemplateDto result =
          templateService.updateTemplate(team.getSlug(), "template-slug", request);

      assertEquals(1, result.groups().size());
      assertEquals("New Group", result.groups().getFirst().name());
      assertEquals(28, result.groups().getFirst().averageSpeed());
    }

    @Test
    void shouldUpdateExistingGroups() {
      RideTemplate template =
          dataService.createRideTemplate(team, admin, "Template", "template-slug");
      var group = dataService.createRideTemplateGroup(admin, template, "Original Group", 25, 20);
      String groupId = TsidUtils.toString(group.getId());

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template",
              "Template",
              Visibility.TEAM,
              Status.PUBLISHED,
              List.of(
                  RideTemplateGroupRequest.builder()
                      .id(groupId)
                      .name("Updated Group")
                      .averageSpeed(30.0f)
                      .maxParticipants(15)
                      .build()));

      queryContext.setUserForTest(organizer);
      RideTemplateDto result =
          templateService.updateTemplate(team.getSlug(), "template-slug", request);

      assertEquals(1, result.groups().size());
      assertEquals("Updated Group", result.groups().getFirst().name());
      assertEquals(30, result.groups().getFirst().averageSpeed());
      assertEquals(15, result.groups().getFirst().maxParticipants());
    }

    @Test
    void shouldRemoveGroupsNotInRequest() {
      RideTemplate template =
          dataService.createRideTemplate(team, admin, "Template", "template-slug");
      dataService.createRideTemplateGroup(admin, template, "Group to Remove", 25, 20);

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      RideTemplateDto result =
          templateService.updateTemplate(team.getSlug(), "template-slug", request);

      assertEquals(0, result.groups().size());
    }

    @Test
    void shouldThrowForNonexistentGroup() {
      dataService.createRideTemplate(team, admin, "Template", "template-slug");

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template",
              "markdown",
              Visibility.TEAM,
              Status.PUBLISHED,
              List.of(
                  RideTemplateGroupRequest.builder()
                      .id(TsidUtils.toString(999999L))
                      .name("Nonexistent Group")
                      .build()));

      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class,
          () -> templateService.updateTemplate(team.getSlug(), "template-slug", request));
    }

    @Test
    void shouldThrowForPublicVisibilityOnPrivateTeam() {
      dataService.createRideTemplate(
          privateTeam,
          admin,
          "Private Template",
          "private-template",
          Visibility.TEAM,
          Status.PUBLISHED);

      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template", "markdown", Visibility.PUBLIC, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class,
          () -> templateService.updateTemplate(privateTeam.getSlug(), "private-template", request));
    }

    @Test
    void shouldThrowForNonexistentTemplate() {
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Template", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class,
          () -> templateService.updateTemplate(team.getSlug(), "nonexistent", request));
    }

    @Test
    void shouldThrowForMember() {
      dataService.createRideTemplate(team, admin, "Template", "template-slug");
      RideTemplateRequest request =
          new RideTemplateRequest(
              "Updated", "markdown", Visibility.TEAM, Status.PUBLISHED, List.of());

      queryContext.setUserForTest(member);
      assertThrows(
          TriblyException.class,
          () -> templateService.updateTemplate(team.getSlug(), "template-slug", request));
    }
  }

  @Nested
  class DeleteTemplate {

    @Test
    void shouldSoftDeleteTemplate() {
      dataService.createRideTemplate(team, admin, "To Delete", "to-delete");

      queryContext.setUserForTest(organizer);
      templateService.deleteTemplate(team.getSlug(), "to-delete");

      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class, () -> templateService.getTemplate(team.getSlug(), "to-delete"));
    }

    @Test
    void shouldThrowForNonexistentTemplate() {
      queryContext.setUserForTest(organizer);
      assertThrows(
          TriblyException.class,
          () -> templateService.deleteTemplate(team.getSlug(), "nonexistent"));
    }

    @Test
    void shouldThrowForMember() {
      dataService.createRideTemplate(team, admin, "Template", "template-slug");

      queryContext.setUserForTest(member);
      assertThrows(
          TriblyException.class,
          () -> templateService.deleteTemplate(team.getSlug(), "template-slug"));
    }
  }
}
