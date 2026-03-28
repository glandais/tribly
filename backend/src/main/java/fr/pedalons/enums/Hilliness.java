package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public enum Hilliness {
  FLAT(null, 8),
  HILLY(8, 15),
  MOUNTAINOUS(15, null);

  final @Nullable Integer minMetersPerKm;
  final @Nullable Integer maxMetersPerKm;
}
