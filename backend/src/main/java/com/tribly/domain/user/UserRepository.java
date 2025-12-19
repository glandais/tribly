package com.tribly.domain.user;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email = ?1 and deleted = false", email).firstResultOptional();
    }

    public Optional<User> findActiveById(Long id) {
        return find("id = ?1 and deleted = false", id).firstResultOptional();
    }

    public List<User> searchByDisplayName(String query, int limit) {
        return find("LOWER(displayName) LIKE LOWER(?1) and deleted = false", "%" + query + "%")
                .page(0, limit)
                .list();
    }

}
