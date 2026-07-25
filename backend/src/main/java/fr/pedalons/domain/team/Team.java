package fr.pedalons.domain.team;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.common.NotNullableDbValue;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "teams",
    indexes = {@Index(columnList = "slug, deleted"), @Index(columnList = "domain_id, deleted")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_teams_domain_slug",
          columnNames = {"domain_id", "slug"})
    })
@NoArgsConstructor
public class Team extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "domain_id", nullable = false)
  private Domain domain;

  @Column(name = "name", nullable = false, length = 250)
  private String name;

  @Column(name = "slug", nullable = false, length = 250)
  private String slug;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  /**
   * LAZY on purpose: only {@code TeamDetailDto} renders the about page, but a Team is loaded by
   * every publication and route list row for its name and slug. EAGER made all of those pay for a
   * TeamPage — with its markdown and assets — that they never look at.
   */
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

  @Column(name = "enable_posts", nullable = false)
  private boolean enablePosts = true;

  @Column(name = "enable_rides", nullable = false)
  private boolean enableRides = true;

  @Column(name = "enable_routes", nullable = false)
  private boolean enableRoutes = true;

  @Column(name = "geometry", columnDefinition = "geometry(Point,4326)")
  @Nullable
  private Point<G2D> geometry;

  @Column(name = "visibility_editable", nullable = false)
  private boolean visibilityEditable = false;

  @Column(name = "joinable", nullable = false)
  private boolean joinable = false;

  @Column(name = "add_member_allowed", nullable = false)
  private boolean addMemberAllowed = false;

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  public Team(Domain domain, User creator, String name, String slug, Visibility visibility) {
    super(creator);
    this.domain = domain;
    this.name = name;
    this.slug = slug;
    this.visibility = visibility;
    this.aboutPage = TeamPage.createAboutPage(creator, this, visibility);
  }
}
