package fr.pedalons.service.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** The cache grid: what two nearby points share, and what is asked of the provider. */
class CellKeyTest {

  @Test
  void of_shouldRoundToTheNearestCellAndBand() {
    CellKey key = CellKey.of(45.7640, 4.8357, 173.0);

    assertEquals(915, key.latIdx()); // 45.764 / 0.05 = 915.28
    assertEquals(97, key.lonIdx()); // 4.8357 / 0.05 = 96.71
    assertEquals(200, key.eleBand());
    assertEquals(45.75, key.centerLat());
    assertEquals(4.85, key.centerLon());
  }

  @Test
  void of_withoutElevation_shouldHaveNoBand() {
    // The departure point: its altitude is unknown, the provider's own model decides.
    assertNull(CellKey.of(45.76, 4.84).eleBand());
    assertNull(CellKey.of(45.76, 4.84, Double.NaN).eleBand());
  }

  @Test
  void of_shouldIndexNegativeCoordinates() {
    assertEquals(0, CellKey.of(-0.02, -0.02).latIdx());
    assertEquals(-1, CellKey.of(-0.03, -0.03).lonIdx());
    assertEquals(-0.05, CellKey.of(-0.03, -0.03).centerLon());
  }

  @Test
  void sql_shouldSpellTheSameRounding() {
    assertEquals("cast(floor(st_y(?1) / 0.05 + 0.5) as integer)", CellKey.SQL_LAT_IDX);
    assertEquals("cast(floor(st_x(?1) / 0.05 + 0.5) as integer)", CellKey.SQL_LON_IDX);
  }
}
