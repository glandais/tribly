package com.tribly.domain.post.repository;

import com.tribly.domain.common.repository.EntityWithSlugRepository;
import com.tribly.domain.common.repository.PublicationRepository;
import com.tribly.domain.post.Post;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PublicationRepository<Post>, EntityWithSlugRepository<Post> {

  @Override
  public Class<Post> getEntityClass() {
    return Post.class;
  }
}
