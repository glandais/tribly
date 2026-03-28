package fr.pedalons.domain.ridetemplate;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "ride_template_groups",
    indexes = {
      @Index(columnList = "template_id"),
    })
@NoArgsConstructor
public class RideTemplateGroup extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  private RideTemplate template;

  @Column(name = "name", nullable = false, length = 250)
  private String name;

  @Nullable
  @Column(name = "time")
  protected LocalTime time;

  @Column(name = "average_speed")
  @Nullable
  private Float averageSpeed;

  @Column(name = "max_participants")
  @Nullable
  private Integer maxParticipants;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder = 0;

  public RideTemplateGroup(User creator, RideTemplate template, String name) {
    super(creator);
    this.template = template;
    this.name = name;
  }
}
