package com.tribly.domain.team;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "user_teams", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "team_id"})
})
public class UserTeam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private TeamRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private @Nullable User invitedBy;

    public UserTeam() {
        this.joinedAt = Instant.now();
    }

    public UserTeam(User user, Team team, TeamRole role) {
        this.user = user;
        this.team = team;
        this.role = role;
        this.joinedAt = Instant.now();
    }

    public UserTeam(User user, Team team, TeamRole role, User invitedBy) {
        this(user, team, role);
        this.invitedBy = invitedBy;
    }

    public boolean isAdmin() {
        return role == TeamRole.ADMIN;
    }

    public boolean isOrganizer() {
        return role == TeamRole.ADMIN || role == TeamRole.ORGANIZER;
    }

    public boolean canManageMembers() {
        return role == TeamRole.ADMIN;
    }

    public boolean canCreateRides() {
        return role == TeamRole.ADMIN || role == TeamRole.ORGANIZER;
    }
}
