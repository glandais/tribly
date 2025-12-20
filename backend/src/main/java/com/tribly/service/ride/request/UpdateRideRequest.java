package com.tribly.service.ride.request;

import com.tribly.enums.RideStatus;
import com.tribly.enums.Visibility;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.jspecify.annotations.Nullable;

public record UpdateRideRequest(
    @Nullable String title,
    @Nullable String description,
    @Nullable LocalDate date,
    @Nullable LocalTime startTime,
    @Nullable RideStatus status,
    @Nullable Visibility visibility,
    @Nullable Long routeId,
    @Nullable Long meetingPointId,
    @Nullable Instant publishAt) {}
