package fr.pedalons.infrastructure.valhalla;

import java.util.List;
import org.jspecify.annotations.Nullable;

public record ValhallaResponse(@Nullable ValhallaTrip trip) {

  public record ValhallaTrip(List<ValhallaLeg> legs) {}

  public record ValhallaLeg(@Nullable String shape) {}
}
