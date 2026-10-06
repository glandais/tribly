package fr.pedalons.service.weather;

import fr.pedalons.enums.CompassPoint;
import org.jspecify.annotations.Nullable;

/**
 * Wind at a place and an hour.
 *
 * @param speed km/h, at 10 m
 * @param gusts km/h; null when the model has none
 * @param direction degrees clockwise from north the wind comes <em>from</em>
 * @param compass {@code direction} on the eight-point rose
 */
public record Wind(double speed, @Nullable Double gusts, double direction, CompassPoint compass) {}
