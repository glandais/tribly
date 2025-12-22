package com.tribly.domain.post.repository;

import com.tribly.domain.common.repository.TeamPublicationRepository;
import com.tribly.domain.post.Post;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository extends TeamPublicationRepository<Post> {

  @Override
  public Class<Post> getEntityClass() {
    return Post.class;
  }
}
