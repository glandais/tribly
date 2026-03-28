package fr.pedalons.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryTest extends AbstractBaseTest {

  @Inject UserRepository userRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
  }

  @Test
  void findByEmailAndDomain_shouldReturnUser() {
    dataService.createUser("test@example.com", "Test User");

    Optional<User> result = userRepository.findByEmailAndDomain(domain.getId(), "test@example.com");

    assertTrue(result.isPresent());
    assertEquals("test@example.com", result.get().getEmail());
    assertEquals("Test User", result.get().getDisplayName());
  }

  @Test
  void findByEmailAndDomain_shouldReturnEmptyForNonexistent() {
    Optional<User> result =
        userRepository.findByEmailAndDomain(domain.getId(), "nonexistent@example.com");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByEmailAndDomain_shouldIgnoreDeletedUsers() {
    User user = dataService.createUser("deleted@example.com", "Deleted User");
    dataService.deleteUser(user);

    Optional<User> result =
        userRepository.findByEmailAndDomain(domain.getId(), "deleted@example.com");

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
  void findActiveByIdAndDomain_shouldReturnUser() {
    User user = dataService.createUser("active@example.com", "Active User");

    Optional<User> result = userRepository.findActiveByIdAndDomain(domain.getId(), user.getId());

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
    assertEquals("Active User", result.get().getDisplayName());
  }

  @Test
  void findActiveByIdAndDomain_shouldReturnEmptyForDeletedUser() {
    User user = dataService.createUser("deleted@example.com", "Deleted User");
    dataService.deleteUser(user);

    Optional<User> result = userRepository.findActiveByIdAndDomain(domain.getId(), user.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findActiveByIdAndDomain_shouldReturnEmptyForNonExistentUser() {
    Optional<User> result = userRepository.findActiveByIdAndDomain(domain.getId(), 999999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findActiveByIdAndDomain_shouldReturnEmptyForWrongDomain() {
    Domain otherDomain = dataService.createDomain("other.com", "Other Domain", "http://other.com");
    User user = dataService.createUser("active@example.com", "Active User");

    Optional<User> result =
        userRepository.findActiveByIdAndDomain(otherDomain.getId(), user.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void searchByDisplayNameAndDomain_shouldFindMatchingUsers() {
    dataService.createUser("john@example.com", "John Doe");
    dataService.createUser("jane@example.com", "Jane Smith");
    dataService.createUser("bob@example.com", "Bob Johnson");

    List<User> results = userRepository.searchByDisplayNameAndDomain(domain.getId(), "john", 10);

    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(u -> u.getDisplayName().equals("John Doe")));
    assertTrue(results.stream().anyMatch(u -> u.getDisplayName().equals("Bob Johnson")));
  }

  @Test
  void searchByDisplayNameAndDomain_shouldBeCaseInsensitive() {
    dataService.createUser("alice@example.com", "Alice Wonder");

    List<User> results = userRepository.searchByDisplayNameAndDomain(domain.getId(), "ALICE", 10);

    assertEquals(1, results.size());
    assertEquals("Alice Wonder", results.get(0).getDisplayName());
  }

  @Test
  void searchByDisplayNameAndDomain_shouldRespectLimit() {
    dataService.createUser("user1@example.com", "Smith One");
    dataService.createUser("user2@example.com", "Smith Two");
    dataService.createUser("user3@example.com", "Smith Three");

    List<User> results = userRepository.searchByDisplayNameAndDomain(domain.getId(), "smith", 2);

    assertEquals(2, results.size());
  }

  @Test
  void searchByDisplayNameAndDomain_shouldIgnoreDeletedUsers() {
    dataService.createUser("visible@example.com", "Visible User");
    User deletedUser = dataService.createUser("hidden@example.com", "Hidden User");
    dataService.deleteUser(deletedUser);

    List<User> results = userRepository.searchByDisplayNameAndDomain(domain.getId(), "user", 10);

    assertEquals(1, results.size());
    assertEquals("Visible User", results.get(0).getDisplayName());
  }
}
