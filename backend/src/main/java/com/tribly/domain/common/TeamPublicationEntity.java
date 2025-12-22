package com.tribly.domain.common;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Entity
public abstract class TeamPublicationEntity extends TeamEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  protected Status status = Status.DRAFT;

  @Nullable
  @Column(name = "publish_at")
  protected Instant publishAt;

  public TeamPublicationEntity() {}

  public TeamPublicationEntity(
      Team team, User createdBy, String name, String slug, Instant dateTime) {
    this.team = team;
    this.createdBy = createdBy;
    this.name = name;
    this.slug = slug;
    this.dateTime = dateTime;
  }
}
