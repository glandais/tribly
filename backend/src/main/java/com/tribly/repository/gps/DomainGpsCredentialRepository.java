package com.tribly.repository.gps;

import com.tribly.domain.gps.DomainGpsCredential;
import com.tribly.enums.GpsServiceType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DomainGpsCredentialRepository implements PanacheRepository<DomainGpsCredential> {

  /**
   * Find active credentials for a specific domain and service type.
   *
   * @param domainId The domain ID
   * @param serviceType The GPS service type
   * @return Optional containing the credentials if found and active
   */
  public Optional<DomainGpsCredential> findByDomainAndServiceType(
      Long domainId, GpsServiceType serviceType) {
    return find("domain.id = ?1 and serviceType = ?2 and active = true", domainId, serviceType)
        .firstResultOptional();
  }

  /**
   * Find all active credentials for a domain.
   *
   * @param domainId The domain ID
   * @return List of active GPS service credentials
   */
  public List<DomainGpsCredential> findActiveByDomain(Long domainId) {
    return list("domain.id = ?1 and active = true", domainId);
  }

  /**
   * Get all service types that have active credentials for a domain.
   *
   * @param domainId The domain ID
   * @return List of GPS service types with configured credentials
   */
  public List<GpsServiceType> findAvailableServiceTypes(Long domainId) {
    return getEntityManager()
        .createQuery(
            "SELECT d.serviceType FROM DomainGpsCredential d "
                + "WHERE d.domain.id = :domainId AND d.active = true",
            GpsServiceType.class)
        .setParameter("domainId", domainId)
        .getResultList();
  }
}
