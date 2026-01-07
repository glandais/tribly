package com.tribly.repository.ridetemplate;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.domain.ridetemplate.RideTemplateGroup;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideTemplateGroupRepositoryTest {

  @Inject RideTemplateGroupRepository groupRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Team team;
  private RideTemplate template;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    template = dataService.createRideTemplate(team, user, "Test Template", "test-template");
  }

  @Nested
  @DisplayName("findByIdAndTemplate")
  class FindByIdAndTemplate {

    @Test
    @DisplayName("Should return group when id and template match")
    void findByIdAndTemplate_shouldReturnGroupWhenMatches() {
      RideTemplateGroup group = dataService.createRideTemplateGroup(user, template, "Group A");

      Optional<RideTemplateGroup> result =
          groupRepository.findByIdAndTemplate(group.getId(), template.getId());

      assertTrue(result.isPresent());
      assertEquals(group.getId(), result.get().getId());
      assertEquals("Group A", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when id doesn't exist")
    void findByIdAndTemplate_shouldReturnEmptyWhenIdNotExists() {
      dataService.createRideTemplateGroup(user, template, "Group A");

      Optional<RideTemplateGroup> result =
          groupRepository.findByIdAndTemplate(999999L, template.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when template doesn't match")
    void findByIdAndTemplate_shouldReturnEmptyWhenTemplateNotMatches() {
      RideTemplateGroup group = dataService.createRideTemplateGroup(user, template, "Group A");
      RideTemplate otherTemplate =
          dataService.createRideTemplate(team, user, "Other Template", "other-template");

      Optional<RideTemplateGroup> result =
          groupRepository.findByIdAndTemplate(group.getId(), otherTemplate.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when group is deleted")
    void findByIdAndTemplate_shouldReturnEmptyWhenDeleted() {
      RideTemplateGroup group = dataService.createRideTemplateGroup(user, template, "Group A");
      dataService.deleteRideTemplateGroup(group);

      Optional<RideTemplateGroup> result =
          groupRepository.findByIdAndTemplate(group.getId(), template.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should distinguish between groups in different templates")
    void findByIdAndTemplate_shouldDistinguishBetweenTemplates() {
      RideTemplateGroup group1 =
          dataService.createRideTemplateGroup(user, template, "Group in Template 1");
      RideTemplate otherTemplate =
          dataService.createRideTemplate(team, user, "Other Template", "other-template");
      RideTemplateGroup group2 =
          dataService.createRideTemplateGroup(user, otherTemplate, "Group in Template 2");

      Optional<RideTemplateGroup> result1 =
          groupRepository.findByIdAndTemplate(group1.getId(), template.getId());
      Optional<RideTemplateGroup> result2 =
          groupRepository.findByIdAndTemplate(group2.getId(), otherTemplate.getId());
      Optional<RideTemplateGroup> wrongTemplate =
          groupRepository.findByIdAndTemplate(group1.getId(), otherTemplate.getId());

      assertTrue(result1.isPresent());
      assertTrue(result2.isPresent());
      assertTrue(wrongTemplate.isEmpty());
      assertEquals("Group in Template 1", result1.get().getName());
      assertEquals("Group in Template 2", result2.get().getName());
    }
  }

  @Nested
  @DisplayName("RideTemplateGroup Entity Properties")
  class RideTemplateGroupEntityProperties {

    @Test
    @DisplayName("Should create group with name")
    void createGroup_shouldHaveName() {
      RideTemplateGroup group = dataService.createRideTemplateGroup(user, template, "Fast Group");

      assertEquals("Fast Group", group.getName());
      assertNull(group.getAverageSpeed());
      assertNull(group.getMaxParticipants());
    }

    @Test
    @DisplayName("Should create group with all properties")
    void createGroup_shouldAllowAllProperties() {
      RideTemplateGroup group =
          dataService.createRideTemplateGroup(user, template, "Fast Group", 30, 15);

      assertEquals("Fast Group", group.getName());
      assertEquals(30, group.getAverageSpeed());
      assertEquals(15, group.getMaxParticipants());
    }

    @Test
    @DisplayName("Should preserve group template association")
    void createGroup_shouldPreserveTemplateAssociation() {
      RideTemplateGroup group = dataService.createRideTemplateGroup(user, template, "Group");

      Optional<RideTemplateGroup> found =
          groupRepository.findByIdAndTemplate(group.getId(), template.getId());

      assertTrue(found.isPresent());
      assertEquals(template.getId(), found.get().getTemplate().getId());
    }

    @Test
    @DisplayName("Should create multiple groups for same template")
    void createGroup_shouldAllowMultipleGroupsPerTemplate() {
      RideTemplateGroup group1 =
          dataService.createRideTemplateGroup(user, template, "Group A", 25, 20);
      RideTemplateGroup group2 =
          dataService.createRideTemplateGroup(user, template, "Group B", 30, 15);
      RideTemplateGroup group3 =
          dataService.createRideTemplateGroup(user, template, "Group C", 35, 10);

      Optional<RideTemplateGroup> found1 =
          groupRepository.findByIdAndTemplate(group1.getId(), template.getId());
      Optional<RideTemplateGroup> found2 =
          groupRepository.findByIdAndTemplate(group2.getId(), template.getId());
      Optional<RideTemplateGroup> found3 =
          groupRepository.findByIdAndTemplate(group3.getId(), template.getId());

      assertTrue(found1.isPresent());
      assertTrue(found2.isPresent());
      assertTrue(found3.isPresent());
      assertEquals("Group A", found1.get().getName());
      assertEquals("Group B", found2.get().getName());
      assertEquals("Group C", found3.get().getName());
    }
  }
}
