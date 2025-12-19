package com.tribly.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryTest {

  @Inject UserRepository userRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void findByEmail_shouldReturnUser() {
    dataService.createUser("test@example.com", "Test User");

    Optional<User> result = userRepository.findByEmail("test@example.com");

    assertTrue(result.isPresent());
    assertEquals("test@example.com", result.get().getEmail());
    assertEquals("Test User", result.get().getDisplayName());
  }

  @Test
  void findByEmail_shouldReturnEmptyForNonexistent() {
    Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByEmail_shouldIgnoreDeletedUsers() {
    User user = dataService.createUser("deleted@example.com", "Deleted User");
    dataService.deleteUser(user);

    Optional<User> result = userRepository.findByEmail("deleted@example.com");

    assertTrue(result.isEmpty());
  }

  @Test
  void findActiveById_shouldReturnUser() {
    User user = dataService.createUser("active@example.com", "Active User");

    Optional<User> result = userRepository.findActiveById(user.getId());

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
  }

  @Test
  void findActiveById_shouldReturnEmptyForDeletedUser() {
    User user = dataService.createUser("inactive@example.com", "Inactive User");
    dataService.deleteUser(user);

    Optional<User> result = userRepository.findActiveById(user.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void searchByDisplayName_shouldFindMatchingUsers() {
    dataService.createUser("john@example.com", "John Doe");
    dataService.createUser("jane@example.com", "Jane Smith");
    dataService.createUser("bob@example.com", "Bob Johnson");

    List<User> results = userRepository.searchByDisplayName("john", 10);

    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(u -> u.getDisplayName().equals("John Doe")));
    assertTrue(results.stream().anyMatch(u -> u.getDisplayName().equals("Bob Johnson")));
  }

  @Test
  void searchByDisplayName_shouldBeCaseInsensitive() {
    dataService.createUser("alice@example.com", "Alice Wonder");

    List<User> results = userRepository.searchByDisplayName("ALICE", 10);

    assertEquals(1, results.size());
    assertEquals("Alice Wonder", results.get(0).getDisplayName());
  }

  @Test
  void searchByDisplayName_shouldRespectLimit() {
    dataService.createUser("user1@example.com", "Smith One");
    dataService.createUser("user2@example.com", "Smith Two");
    dataService.createUser("user3@example.com", "Smith Three");

    List<User> results = userRepository.searchByDisplayName("smith", 2);

    assertEquals(2, results.size());
  }

  @Test
  void searchByDisplayName_shouldIgnoreDeletedUsers() {
    dataService.createUser("visible@example.com", "Visible User");
    User deletedUser = dataService.createUser("hidden@example.com", "Hidden User");
    dataService.deleteUser(deletedUser);

    List<User> results = userRepository.searchByDisplayName("user", 10);

    assertEquals(1, results.size());
    assertEquals("Visible User", results.get(0).getDisplayName());
  }
}
