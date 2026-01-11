package com.tribly.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.TriblyException;
import com.tribly.domain.user.User;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.users.response.UserDto;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
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
class UserServiceTest {

  @Inject UserService userService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext queryContext;

  @InjectMock JsonWebToken jwt;
  @InjectMock SecurityIdentity securityIdentity;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
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
      assertThrows(TriblyException.class, () -> userService.getUserDto());
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(TriblyException.class, () -> userService.getUserDto());
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

      UserDto result = userService.updateUser("Updated Name");

      assertEquals("Updated Name", result.displayName());
    }

    @Test
    void shouldPreserveDisplayNameWhenNull() {
      dataService.createUser("test@example.com", "Original Name");

      when(jwt.getClaim("email")).thenReturn("test@example.com");
      when(jwt.getClaim("name")).thenReturn("Original Name");
      when(securityIdentity.isAnonymous()).thenReturn(false);
      when(securityIdentity.getPrincipal()).thenReturn(jwt);

      UserDto result = userService.updateUser(null);

      assertEquals("Original Name", result.displayName());
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(TriblyException.class, () -> userService.updateUser("New Name"));
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
      assertThrows(TriblyException.class, () -> userService.getUserDto());
    }

    @Test
    void shouldThrowForAlreadyDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      queryContext.setUserForTest(user);
      assertThrows(TriblyException.class, () -> userService.deleteUser());
    }
  }

  // ==================== Search By Display Name ====================

  @Nested
  class SearchByDisplayName {

    @Test
    void shouldFindMatchingUsers() {
      dataService.createUser("john@example.com", "John Doe");
      dataService.createUser("jane@example.com", "Jane Smith");
      dataService.createUser("johnny@example.com", "Johnny Walker");

      List<PublicUserDto> result = userService.searchByDisplayName("john", 10);

      assertEquals(2, result.size());
      assertTrue(result.stream().anyMatch(u -> u.displayName().equals("John Doe")));
      assertTrue(result.stream().anyMatch(u -> u.displayName().equals("Johnny Walker")));
    }

    @Test
    void shouldBeCaseInsensitive() {
      dataService.createUser("test@example.com", "Alice Test");

      List<PublicUserDto> resultLower = userService.searchByDisplayName("alice", 10);
      List<PublicUserDto> resultUpper = userService.searchByDisplayName("ALICE", 10);
      List<PublicUserDto> resultMixed = userService.searchByDisplayName("AlIcE", 10);

      assertEquals(1, resultLower.size());
      assertEquals(1, resultUpper.size());
      assertEquals(1, resultMixed.size());
      assertEquals("Alice Test", resultLower.getFirst().displayName());
    }

    @Test
    void shouldRespectLimit() {
      for (int i = 1; i <= 5; i++) {
        dataService.createUser("user" + i + "@example.com", "Test User " + i);
      }

      List<PublicUserDto> result = userService.searchByDisplayName("test", 3);

      assertEquals(3, result.size());
    }

    @Test
    void shouldExcludeDeletedUsers() {
      User user1 = dataService.createUser("active@example.com", "Active User");
      User user2 = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user2);

      List<PublicUserDto> result = userService.searchByDisplayName("user", 10);

      assertEquals(1, result.size());
      assertEquals("Active User", result.getFirst().displayName());
    }

    @Test
    void shouldReturnEmptyForNoMatches() {
      dataService.createUser("john@example.com", "John Doe");

      List<PublicUserDto> result = userService.searchByDisplayName("alice", 10);

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldMatchPartialNames() {
      dataService.createUser("test@example.com", "Alice Johnson");

      List<PublicUserDto> result = userService.searchByDisplayName("ali", 10);

      assertEquals(1, result.size());
      assertEquals("Alice Johnson", result.getFirst().displayName());
    }
  }
}
