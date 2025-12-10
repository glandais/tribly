package com.tribly.infrastructure.multitenancy;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TenantContext {

    private Long teamId;
    private String teamSlug;
    private String domain;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamSlug() {
        return teamSlug;
    }

    public void setTeamSlug(String teamSlug) {
        this.teamSlug = teamSlug;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isTeamResolved() {
        return teamId != null;
    }

    public void clear() {
        this.teamId = null;
        this.teamSlug = null;
        this.domain = null;
    }

    public void requireTeam() {
        if (!isTeamResolved()) {
            throw new IllegalStateException("Team context is required but not set");
        }
    }
}
