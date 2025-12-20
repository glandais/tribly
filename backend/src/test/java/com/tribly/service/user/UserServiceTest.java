package com.tribly.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.user.User;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

  // ==================== Search By Display Name ====================

  @Test
  void searchByDisplayName_shouldFindMatchingUsers() {
    dataService.createUser("john@example.com", "John Doe");
    dataService.createUser("jane@example.com", "Jane Smith");
    dataService.createUser("johnny@example.com", "Johnny Walker");

    List<PublicUserDto> result = userService.searchByDisplayName("john", 10);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(u -> u.displayName().equals("John Doe")));
    assertTrue(result.stream().anyMatch(u -> u.displayName().equals("Johnny Walker")));
  }

  @Test
  void searchByDisplayName_shouldBeCaseInsensitive() {
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
  void searchByDisplayName_shouldRespectLimit() {
    for (int i = 1; i <= 5; i++) {
      dataService.createUser("user" + i + "@example.com", "Test User " + i);
    }

    List<PublicUserDto> result = userService.searchByDisplayName("test", 3);

    assertEquals(3, result.size());
  }

  @Test
  void searchByDisplayName_shouldExcludeDeletedUsers() {
    User user1 = dataService.createUser("active@example.com", "Active User");
    User user2 = dataService.createUser("deleted@example.com", "Deleted User");
    dataService.deleteUser(user2);

    List<PublicUserDto> result = userService.searchByDisplayName("user", 10);

    assertEquals(1, result.size());
    assertEquals("Active User", result.getFirst().displayName());
  }

  @Test
  void searchByDisplayName_shouldReturnEmptyForNoMatches() {
    dataService.createUser("john@example.com", "John Doe");

    List<PublicUserDto> result = userService.searchByDisplayName("alice", 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void searchByDisplayName_shouldMatchPartialNames() {
    dataService.createUser("test@example.com", "Alice Johnson");

    List<PublicUserDto> result = userService.searchByDisplayName("ali", 10);

    assertEquals(1, result.size());
    assertEquals("Alice Johnson", result.getFirst().displayName());
  }
}
