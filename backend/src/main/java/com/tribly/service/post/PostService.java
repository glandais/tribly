package com.tribly.service.post;

import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.repository.post.PostRepository;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PostService extends TeamEntityService<Post, PostRepository, PostDto> {

  @Inject PostRepository postRepository;

  @Override
  protected PostRepository getRepository() {
    return postRepository;
  }

  @Override
  protected PostDto toDto(Post entity) {
    return PostDto.from(entity, assetService);
  }

  @Override
  public Post findBySlug(Team team, String entitySlug) {
    return super.findBySlug(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.POST, action = ActionType.READ)
  public PostDto getDto(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.getDto(team, entitySlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.POST, action = ActionType.CREATE)
  public PostDto createPost(String teamSlug, PostRequest request) {
    Team team = teamService.getTeam(teamSlug);
    validateVisibility(team, request);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), postRepository);

    Post post =
        new Post(
            triblyContext.getUser(),
            team,
            request.dateTime(),
            request.name(),
            slug,
            request.visibility());
    post.setStatus(request.status());
    if (request.status() == Status.DRAFT) {
      post.setPublishAt(request.publishAt());
    } else {
      post.setPublishAt(null);
    }

    postRepository.persistAndFlush(post);

    updateMedia(post, request.media());

    postRepository.persist(post);

    return PostDto.from(post, assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.POST, action = ActionType.UPDATE)
  public PostDto updatePost(String teamSlug, String postSlug, PostRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Post post = findBySlug(team, postSlug);

    validateVisibility(team, request);

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

    return PostDto.from(post, assetService);
  }

  @CheckAccess(entityType = EntityType.POST, action = ActionType.UPDATE)
  @Transactional
  public PostDto updateSlug(String teamSlug, String slug, String newSlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.updateSlug(team, slug, newSlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.POST, action = ActionType.DELETE)
  public void deletePost(String teamSlug, String postSlug) {
    Team team = teamService.getTeam(teamSlug);
    Post post = findBySlug(team, postSlug);

    post.setDeleted(true);
    postRepository.persist(post);
  }
}
