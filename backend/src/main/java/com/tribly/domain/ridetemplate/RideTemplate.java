package com.tribly.domain.ridetemplate;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "ride_templates",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ride_template_slug",
          columnNames = {"team_id", "slug"})
    },
    indexes = {
      @Index(columnList = "team_id, deleted"),
      @Index(columnList = "team_id, slug, deleted"),
    })
@NoArgsConstructor
public class RideTemplate extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank
  @Size(max = 255)
  @Pattern(regexp = "^[a-z0-9-]+$")
  @Column(name = "slug", nullable = false)
  private String slug;

  @Column(name = "markdown", columnDefinition = "TEXT")
  @Nullable
  private String markdown;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private Status status = Status.PUBLISHED;

  @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RideTemplateGroup> groups = new ArrayList<>();

  public RideTemplate(User creator, Team team, String name, String slug) {
    super(creator);
    this.team = team;
    this.name = name;
    this.slug = slug;
  }

  public void addGroup(RideTemplateGroup group) {
    groups.add(group);
    group.setTemplate(this);
  }

  public int getGroupCount() {
    return (int) groups.stream().filter(g -> !g.isDeleted()).count();
  }
}
