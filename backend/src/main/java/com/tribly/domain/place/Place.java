package com.tribly.domain.place;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "places")
@NoArgsConstructor
public class Place extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "address")
  @Nullable
  private String address;

  @Column(name = "link")
  @Nullable
  private String link;

  @Column(name = "start_place")
  private boolean startPlace = true;

  @Column(name = "end_place")
  private boolean endPlace = true;

  @Column(name = "geometry", columnDefinition = "geometry(Point,4326)")
  @Nullable
  private Point<G2D> geometry;

  public Place(User createdBy, Team team) {
    super(createdBy);
    this.team = team;
  }
}
