package com.tribly.domain.common;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Entity
public abstract class Publication extends TeamEntity {

  @Nullable
  @Column(name = "publish_at")
  protected Instant publishAt;

  public Publication() {}

  public Publication(Team team, User createdBy, String name, String slug, Instant dateTime) {
    this.team = team;
    this.createdBy = createdBy;
    this.name = name;
    this.slug = slug;
    this.dateTime = dateTime;
  }
}
