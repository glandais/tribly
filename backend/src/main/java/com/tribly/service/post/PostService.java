package com.tribly.service.post;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.post.Post;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.posts.response.PostListResponse;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
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

  public PostListResponse listPosts(
      String teamSlug,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Post> posts =
        postRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .search(search)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<PostDto> dtos =
        posts.items().stream().map(post -> PostDto.from(post, assetService)).toList();
    return new PostListResponse(dtos, posts.total(), page, size);
  }

  public Post getPost(String teamSlug, String postSlug, @Nullable Long userId) {
    TriblyPage<Post> posts =
        postRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .slug(postSlug)
                .page(0)
                .size(1)
                .build());
    if (posts.items().isEmpty()) {
      throw BusinessException.notFound("Post", postSlug);
    } else {
      return posts.items().getFirst();
    }
  }

  public PostDto getPostDetail(String teamSlug, String postSlug, @Nullable Long userId) {
    return PostDto.from(getPost(teamSlug, postSlug, userId), assetService);
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

    Post post = new Post(creator, team, request.dateTime(), request.name(), slug, visibility);
    post.setStatus(request.status());
    post.setPublishAt(request.publishAt());

    postRepository.persistAndFlush(post);

    updateMedia(post, request.media());

    postRepository.persist(post);

    LOG.infov("Post '{0}' created by user {1} for team {2}", post.getName(), creatorId, teamSlug);
    return PostDto.from(post, assetService);
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
    post.setDateTime(request.dateTime());
    post.setStatus(request.status());
    // publishAt can be explicitly set to null to remove scheduled publishing
    post.setPublishAt(request.publishAt());

    updateMedia(post, request.media());

    postRepository.persist(post);

    LOG.infov("Post {0} updated by user {1}", postSlug, userId);
    return PostDto.from(post, assetService);
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
