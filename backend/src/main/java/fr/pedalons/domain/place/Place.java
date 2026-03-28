package fr.pedalons.domain.place;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "places",
    indexes = {@Index(name = "idx_places_team", columnList = "team_id")})
@NoArgsConstructor
public class Place extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Column(name = "name", nullable = false, length = 250)
  private String name;

  @Column(name = "address", length = 250)
  @Nullable
  private String address;

  @Column(name = "link", length = 250)
  @Nullable
  private String link;

  @Column(name = "start_place", nullable = false)
  private boolean startPlace = true;

  @Column(name = "end_place", nullable = false)
  private boolean endPlace = true;

  @Column(name = "geometry", columnDefinition = "geometry(Point,4326)")
  @Nullable
  private Point<G2D> geometry;

  public Place(User createdBy, Team team, String name, boolean startPlace, boolean endPlace) {
    super(createdBy);
    this.team = team;
    this.name = name;
    this.startPlace = startPlace;
    this.endPlace = endPlace;
  }
}
