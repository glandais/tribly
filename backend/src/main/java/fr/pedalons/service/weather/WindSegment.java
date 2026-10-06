package fr.pedalons.service.weather;

import fr.pedalons.enums.RelativeWind;

/**
 * The wind on the stretch between two checkpoints, as the rider meets it.
 *
 * @param fromDistance metres from the start
 * @param headwind km/h, signed: positive against the rider
 */
public record WindSegment(
    double fromDistance, double toDistance, RelativeWind relativeWind, double headwind) {}
