package fr.pedalons.enums;

/** An eight-point compass rose. For the wind: the direction it comes <em>from</em>. */
public enum CompassPoint {
  N,
  NE,
  E,
  SE,
  S,
  SW,
  W,
  NW;

  /** The point nearest {@code degrees} (clockwise from north, any value). */
  public static CompassPoint fromDegrees(double degrees) {
    double normalized = ((degrees % 360) + 360) % 360;
    return values()[(int) Math.round(normalized / 45) % 8];
  }
}
