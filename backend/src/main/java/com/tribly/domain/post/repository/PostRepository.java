package com.tribly.domain.post.repository;

import com.tribly.domain.common.repository.EntityWithPublishAtRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.post.Post;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository
    implements TeamEntityRepository<Post, TeamEntityQueryBasic>,
        EntityWithPublishAtRepository<Post> {}
