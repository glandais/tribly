package com.tribly.infrastructure.multitenancy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class TenantResolver {

    private static final Logger LOG = Logger.getLogger(TenantResolver.class);

    @Inject
    EntityManager entityManager;

    public Optional<TeamInfo> resolveByDomain(String domain) {
        try {
            Object[] result = (Object[]) entityManager.createQuery(
                    "SELECT t.id, t.slug FROM Team t " +
                    "JOIN TeamDomain td ON td.team = t " +
                    "WHERE td.domain = :domain AND td.verified = true AND t.deleted = false AND td.deleted = false")
                    .setParameter("domain", domain)
                    .getSingleResult();

            return Optional.of(new TeamInfo((Long) result[0], (String) result[1]));
        } catch (NoResultException e) {
            LOG.debugv("No team found for domain: {0}", domain);
            return Optional.empty();
        }
    }

    public Optional<TeamInfo> resolveBySlug(String slug) {
        try {
            Object[] result = (Object[]) entityManager.createQuery(
                    "SELECT t.id, t.slug FROM Team t WHERE t.slug = :slug AND t.deleted = false")
                    .setParameter("slug", slug)
                    .getSingleResult();

            return Optional.of(new TeamInfo((Long) result[0], (String) result[1]));
        } catch (NoResultException e) {
            LOG.debugv("No team found for slug: {0}", slug);
            return Optional.empty();
        }
    }

    public record TeamInfo(Long id, String slug) {}
}
