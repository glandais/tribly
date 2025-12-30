package com.tribly.domain.common;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "team_entities",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_team_entity_slug",
          columnNames = {"team_id", "entity_type", "slug"})
    },
    indexes = {
      @Index(columnList = "team_id, deleted"),
      @Index(columnList = "entity_type, deleted, date_time"),
      @Index(columnList = "entity_type, deleted, team_id"),
      @Index(columnList = "publish_at, status, deleted"),
      // ride
      @Index(columnList = "entity_type, deleted, team_id, date_time"),
      @Index(columnList = "entity_type, deleted, team_id, slug"),
    })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.INTEGER)
@NoArgsConstructor
public abstract class TeamEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  protected Team team;

  @Column(name = "date_time", nullable = false)
  protected Instant dateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  protected Status status = Status.PUBLISHED;

  @Nullable
  @Column(name = "publish_at")
  protected Instant publishAt;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  protected String name;

  @NotBlank
  @Size(max = 255)
  @Pattern(
      regexp = "^[a-z0-9-]+$",
      message = "Slug must contain only lowercase letters, numbers, and hyphens")
  @Column(name = "slug", nullable = false)
  protected String slug;

  @Column(name = "markdown", columnDefinition = "TEXT")
  @Nullable
  protected String markdown;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  protected Visibility visibility;

  @OneToMany(mappedBy = "teamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Asset> assets = new HashSet<>();

  public TeamEntity(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy);
    this.team = team;
    this.dateTime = dateTime;
    this.name = name;
    this.slug = slug;
    this.visibility = visibility;
  }
}
