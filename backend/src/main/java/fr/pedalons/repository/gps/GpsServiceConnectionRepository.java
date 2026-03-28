package fr.pedalons.repository.gps;

import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.enums.GpsServiceType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GpsServiceConnectionRepository implements PanacheRepository<GpsServiceConnection> {

  public Optional<GpsServiceConnection> findByUserAndService(
      Long userId, GpsServiceType serviceType) {
    return find("user.id = ?1 and serviceType = ?2", userId, serviceType).firstResultOptional();
  }

  public List<GpsServiceConnection> findByUser(Long userId) {
    return find("user.id = ?1", userId).list();
  }
}
