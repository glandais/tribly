package com.tribly.domain.ridetemplate;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
      @Index(columnList = "template_id, deleted"),
    })
@NoArgsConstructor
public class RideTemplateGroup extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  private RideTemplate template;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @Nullable
  @Column(name = "time")
  protected LocalTime time;

  @Column(name = "average_speed")
  @Nullable
  private Integer averageSpeed;

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
