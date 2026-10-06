package fr.pedalons.domain.common;

import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
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
      // agenda: upcoming / past by end (docs/LEDGER_*.md API-85)
      @Index(name = "idx_team_entities_team_end", columnList = "team_id, end_date_time"),
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

  /**
   * When a ride, a trip or a trip stage is over, as computed by {@code PublicationEndCalculator} —
   * the only writer. Null for everything else, and for a row written by a backend that predates the
   * column: readers fall back on {@code dateTime} plus {@code
   * PublicationEndCalculator.DEFAULT_DURATION} (docs/LEDGER_*.md API-85).
   */
  @Nullable
  @Column(name = "end_date_time")
  protected Instant endDateTime;

  /**
   * The zone this entity's dates were entered in (IANA id): that of its start place or route for a
   * ride, a trip or a stage, the team's for everything else. <b>Not a cache</b>: it is frozen when
   * the entity is saved and only a new save recomputes it, never a side effect (a route whose GPX
   * is replaced moves nobody's zone). With {@code dateTime} it gives back the wall time that was
   * typed. Null only on a row written by a backend that predates the column — read it through
   * {@link #zone()} (docs/LEDGER_*.md API-60).
   */
  @Nullable
  @Column(name = "timezone", length = 64)
  protected String timezone;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  protected Status status = Status.PUBLISHED;

  @Nullable
  @Column(name = "publish_at")
  protected Instant publishAt;

  @Column(name = "name", nullable = false, length = 250)
  protected String name;

  @Column(name = "slug", nullable = false, length = 250)
  protected String slug;

  @Column(name = "markdown", nullable = false, columnDefinition = "TEXT")
  protected String markdown = "";

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  protected Visibility visibility;

  @OneToMany(mappedBy = "teamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Asset> assets = new HashSet<>();

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  /**
   * Set when enough distinct members reported this content: hidden from everyone but the team's
   * moderators until one of them decides. See {@code ReportService}.
   */
  @Nullable
  @Column(name = "moderation_hidden_at")
  private Instant moderationHiddenAt;

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
    // The team's zone until a service resolves a better one (rides, trips, stages).
    this.timezone = team != null ? team.getTimezone() : null;
  }

  /**
   * The stored zone, or the team's for a row an older backend wrote without one (docs/LEDGER_*.md
   * API-60).
   */
  public ZoneId zone() {
    return ZoneId.of(timezone != null ? timezone : team.getTimezone());
  }

  public abstract EntityType getEntityType();
}
