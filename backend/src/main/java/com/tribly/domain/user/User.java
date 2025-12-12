package com.tribly.domain.user;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "strava_id", unique = true, length = 50)
    private String stravaId;

    @Column(name = "garmin_id", unique = true, length = 50)
    private String garminId;

    @Column(name = "google_id", unique = true, length = 100)
    private String googleId;

    @Column(name = "facebook_id", unique = true, length = 100)
    private String facebookId;

    @Column(name = "locale", length = 10)
    private String locale = "en";

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    public User() {
    }

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStravaId() {
        return stravaId;
    }

    public void setStravaId(String stravaId) {
        this.stravaId = stravaId;
    }

    public String getGarminId() {
        return garminId;
    }

    public void setGarminId(String garminId) {
        this.garminId = garminId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }

    public static User fromStrava(String stravaId, String email, String displayName, String avatarUrl) {
        User user = new User(email, displayName);
        user.setStravaId(stravaId);
        user.setAvatarUrl(avatarUrl);
        return user;
    }
}
