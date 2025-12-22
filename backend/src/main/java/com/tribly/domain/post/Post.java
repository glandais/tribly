package com.tribly.domain.post;

import com.tribly.domain.common.TeamPublicationEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("3")
public class Post extends TeamPublicationEntity {

  public Post() {
    super();
  }

  public Post(Team team, User createdBy, String name, String slug, Instant dateTime) {
    super(team, createdBy, name, slug, dateTime);
  }
}
