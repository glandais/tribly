package com.tribly.domain.user;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email = ?1 and deleted = false", email).firstResultOptional();
    }

    public Optional<User> findByStravaId(String stravaId) {
        return find("stravaId = ?1 and deleted = false", stravaId).firstResultOptional();
    }

    public Optional<User> findByGoogleId(String googleId) {
        return find("googleId = ?1 and deleted = false", googleId).firstResultOptional();
    }

    public Optional<User> findByFacebookId(String facebookId) {
        return find("facebookId = ?1 and deleted = false", facebookId).firstResultOptional();
    }

    public Optional<User> findActiveById(Long id) {
        return find("id = ?1 and deleted = false", id).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email = ?1 and deleted = false", email) > 0;
    }

    public boolean existsByStravaId(String stravaId) {
        return count("stravaId = ?1 and deleted = false", stravaId) > 0;
    }
}
