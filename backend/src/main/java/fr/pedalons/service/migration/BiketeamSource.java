package fr.pedalons.service.migration;

import fr.pedalons.service.migration.BiketeamModel.BtMap;
import fr.pedalons.service.migration.BiketeamModel.BtPlace;
import fr.pedalons.service.migration.BiketeamModel.BtPublication;
import fr.pedalons.service.migration.BiketeamModel.BtRide;
import fr.pedalons.service.migration.BiketeamModel.BtRideGroup;
import fr.pedalons.service.migration.BiketeamModel.BtRideGroupTemplate;
import fr.pedalons.service.migration.BiketeamModel.BtRideTemplate;
import fr.pedalons.service.migration.BiketeamModel.BtTeam;
import fr.pedalons.service.migration.BiketeamModel.BtTeamDescription;
import fr.pedalons.service.migration.BiketeamModel.BtTrip;
import fr.pedalons.service.migration.BiketeamModel.BtTripStage;
import java.time.ZoneId;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Where the content of one biketeam team comes from. Two implementations: the live export snapshot
 * fetched over HTTPS ({@code SnapshotBiketeamSource}) and, until it is removed, the restored dump
 * plus data directory of the legacy import (REMOVE-WITH-LEGACY-BIKETEAM-IMPORT: drop this mention).
 *
 * <p>Child lists come <em>already ordered</em> the way biketeam displayed them — ride groups by
 * meeting time then name, trip stages by date then name, template groups by name alone — because
 * the mapping turns the position into Pédalons' {@code sortOrder}. See MIGRATE_BIKETEAM.md,
 * "Ordering of groups and stages".
 */
public interface BiketeamSource extends AutoCloseable {

  /** The three kinds of per-entity images biketeam stored, with their legacy directory names. */
  enum ImageKind {
    RIDE("ride-images"),
    TRIP("trip-images"),
    PUBLICATION("pub-images");

    private final String legacyDirectory;

    ImageKind(String legacyDirectory) {
      this.legacyDirectory = legacyDirectory;
    }

    /**
     * The directory of the legacy data export. Also the prefix of the {@code ASSET} mapping key,
     * which is why the live path keeps using it: an image the legacy import already uploaded is then
     * recognised and not uploaded twice.
     */
    public String legacyDirectory() {
      return legacyDirectory;
    }
  }

  BtTeam team();

  @Nullable BtTeamDescription teamDescription();

  /** {@code team_configuration.markdown_page}, the team's FAQ, or null. */
  @Nullable String teamMarkdownPage();

  /** The zone biketeam's bare dates and times are read in. */
  ZoneId zone();

  List<BtPlace> places();

  /** Every map of the team, deleted ones included. */
  List<BtMap> maps();

  List<BtRideTemplate> rideTemplates();

  /** Grouped by template, each group in biketeam's display order. */
  List<BtRideGroupTemplate> rideGroupTemplates();

  /** Every publication, deleted ones included. */
  List<BtPublication> publications();

  /** Every ride, deleted ones included. */
  List<BtRide> rides();

  /** Grouped by ride, each group in biketeam's display order. */
  List<BtRideGroup> rideGroups();

  /** Every trip, deleted ones included. */
  List<BtTrip> trips();

  /** Grouped by trip, each group in biketeam's display order. */
  List<BtTripStage> tripStages();

  /** The GPX of a map, or null when biketeam has none. */
  @Nullable SourceFile gpx(String mapId);

  /** The image of a ride, trip or publication, or null when it has none. */
  @Nullable SourceFile image(ImageKind kind, String entityId);

  /** {@code misc/<teamId>/logo.*}, placeholder included — the mapping recognises it — or null. */
  @Nullable SourceFile logo();

  /** Releases whatever the source downloaded. Never throws. */
  @Override
  void close();
}
