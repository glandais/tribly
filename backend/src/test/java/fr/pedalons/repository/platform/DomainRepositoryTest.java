package fr.pedalons.repository.platform;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DomainRepositoryTest extends AbstractBaseTest {

  @Inject DomainRepository domainRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void findByIdOptional_shouldReturnDomain() {
    Domain domain =
        dataService.createDomain("test.example.com", "Test Domain", "https://test.example.com");

    Optional<Domain> result = domainRepository.findByIdOptional(domain.getId());

    assertTrue(result.isPresent());
    assertEquals(domain.getId(), result.get().getId());
    assertEquals("test.example.com", result.get().getDomain());
    assertEquals("Test Domain", result.get().getName());
  }

  @Test
  void findByIdOptional_shouldReturnEmptyForNonexistentId() {
    Optional<Domain> result = domainRepository.findByIdOptional(999999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void findByIdOptional_shouldReturnEmptyForDeletedDomain() {
    Domain domain =
        dataService.createDomain(
            "deleted.example.com", "Deleted Domain", "https://deleted.example.com");
    dataService.deleteDomain(domain);

    Optional<Domain> result = domainRepository.findByIdOptional(domain.getId());

    assertTrue(result.isEmpty());
  }
}
