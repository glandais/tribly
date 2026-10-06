package fr.pedalons.enums;

/**
 * The wind as the rider feels it on a stretch of road: mostly against, across, or behind. Its colour
 * lives in {@code contracts/brand-colors.yaml}, always doubled by a label and an arrow.
 */
public enum RelativeWind {
  /** Head component above half the wind speed. */
  HEAD,
  CROSS,
  /** Head component below minus half the wind speed. */
  TAIL
}
