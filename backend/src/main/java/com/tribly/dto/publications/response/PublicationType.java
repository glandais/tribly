package com.tribly.dto.publications.response;

import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.trip.Trip;
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
