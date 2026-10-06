package fr.pedalons.service.weather;

/** How many metres of a leg ride against, across and with the wind. */
public record WindExposure(double head, double cross, double tail) {

  public static final WindExposure NONE = new WindExposure(0, 0, 0);
}
