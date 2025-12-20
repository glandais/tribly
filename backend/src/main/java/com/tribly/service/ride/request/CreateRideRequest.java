package com.tribly.service.ride.request;

import com.tribly.enums.Visibility;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record CreateRideRequest(
    String title,
    @Nullable String description,
    LocalDate date,
    @Nullable LocalTime startTime,
    @Nullable Visibility visibility,
    @Nullable Long routeId,
    @Nullable Long meetingPointId,
    @Nullable Instant publishAt,
    @Nullable List<CreateRideGroupRequest> groups) {}
