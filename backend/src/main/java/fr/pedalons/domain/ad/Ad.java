package fr.pedalons.domain.ad;

import fr.pedalons.domain.common.NotNullableDbValue;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.RentalPeriod;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("7")
@NoArgsConstructor
public class Ad extends TeamEntity {

  @Column(name = "price", precision = 10, scale = 2)
  @Nullable
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(name = "ad_type", length = 20)
  @NotNullableDbValue
  private AdType adType;

  @Enumerated(EnumType.STRING)
  @Column(name = "rental_period", length = 20)
  @Nullable
  private RentalPeriod rentalPeriod;

  @Column(name = "location_geometry", columnDefinition = "geometry(Point,4326)")
  @Nullable
  private Point<G2D> locationGeometry;

  @Column(name = "location_description", length = 250)
  @Nullable
  private String locationDescription;

  public Ad(User createdBy, Team team, Instant dateTime, String name, String slug, AdType adType) {
    super(createdBy, team, dateTime, name, slug, Visibility.TEAM);
    this.adType = adType;
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.AD;
  }
}
