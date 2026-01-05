package com.tribly.domain.comment;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "comments",
    indexes = {
      @Index(columnList = "team_entity_id, deleted, created_at"),
      @Index(columnList = "parent_id, deleted")
    })
@NoArgsConstructor
public class Comment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_entity_id", nullable = false)
  private TeamEntity teamEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  @Nullable
  private Comment parent;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  public Comment(User createdBy, Team team, TeamEntity teamEntity, String content) {
    super(createdBy);
    this.team = team;
    this.teamEntity = teamEntity;
    this.content = content;
  }

  public Comment(
      User createdBy, Team team, TeamEntity teamEntity, @Nullable Comment parent, String content) {
    this(createdBy, team, teamEntity, content);
    this.parent = parent;
  }
}
