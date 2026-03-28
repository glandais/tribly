package fr.pedalons.dto.publications.response;

import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.trip.Trip;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PublicationType {
  RIDE(Ride.class),
  POST(Post.class),
  TRIP(Trip.class);

  final Class<?> type;
}
