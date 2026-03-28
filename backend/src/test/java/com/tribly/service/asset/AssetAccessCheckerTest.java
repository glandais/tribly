package com.tribly.service.asset;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.AssetType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssetAccessCheckerTest extends AbstractBaseTest {

  @Inject AssetAccessChecker assetAccessChecker;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext userService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Test
  void getType_shouldReturnAsset() {
    assertEquals(EntityType.ASSET, assetAccessChecker.getType());
  }

  @Nested
  class CreateAction {

    @Test
    void shouldAllowForMember() {
      userService.setUserForTest(member);
      boolean result = assetAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForOrganizer() {
      userService.setUserForTest(organizer);
      boolean result = assetAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldAllowForAdmin() {
      userService.setUserForTest(admin);
      boolean result = assetAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertTrue(result);
    }

    @Test
    void shouldDenyForAnonymous() {
      userService.setUserForTest(null);
      boolean result = assetAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonMember() {
      userService.setUserForTest(nonMember);
      boolean result = assetAccessChecker.hasRights(ActionType.CREATE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }

  @Nested
  class ReadAction {

    @Test
    void shouldDenyForMissingAssetId() {
      userService.setUserForTest(member);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = assetAccessChecker.hasRights(ActionType.READ, params);
      assertFalse(result);
    }

    @Test
    void shouldDenyForNonexistentAsset() {
      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), 999999L));
      assertFalse(result);
    }

    @Test
    void shouldDenyForDeletedAsset() {
      Asset asset = dataService.createAsset(team, member, AssetType.IMAGE, "photo.jpg");
      dataService.deleteAsset(asset);

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }

    @Test
    void shouldAllowCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, member, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyNonCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }

    @Test
    void shouldAllowMemberForAttachedAsset() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.READ, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }
  }

  @Nested
  class UpdateAction {

    @Test
    void shouldDenyForMissingAssetId() {
      userService.setUserForTest(organizer);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = assetAccessChecker.hasRights(ActionType.UPDATE, params);
      assertFalse(result);
    }

    @Test
    void shouldAllowCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, member, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyNonCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }

    @Test
    void shouldAllowOrganizerForAttachedAsset() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(organizer);
      boolean result =
          assetAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldAllowAdminForAttachedAsset() {
      Post post =
          dataService.createPost(team, organizer, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, organizer, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(admin);
      boolean result =
          assetAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyMemberForAttachedAsset() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.UPDATE, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }
  }

  @Nested
  class DeleteAction {

    @Test
    void shouldDenyForMissingAssetId() {
      userService.setUserForTest(organizer);
      List<Object> params = new ArrayList<>();
      params.add(team.getSlug());
      params.add(null);
      boolean result = assetAccessChecker.hasRights(ActionType.DELETE, params);
      assertFalse(result);
    }

    @Test
    void shouldAllowCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, member, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyNonCreatorForUnattachedAsset() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }

    @Test
    void shouldAllowOrganizerForAttachedAsset() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(organizer);
      boolean result =
          assetAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), asset.getId()));
      assertTrue(result);
    }

    @Test
    void shouldDenyMemberForAttachedAsset() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "photo.jpg");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      userService.setUserForTest(member);
      boolean result =
          assetAccessChecker.hasRights(ActionType.DELETE, List.of(team.getSlug(), asset.getId()));
      assertFalse(result);
    }
  }

  @Nested
  class UnsupportedActions {

    @Test
    void shouldDenyList() {
      userService.setUserForTest(admin);
      boolean result = assetAccessChecker.hasRights(ActionType.LIST, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyListAllTeams() {
      userService.setUserForTest(admin);
      boolean result =
          assetAccessChecker.hasRights(ActionType.LIST_ALL_TEAMS, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyJoin() {
      userService.setUserForTest(admin);
      boolean result = assetAccessChecker.hasRights(ActionType.JOIN, List.of(team.getSlug()));
      assertFalse(result);
    }

    @Test
    void shouldDenyLeave() {
      userService.setUserForTest(admin);
      boolean result = assetAccessChecker.hasRights(ActionType.LEAVE, List.of(team.getSlug()));
      assertFalse(result);
    }
  }
}
