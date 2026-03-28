package fr.pedalons.infrastructure.brouter;

import java.util.Map;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;

public record ResultFeature(Map<String, Object> properties, LineString<G3D> geometry) {}
