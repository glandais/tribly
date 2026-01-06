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
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PostService extends TeamEntityService<Post> {

  private static final Logger LOG = Logger.getLogger(PostService.class);

  @Inject PostRepository postRepository;

  @Override
  protected EntityType getEntityType() {
    return EntityType.POST;
  }

  @Override
  protected Optional<Post> findByIdOptional(Long entityId) {
    return postRepository.findByIdOptional(entityId);
  }

  @Override
  protected Post getBySlug(Team team, String postSlug, @Nullable User user) {
    TriblyPage<Post> posts =
        postRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
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

  public PostListResponse listPosts(
      Team team,
      @Nullable User user,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Post> posts =
        postRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
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

  public PostDto getPostDetail(Team team, String postSlug, @Nullable User user) {
    return PostDto.from(get(team, postSlug, user), assetService);
  }

  @Transactional
  public PostDto createPost(Team team, PostRequest request, User creator) {
    // Security check: must be admin or organizer to create posts
    securityService.requireOrganizer(creator, team);

    validateVisibility(request, team);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), postRepository);

    Post post =
        new Post(creator, team, request.dateTime(), request.name(), slug, request.visibility());
    post.setStatus(request.status());
    if (request.status() == Status.DRAFT) {
      post.setPublishAt(request.publishAt());
    } else {
      post.setPublishAt(null);
    }

    postRepository.persistAndFlush(post);

    updateMedia(post, request.media());

    postRepository.persist(post);

    LOG.infov(
        "Post '{0}' created by user {1} for team {2}",
        post.getName(), creator.getId(), team.getId());
    return PostDto.from(post, assetService);
  }

  @Transactional
  public PostDto updatePost(Team team, String postSlug, PostRequest request, User user) {
    Post post = get(team, postSlug, user);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(user, team);

    validateVisibility(request, post.getTeam());

    post.setVisibility(request.visibility());

    post.setName(request.name());
    post.setDateTime(request.dateTime());
    post.setStatus(request.status());
    if (request.status() == Status.DRAFT) {
      post.setPublishAt(request.publishAt());
    } else {
      post.setPublishAt(null);
    }

    updateMedia(post, request.media());

    postRepository.persist(post);

    LOG.infov("Post {0} updated by user {1}", postSlug, user.getId());
    return PostDto.from(post, assetService);
  }

  @Transactional
  public void deletePost(Team team, String postSlug, User user) {
    Post post = get(team, postSlug, user);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(user, team);

    post.setDeleted(true);
    postRepository.persist(post);
    LOG.infov("Post {0} deleted by user {1}", postSlug, user.getId());
  }

  @Transactional
  public PostDto updateSlug(Team team, String slugParam, String newSlug, User user) {
    Post post = get(team, slugParam, user);
    String currentSlug = post.getSlug();

    securityService.requireOrganizer(user, team);

    if (!slugService.isValidSlug(newSlug)) {
      throw BusinessException.validation("Invalid slug format");
    }

    if (currentSlug.equals(newSlug)) {
      return PostDto.from(post, assetService);
    }

    if (postRepository.existsByTeamAndSlug(post.getTeam().getId(), newSlug)) {
      throw BusinessException.conflict("Slug already in use", "SLUG_TAKEN");
    }

    slugService.clearEntityRedirect(post.getTeam().getId(), EntityType.POST, newSlug);
    slugService.createEntityRedirect(post, currentSlug);

    post.setSlug(newSlug);
    postRepository.persist(post);

    LOG.infov("Post slug changed from {0} to {1} by user {2}", currentSlug, newSlug, user.getId());
    return PostDto.from(post, assetService);
  }
}
