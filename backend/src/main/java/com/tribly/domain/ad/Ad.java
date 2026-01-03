package com.tribly.domain.ad;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
  private AdType adType;

  @Enumerated(EnumType.STRING)
  @Column(name = "rental_period", length = 20)
  @Nullable
  private RentalPeriod rentalPeriod;

  @Column(name = "latitude")
  @Nullable
  private Double latitude;

  @Column(name = "longitude")
  @Nullable
  private Double longitude;

  @Column(name = "location_description")
  @Size(max = 255)
  @Nullable
  private String locationDescription;

  public Ad(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility,
      AdType adType) {
    super(createdBy, team, dateTime, name, slug, visibility);
    this.adType = adType;
  }
}
