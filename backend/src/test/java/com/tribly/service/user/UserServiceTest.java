package com.tribly.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.user.User;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.users.response.UserDto;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserServiceTest {

  @Inject UserService userService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  // ==================== Get Public User DTO ====================

  @Nested
  class GetPublicUserDto {

    @Test
    void shouldReturnPublicUserDtoForExistingUser() {
      User user = dataService.createUser("test@example.com", "Test User");

      PublicUserDto result = userService.getPublicUserDto(user.getId());

      assertNotNull(result);
      assertEquals(TsidUtils.toString(user.getId()), result.id());
      assertEquals("Test User", result.displayName());
    }

    @Test
    void shouldThrowForNonexistentUser() {
      assertThrows(BusinessException.class, () -> userService.getPublicUserDto(999999L));
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      assertThrows(BusinessException.class, () -> userService.getPublicUserDto(user.getId()));
    }
  }

  // ==================== Get User DTO ====================

  @Nested
  class GetUserDto {

    @Test
    void shouldReturnUserDtoForExistingUser() {
      User user = dataService.createUser("test@example.com", "Test User");

      UserDto result = userService.getUserDto(user.getId());

      assertNotNull(result);
      assertEquals(TsidUtils.toString(user.getId()), result.id());
      assertEquals("test@example.com", result.email());
      assertEquals("Test User", result.displayName());
      assertNotNull(result.createdAt());
    }

    @Test
    void shouldThrowForNonexistentUser() {
      assertThrows(BusinessException.class, () -> userService.getUserDto(999999L));
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      assertThrows(BusinessException.class, () -> userService.getUserDto(user.getId()));
    }
  }

  // ==================== Update User ====================

  @Nested
  class UpdateUser {

    @Test
    void shouldUpdateDisplayName() {
      User user = dataService.createUser("test@example.com", "Original Name");

      UserDto result = userService.updateUser(user.getId(), "Updated Name");

      assertEquals("Updated Name", result.displayName());
    }

    @Test
    void shouldPreserveDisplayNameWhenNull() {
      User user = dataService.createUser("test@example.com", "Original Name");

      UserDto result = userService.updateUser(user.getId(), null);

      assertEquals("Original Name", result.displayName());
    }

    @Test
    void shouldThrowForNonexistentUser() {
      assertThrows(BusinessException.class, () -> userService.updateUser(999999L, "New Name"));
    }

    @Test
    void shouldThrowForDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      assertThrows(BusinessException.class, () -> userService.updateUser(user.getId(), "New Name"));
    }
  }

  // ==================== Delete User ====================

  @Nested
  class DeleteUser {

    @Test
    void shouldSoftDeleteUser() {
      User user = dataService.createUser("test@example.com", "Test User");

      userService.deleteUser(user.getId());

      assertThrows(BusinessException.class, () -> userService.getUserDto(user.getId()));
    }

    @Test
    void shouldThrowForNonexistentUser() {
      assertThrows(BusinessException.class, () -> userService.deleteUser(999999L));
    }

    @Test
    void shouldThrowForAlreadyDeletedUser() {
      User user = dataService.createUser("deleted@example.com", "Deleted User");
      dataService.deleteUser(user);

      assertThrows(BusinessException.class, () -> userService.deleteUser(user.getId()));
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
