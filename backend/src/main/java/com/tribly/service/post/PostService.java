package com.tribly.service.post;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.posts.response.PostListResponse;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.SlugService;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PostService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(PostService.class);

  @Inject PostRepository postRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject SlugService slugService;

  public PostListResponse listPosts(
      String teamSlug,
      @Nullable Long userId,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Post> posts =
        postRepository.find(
            new TeamEntityQueryBasic(userId, Set.of(teamSlug), null, null, from, to, page, size));
    List<PostDto> dtos = posts.items().stream().map(PostDto::from).toList();
    return new PostListResponse(dtos, posts.total(), page, size);
  }

  protected Post getPost(String teamSlug, String postSlug, @Nullable Long userId) {
    TriblyPage<Post> posts =
        postRepository.find(
            new TeamEntityQueryBasic(userId, Set.of(teamSlug), postSlug, null, null, null, 0, 1));
    if (posts.items().isEmpty()) {
      throw BusinessException.notFound("Post", postSlug);
    } else {
      return posts.items().getFirst();
    }
  }

  public PostDto getPostDetail(String teamSlug, String postSlug, @Nullable Long userId) {
    return PostDto.from(getPost(teamSlug, postSlug, userId));
  }

  @Transactional
  public PostDto createPost(String teamSlug, PostRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: must be admin or organizer to create posts
    securityService.requireOrganizer(creatorId, team.getSlug());

    // Validate visibility: private teams can only have team-only posts
    Visibility visibility = request.visibility();
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only posts");
    }

    // Generate slug from name, ensure unique within team
    String slug =
        slugService.generateSlug(
            request.name(), s -> postRepository.existsByTeamAndSlug(team.getId(), s));

    Post post = new Post(team, creator, request.name(), slug, request.dateTime());
    post.setDescription(request.description());
    post.setVisibility(visibility);
    post.setStatus(request.status());
    post.setPublishAt(request.publishAt());

    postRepository.persist(post);

    LOG.infov("Post '{0}' created by user {1} for team {2}", post.getName(), creatorId, teamSlug);
    return PostDto.from(post);
  }

  @Transactional
  public PostDto updatePost(String teamSlug, String postSlug, PostRequest request, Long userId) {
    Post post = getPost(teamSlug, postSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(userId, teamSlug);

    // Validate visibility: private teams can only have team-only posts
    Team team = post.getTeam();
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only posts");
    }
    post.setVisibility(request.visibility());

    post.setName(request.name());
    post.setDescription(request.description());
    post.setDateTime(request.dateTime());
    post.setStatus(request.status());
    // publishAt can be explicitly set to null to remove scheduled publishing
    post.setPublishAt(request.publishAt());

    postRepository.persist(post);

    LOG.infov("Post {0} updated by user {1}", postSlug, userId);
    return PostDto.from(post);
  }

  @Transactional
  public void deletePost(String teamSlug, String postSlug, Long userId) {
    Post post = getPost(teamSlug, postSlug, userId);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(userId, teamSlug);

    post.setDeleted(true);
    postRepository.persist(post);
    LOG.infov("Post {0} deleted by user {1}", postSlug, userId);
  }
}
