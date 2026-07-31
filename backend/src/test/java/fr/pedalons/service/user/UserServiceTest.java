package fr.pedalons.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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

    @Test
    void shouldThrowForAlreadyDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(PedalonsException.class, () -> userService.deleteUser());
    }
  }
}
