package com.tribly.domain.asset;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.enums.AssetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * Represents a GPX track associated with a route.
 * Stores both PostGIS geometry for spatial queries and JSONB track points for efficient frontend rendering.
 */
@Setter
@Getter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  protected Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_entity_id")
  @Nullable
  protected TeamEntity teamEntity;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  protected AssetType type;

  @Column(name = "file_name", nullable = false)
  protected String fileName;

  public Asset(Team team, AssetType type, String fileName) {
    this.team = team;
    this.type = type;
    this.fileName = fileName;
  }
}
