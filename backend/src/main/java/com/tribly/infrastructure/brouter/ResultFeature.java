package com.tribly.infrastructure.brouter;

import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;

import java.util.Map;

public record ResultFeature(Map<String, Object> properties, LineString<G3D> geometry) {
}
