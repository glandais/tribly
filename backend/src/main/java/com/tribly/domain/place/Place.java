package com.tribly.domain.place;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PlaceType type;

    @Column(name = "address", columnDefinition = "TEXT")
    @Nullable
    private String address;

    @Column(name = "lat", precision = 10, scale = 8, nullable = false)
    private BigDecimal lat;

    @Column(name = "lng", precision = 11, scale = 8, nullable = false)
    private BigDecimal lng;

    @Column(name = "phone", length = 50)
    @Nullable
    private String phone;

    @Column(name = "website", length = 500)
    @Nullable
    private String website;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Nullable
    private String notes;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

}
