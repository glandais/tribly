package fr.pedalons.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.notification.Notification;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationType;
import fr.pedalons.enums.PushPlatform;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.NotificationTestData;
import fr.pedalons.util.PushDeviceTestData;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserServiceTest extends AbstractBaseTest {

  @Inject UserService userService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;
  @Inject NotificationTestData notifications;
  @Inject PushDeviceTestData pushDevices;

  @InjectMock JsonWebToken jwt;
  @InjectMock SecurityIdentity securityIdentity;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
  }

  // ==================== Get User DTO ====================

  @Nested
  class GetUserDto {

    @Test
    void shouldReturnUserDtoForExistingUser() {
      User user = dataService.createUser("test@example.com", "Test User");

      when(jwt.getClaim("email")).thenReturn("test@example.com");
      when(jwt.getClaim("name")).thenReturn("Original Name");
      when(securityIdentity.isAnonymous()).thenReturn(false);
      when(securityIdentity.getPrincipal()).thenReturn(jwt);

      UserDto result = userService.getUserDto();

      assertNotNull(result);
      assertEquals(TsidUtils.toString(user.getId()), result.id());
      assertEquals("test@example.com", result.email());
      assertEquals("Test User", result.displayName());
      assertNotNull(result.createdAt());
    }

    @Test
    void shouldThrowForNonexistentUser() {
      queryContext.setUserForTest(null);
      assertThrows(PedalonsException.class, () -> userService.getUserDto());
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(PedalonsException.class, () -> userService.getUserDto());
    }
  }

  // ==================== Update User ====================

  @Nested
  class UpdateUser {

    @Test
    void shouldUpdateDisplayName() {
      dataService.createUser("test@example.com", "Original Name");

      when(jwt.getClaim("email")).thenReturn("test@example.com");
      when(jwt.getClaim("name")).thenReturn("Original Name");
      when(securityIdentity.isAnonymous()).thenReturn(false);
      when(securityIdentity.getPrincipal()).thenReturn(jwt);

      UserDto result =
          userService.updateUser(UpdateUserRequest.builder().displayName("Updated Name").build());

      assertEquals("Updated Name", result.displayName());
    }

    @Test
    void shouldPreserveDisplayNameWhenNull() {
      dataService.createUser("test@example.com", "Original Name");

      when(jwt.getClaim("email")).thenReturn("test@example.com");
      when(jwt.getClaim("name")).thenReturn("Original Name");
      when(securityIdentity.isAnonymous()).thenReturn(false);
      when(securityIdentity.getPrincipal()).thenReturn(jwt);

      UserDto result = userService.updateUser(UpdateUserRequest.builder().build());

      assertEquals("Original Name", result.displayName());
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(
          PedalonsException.class,
          () ->
              userService.updateUser(UpdateUserRequest.builder().displayName("New Name").build()));
    }
  }

  // ==================== Delete User ====================

  @Nested
  class DeleteUser {

    @Test
    void shouldSoftDeleteUser() {
      User user = dataService.createUser("test@example.com", "Test User");

      when(jwt.getClaim("email")).thenReturn("test@example.com");
      when(jwt.getClaim("name")).thenReturn("Original Name");
      when(securityIdentity.isAnonymous()).thenReturn(false);
      when(securityIdentity.getPrincipal()).thenReturn(jwt);

      userService.deleteUser();

      queryContext.setUserForTest(user);
      assertThrows(PedalonsException.class, () -> userService.getUserDto());
    }

    /**
     * The account is only flagged deleted, so the database cascade never runs: the notification
     * data has to go explicitly — and only the leaver's.
     */
    @Test
    void shouldForgetNotificationDataOfTheDeletedUserOnly() {
      User leaver = dataService.createUser("leaver@example.com", "Leaver");
      User stayer = dataService.createUser("stayer@example.com", "Stayer");
      Team team = dataService.createTeam(leaver, "Team", "team", Visibility.PUBLIC);
      for (User user : List.of(leaver, stayer)) {
        List<Notification> inbox = notifications.seedInbox(user, team, 2);
        notifications.seedDelivery(inbox.getFirst(), NotificationChannel.EMAIL);
        notifications.seedPreference(
            user, NotificationType.RIDE_PUBLISHED, NotificationChannel.EMAIL, true);
        pushDevices.seed(user, PushPlatform.ANDROID, "token-" + user.getEmail());
      }

      queryContext.setUserForTest(leaver);
      userService.deleteUser();

      assertTrue(notifications.notificationTypesFor(leaver).isEmpty());
      assertTrue(notifications.deliveriesFor(leaver).isEmpty());
      assertEquals(0, notifications.preferenceCount(leaver));
      assertTrue(pushDevices.of(leaver).isEmpty());

      assertEquals(2, notifications.notificationTypesFor(stayer).size());
      assertEquals(1, notifications.deliveriesFor(stayer).size());
      assertEquals(1, notifications.preferenceCount(stayer));
      assertEquals(1, pushDevices.of(stayer).size());
    }

    @Test
    void shouldThrowForAlreadyDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(PedalonsException.class, () -> userService.deleteUser());
    }
  }
}
