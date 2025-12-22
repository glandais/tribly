package com.tribly.service.post;

import com.tribly.domain.common.repository.TeamPublicationQuery;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.posts.response.PostListResponse;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.SlugService;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PostService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(PostService.class);

  @Inject PostRepository postRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject RouteRepository routeRepository;

  @Inject SlugService slugService;

  public PostListResponse listPosts(
      String slug,
      @Nullable Long userId,
      @Nullable LocalDateTime from,
      @Nullable LocalDateTime to,
      @Nullable Status status,
      int page,
      int size) {
    Team team =
        teamRepository.findBySlug(slug).orElseThrow(() -> BusinessException.notFound("Team", slug));

    PostQueryParams postQueryParams = getPostQueryParams(userId, status, team);

    TeamPublicationQuery postQuery =
        new TeamPublicationQuery(
            team.getId(),
            page,
            size,
            null,
            postQueryParams.visibility(),
            from,
            to,
            postQueryParams.statuses());
    TriblyPage<Post> postTriblyPage = postRepository.find(postQuery);

    List<PostDto> dtos = postTriblyPage.items().stream().map(PostDto::from).toList();
    return new PostListResponse(dtos, postTriblyPage.total(), page, size);
  }

  protected Post getPost(String teamSlug, String postSlug, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    PostQueryParams postQueryParams = getPostQueryParams(userId, null, team);

    TeamPublicationQuery postQuery =
        new TeamPublicationQuery(
            team.getId(),
            0,
            1,
            postSlug,
            postQueryParams.visibility(),
            null,
            null,
            postQueryParams.statuses());
    TriblyPage<Post> triblyPage = postRepository.find(postQuery);
    if (triblyPage.items().isEmpty()) {
      throw BusinessException.notFound("Post", postSlug);
    } else {
      return triblyPage.items().getFirst();
    }
  }

  public PostDto getPostDetail(String teamSlug, String postSlug, @Nullable Long userId) {
    return PostDto.from(getPost(teamSlug, postSlug, userId));
  }

  private record PostQueryParams(List<Status> statuses, @Nullable Visibility visibility) {}

  private PostQueryParams getPostQueryParams(
      @Nullable Long userId, @Nullable Status status, Team team) {
    boolean canSeeDrafts = securityService.canSeeDrafts(userId, team);
    List<Status> statuses = getPostStatuses(status, canSeeDrafts);

    Visibility visibility = getVisibility(userId, team);
    return new PostQueryParams(statuses, visibility);
  }

  private List<Status> getPostStatuses(@Nullable Status status, boolean canSeeDrafts) {
    List<Status> statuses;
    if (status == null) {
      if (canSeeDrafts) {
        statuses = Arrays.asList(Status.values());
      } else {
        statuses = List.of(Status.PUBLISHED, Status.CANCELLED);
      }
    } else {
      if (status.equals(Status.DRAFT) && !canSeeDrafts) {
        statuses = List.of();
      } else {
        statuses = List.of(status);
      }
    }
    return statuses;
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
