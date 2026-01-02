package com.tribly.dto.router.request;

import com.tribly.common.GeoPoint;

public record RouterRequest(GeoPoint from, GeoPoint to, String profile) {}
