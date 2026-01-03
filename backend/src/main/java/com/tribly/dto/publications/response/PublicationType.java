package com.tribly.dto.publications.response;

import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.trip.Trip;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PublicationType {
  RIDE(Ride.class.getCanonicalName()),
  POST(Post.class.getCanonicalName()),
  TRIP(Trip.class.getCanonicalName());

  final String typeName;
}
