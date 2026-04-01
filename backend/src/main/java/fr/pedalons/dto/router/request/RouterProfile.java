package fr.pedalons.dto.router.request;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RouterProfile {
  BIKE("bicycle", Map.of("bicycle_type", "hybrid")),
  FASTBIKE("bicycle", Map.of("bicycle_type", "road", "use_roads", 1, "avoid_bad_surfaces", 1)),
  GRAVEL("bicycle", Map.of("bicycle_type", "cross")),
  MTB("bicycle", Map.of("bicycle_type", "mountain")),
  RUN_HIKE("pedestrian", Map.of()),
  MOTORCYCLE("motorcycle", Map.of());

  private final String costing;
  private final Map<String, Object> costingOptions;
}
