package com.tribly.domain.team;

import com.tribly.domain.common.BaseEntity;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "teams",
    indexes = {
      @Index(columnList = "slug, deleted"),
      @Index(columnList = "name, deleted"),
    })
public class Team extends BaseEntity {

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank
  @Size(max = 100)
  @Pattern(
      regexp = "^[a-z0-9-]+$",
      message = "Slug must contain only lowercase letters, numbers, and hyphens")
  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @Column(name = "description", columnDefinition = "TEXT")
  private @Nullable String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserTeam> members = new HashSet<>();

  public Team() {}

  public Team(String name, String slug) {
    this.name = name;
    this.slug = slug;
  }
}
