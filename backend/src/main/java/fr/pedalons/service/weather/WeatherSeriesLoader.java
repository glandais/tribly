package fr.pedalons.service.weather;

import fr.pedalons.domain.weather.WeatherCell;
import fr.pedalons.repository.weather.WeatherCellRepository;
import fr.pedalons.repository.weather.WeatherHourRow;
import fr.pedalons.repository.weather.WeatherHourlyRepository;
import fr.pedalons.service.weather.RideWeatherCalculator.CellSeries;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reads the cached forecast of a set of cells over a window, for the detail reads of a ride and of
 * a trip: one query for the cells, one for their hours with sunrise and sunset — whatever the number
 * of cells. Reads only.
 */
@ApplicationScoped
public class WeatherSeriesLoader {

  /** Hours read around the window, so the hour nearest each end is in hand. */
  static final Duration SLACK = Duration.ofMinutes(90);

  @Inject WeatherCellRepository cellRepository;
  @Inject WeatherHourlyRepository hourlyRepository;

  /** The series of each of {@code keys} that was ever created; a key never seen is absent. */
  public Map<CellKey, CellSeries> load(Set<CellKey> keys, Instant from, Instant to) {
    if (keys.isEmpty()) {
      return Map.of();
    }
    Set<Integer> lats = new HashSet<>();
    Set<Integer> lons = new HashSet<>();
    for (CellKey key : keys) {
      lats.add(key.latIdx());
      lons.add(key.lonIdx());
    }
    Map<Long, WeatherCell> cellsById = new HashMap<>();
    Map<Long, CellKey> keyById = new HashMap<>();
    for (WeatherCell cell : cellRepository.findByIndexes(lats, lons)) {
      CellKey key = new CellKey(cell.getLatIdx(), cell.getLonIdx(), cell.getEleBand());
      if (keys.contains(key)) {
        cellsById.put(cell.getId(), cell);
        keyById.put(cell.getId(), key);
      }
    }
    if (cellsById.isEmpty()) {
      return Map.of();
    }
    Map<Long, List<WeatherHourRow>> rows = new HashMap<>();
    for (WeatherHourRow row :
        hourlyRepository.findHours(cellsById.keySet(), from.minus(SLACK), to.plus(SLACK))) {
      rows.computeIfAbsent(row.ownerId(), k -> new ArrayList<>()).add(row);
    }
    Map<CellKey, CellSeries> cache = new HashMap<>();
    for (Map.Entry<Long, WeatherCell> entry : cellsById.entrySet()) {
      cache.put(
          keyById.get(entry.getKey()),
          CellSeries.of(
              entry.getValue().getFetchedAt(), rows.getOrDefault(entry.getKey(), List.of())));
    }
    return cache;
  }
}
