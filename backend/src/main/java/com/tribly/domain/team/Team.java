package com.tribly.domain.team;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private @Nullable String description;

    @Column(name = "logo_url", length = 500)
    private @Nullable String logoUrl;

    @Column(name = "cover_image_url", length = 500)
    private @Nullable String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility = Visibility.TEAM;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "jsonb")
    private @Nullable Map<String, Object> settings;

    @Column(name = "max_members")
    private @Nullable Integer maxMembers;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTeam> members = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamDomain> domains = new HashSet<>();

    public Team() {
    }

    public Team(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public int getMemberCount() {
        return (int) members.stream().filter(m -> !m.isDeleted()).count();
    }

    public boolean hasCapacity() {
        return maxMembers == null || getMemberCount() < maxMembers;
    }
}
