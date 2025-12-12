package com.tribly.domain.ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements PanacheRepository<RideParticipation> {

    public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
        return find("user.id = ?1 and rideGroup.id = ?2 and deleted = false", userId, groupId)
                .firstResultOptional();
    }

    public Optional<RideParticipation> findByUserAndRide(Long userId, Long rideId) {
        return find("user.id = ?1 and rideGroup.ride.id = ?2 and deleted = false", userId, rideId)
                .firstResultOptional();
    }

    public List<RideParticipation> findByGroup(Long groupId) {
        return find("rideGroup.id = ?1 and deleted = false order by registeredAt", groupId).list();
    }

    public List<RideParticipation> findByRide(Long rideId) {
        return find("rideGroup.ride.id = ?1 and deleted = false order by registeredAt", rideId).list();
    }

    public List<RideParticipation> findByUser(Long userId) {
        return find("user.id = ?1 and deleted = false order by registeredAt desc", userId).list();
    }

    public long countByGroup(Long groupId) {
        return count("rideGroup.id = ?1 and deleted = false and status != ?2",
                groupId, ParticipationStatus.CANCELLED);
    }

    public long countByRide(Long rideId) {
        return count("rideGroup.ride.id = ?1 and deleted = false and status != ?2",
                rideId, ParticipationStatus.CANCELLED);
    }
}
