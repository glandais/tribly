package fr.pedalons.repository.post;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PostRepositoryTest extends AbstractBaseTest {

  @Inject PostRepository postRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User user;
  private Team team;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
    now = Instant.now();
  }

  @Nested
  @DisplayName("findByTeamAndSlug")
  class FindByTeamAndSlug {

    @Test
    @DisplayName("Should find post by team and slug")
    void findByTeamAndSlug_shouldFindPost() {
      Post post = dataService.createPost(team, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-post");

      assertTrue(result.isPresent());
      assertEquals(post.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findByTeamAndSlug_nonExistentSlug_shouldReturnEmpty() {
      dataService.createPost(team, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndSlug(
              domain.getId(), team.getId(), user.getId(), "non-existent");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted post")
    void findByTeamAndSlug_deleted_shouldReturnEmpty() {
      Post post = dataService.createPost(team, user, "Test Post", now);
      dataService.deletePost(post);

      Optional<Post> result =
          postRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-post");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find post from different team")
    void findByTeamAndSlug_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createPost(otherTeam, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndSlug(domain.getId(), team.getId(), user.getId(), "test-post");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("findByTeamAndId")
  class FindByTeamAndId {

    @Test
    @DisplayName("Should find post by team and id")
    void findByTeamAndId_shouldFindPost() {
      Post post = dataService.createPost(team, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndId(domain.getId(), team.getId(), user.getId(), post.getId());

      assertTrue(result.isPresent());
      assertEquals(post.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty for non-existent id")
    void findByTeamAndId_nonExistentId_shouldReturnEmpty() {
      dataService.createPost(team, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndId(domain.getId(), team.getId(), user.getId(), 999999L);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for deleted post")
    void findByTeamAndId_deleted_shouldReturnEmpty() {
      Post post = dataService.createPost(team, user, "Test Post", now);
      dataService.deletePost(post);

      Optional<Post> result =
          postRepository.findByTeamAndId(domain.getId(), team.getId(), user.getId(), post.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should not find post from different team")
    void findByTeamAndId_differentTeam_shouldReturnEmpty() {
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      Post post = dataService.createPost(otherTeam, user, "Test Post", now);

      Optional<Post> result =
          postRepository.findByTeamAndId(domain.getId(), team.getId(), user.getId(), post.getId());

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("existsByTeamAndSlug")
  class ExistsByTeamAndSlug {

    @Test
    @DisplayName("Should return true for existing post")
    void existsByTeamAndSlug_existing_shouldReturnTrue() {
      dataService.createPost(team, user, "Test Post", now);

      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "test-post");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false for non-existent slug")
    void existsByTeamAndSlug_nonExistent_shouldReturnFalse() {
      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "non-existent");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true for deleted post (slug still reserved)")
    void existsByTeamAndSlug_deleted_shouldReturnTrue() {
      Post post = dataService.createPost(team, user, "Test Post", now);
      dataService.deletePost(post);

      boolean exists = postRepository.existsByTeamAndSlug(team.getId(), "test-post");

      assertTrue(exists);
    }
  }

  @Nested
  @DisplayName("Query Builder Methods")
  class QueryBuilderMethods {

    @Test
    @DisplayName("getQuerySlug should build correct query")
    void getQuerySlug_shouldBuildCorrectQuery() {
      TeamEntityQueryBasic query =
          postRepository.getQuerySlug(domain.getId(), team.getId(), user.getId(), "test-slug");

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    @DisplayName("getQuerySlug should work with null userId")
    void getQuerySlug_shouldWorkWithNullUserId() {
      TeamEntityQueryBasic query =
          postRepository.getQuerySlug(domain.getId(), team.getId(), null, "test-slug");

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    @DisplayName("getQueryId should build correct query")
    void getQueryId_shouldBuildCorrectQuery() {
      TeamEntityQueryBasic query =
          postRepository.getQueryId(domain.getId(), team.getId(), user.getId(), 12345L);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals(12345L, query.id());
    }

    @Test
    @DisplayName("getQueryId should work with null userId")
    void getQueryId_shouldWorkWithNullUserId() {
      TeamEntityQueryBasic query =
          postRepository.getQueryId(domain.getId(), team.getId(), null, 12345L);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals(12345L, query.id());
    }
  }

  @Nested
  @DisplayName("Entity Type Methods")
  class EntityTypeMethods {

    @Test
    @DisplayName("getEntityType should return POST")
    void getEntityType_shouldReturnPost() {
      assertEquals(TeamEntityType.POST, postRepository.getEntityType());
    }

    @Test
    @DisplayName("getAllEntityType should return POST")
    void getAllEntityType_shouldReturnPost() {
      assertEquals(EntityType.POST, postRepository.getAllEntityType());
    }
  }
}
