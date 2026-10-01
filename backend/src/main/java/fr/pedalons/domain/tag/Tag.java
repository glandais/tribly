package fr.pedalons.domain.tag;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TagColor;
import fr.pedalons.enums.TagTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A word of a team's vocabulary for one kind of content (docs/LEDGER_*.md API-59).
 *
 * <p>A {@link BaseEntity}, not a {@code TeamEntity}: no slug, no visibility, no trash — a deleted
 * tag is detached from every content and gone (plan D11). Readable by whoever sees the team,
 * managed by its admins only.
 *
 * <p>The label is unique per (team, type) <b>case-insensitively</b>: the migration holds the
 * functional index {@code uk_tags_team_type_label} on {@code lower(label)}, which no JPA annotation
 * can express — {@code TagService} checks it before writing, the index settles a race.
 */
@Setter
@Getter
@Entity
@Table(name = "tags", indexes = @Index(name = "idx_tags_team_type", columnList = "team_id, type"))
@NoArgsConstructor
public class Tag extends BaseEntity {

  public static final int MAX_LABEL_LENGTH = 32;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private TagTarget type;

  @Column(name = "label", nullable = false, length = MAX_LABEL_LENGTH)
  private String label;

  @Enumerated(EnumType.STRING)
  @Column(name = "color", nullable = false, length = 20)
  private TagColor color;

  public Tag(User createdBy, Team team, TagTarget type, String label, TagColor color) {
    super(createdBy);
    this.team = team;
    this.type = type;
    this.label = label;
    this.color = color;
  }
}
