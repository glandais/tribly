package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WindDirection {
  NORTH(0),
  NORTH_EAST(45),
  EAST(90),
  SOUTH_EAST(135),
  SOUTH(180),
  SOUTH_WEST(225),
  WEST(270),
  NORTH_WEST(315);

  final int angle;
}
