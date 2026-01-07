package com.tribly.domain.team;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.NotNullableDbValue;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Setter
@Getter
@Entity
@Table(
    name = "teams",
    indexes = {@Index(columnList = "slug, deleted")})
@NoArgsConstructor
public class Team extends BaseEntity {

  @Column(name = "name", nullable = false, length = 250)
  private String name;

  @Column(name = "slug", nullable = false, unique = true, length = 250)
  private String slug;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "description_id")
  @NotNullableDbValue
  private TeamPage aboutPage;

  @OneToMany(mappedBy = "team")
  @SQLRestriction("is_about_page = false AND deleted = false")
  @OrderBy("pageOrder ASC")
  private List<TeamPage> additionalPages = new ArrayList<>();

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserTeam> members = new HashSet<>();

  @Column(name = "enable_trips", nullable = false)
  private boolean enableTrips = true;

  @Column(name = "enable_ads", nullable = false)
  private boolean enableAds = true;

  public Team(User creator, String name, String slug, Visibility visibility) {
    super(creator);
    this.name = name;
    this.slug = slug;
    this.visibility = visibility;
    this.aboutPage = TeamPage.createAboutPage(creator, this, visibility);
  }
}
