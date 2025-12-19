package com.tribly.domain.team;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "team_domains")
public class TeamDomain extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @NotBlank
    @Size(max = 255)
    @Column(name = "domain", nullable = false, unique = true)
    private String domain;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "verification_token", length = 100)
    private String verificationToken;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    public TeamDomain() {
    }

    public TeamDomain(Team team, String domain) {
        this.team = team;
        this.domain = domain;
        this.verificationToken = generateVerificationToken();
    }

    private static String generateVerificationToken() {
        return "tribly-verify-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public void verify() {
        this.verified = true;
    }

    public void regenerateVerificationToken() {
        this.verificationToken = generateVerificationToken();
        this.verified = false;
    }
}
