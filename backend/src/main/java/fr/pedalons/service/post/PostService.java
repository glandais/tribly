package fr.pedalons.service.post;

import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.dto.posts.response.PostDto;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.service.comment.CommentCountLookup;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PostService extends TeamEntityService<Post, PostRepository, PostDto> {

  @Inject PostRepository postRepository;

  @Inject CommentCountLookup commentCountLookup;

  @Override
  protected PostRepository getRepository() {
    return postRepository;
  }

  @Override
  protected PostDto toDto(Post entity) {
    return PostDto.from(entity, assetService, commentCountLookup.forEntity(entity));
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
            pedalonsContext.getUser(),
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

  @CheckAccess(entityType = EntityType.POST, action = ActionType.DELETE)
  @Transactional
  public PostDto undeletePost(String teamSlug, String postSlug) {
    Team team = teamService.getTeam(teamSlug);
    Post post = findBySlugIncludeDeleted(team, postSlug);
    post.setDeleted(false);
    postRepository.persist(post);
    return PostDto.from(post, assetService);
  }
}
