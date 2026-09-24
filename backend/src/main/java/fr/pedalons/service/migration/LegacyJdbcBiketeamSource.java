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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * The legacy source: a restored biketeam dump, read through {@link BiketeamReader}, plus the data
 * directory copied from the biketeam server. Behaves exactly as the import did before the source
 * was abstracted: dates read in {@code Europe/Paris}, files found by listing their directory,
 * fingerprints digested from disk.
 *
 * @deprecated replaced by the live migration (docs/plans/2026-09-22-biketeam-live-migration.md)
 */
// REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — whole class: born deprecated, the dump import's source.
@Deprecated(forRemoval = true, since = "4.5.0")
public final class LegacyJdbcBiketeamSource implements BiketeamSource {

  private static final Logger LOG = Logger.getLogger(LegacyJdbcBiketeamSource.class);

  /**
   * The legacy import hardcoded Paris: 186 of the 187 teams of the 2026-07 dump are on it, and the
   * last one has no dated content (MIGRATE_BIKETEAM.md, "Timezones").
   */
  static final ZoneId PARIS = ZoneId.of("Europe/Paris");

  private final BiketeamReader reader;
  private final BtTeam team;
  private final String dataDir;

  private @Nullable List<BtRide> rides;
  private @Nullable List<BtTrip> trips;
  private @Nullable List<BtRideTemplate> rideTemplates;

  /**
   * @param dataDir root of the biketeam data export ({@code gpx/}, {@code *-images/}, {@code
   *     misc/}); blank skips every file, as the import always did
   */
  public LegacyJdbcBiketeamSource(BiketeamReader reader, BtTeam team, String dataDir) {
    this.reader = reader;
    this.team = team;
    this.dataDir = dataDir;
  }

  @Override
  public BtTeam team() {
    return team;
  }

  @Override
  public @Nullable BtTeamDescription teamDescription() {
    return reader.findTeamDescription(team.id());
  }

  @Override
  public @Nullable String teamMarkdownPage() {
    return reader.findTeamMarkdownPage(team.id());
  }

  @Override
  public ZoneId zone() {
    return PARIS;
  }

  @Override
  public List<BtPlace> places() {
    return reader.findPlaces(team.id());
  }

  @Override
  public List<BtMap> maps() {
    return reader.findMaps(team.id());
  }

  @Override
  public List<BtRideTemplate> rideTemplates() {
    if (rideTemplates == null) {
      rideTemplates = reader.findRideTemplates(team.id());
    }
    return rideTemplates;
  }

  @Override
  public List<BtRideGroupTemplate> rideGroupTemplates() {
    return reader.findRideGroupTemplates(rideTemplates().stream().map(BtRideTemplate::id).toList());
  }

  @Override
  public List<BtPublication> publications() {
    return reader.findPublications(team.id());
  }

  @Override
  public List<BtRide> rides() {
    if (rides == null) {
      rides = reader.findRides(team.id());
    }
    return rides;
  }

  @Override
  public List<BtRideGroup> rideGroups() {
    return reader.findRideGroups(rides().stream().map(BtRide::id).toList());
  }

  @Override
  public List<BtTrip> trips() {
    if (trips == null) {
      trips = reader.findTrips(team.id());
    }
    return trips;
  }

  @Override
  public List<BtTripStage> tripStages() {
    return reader.findTripStages(trips().stream().map(BtTrip::id).toList());
  }

  @Override
  public @Nullable SourceFile gpx(String mapId) {
    if (dataDir.isBlank()) {
      return null;
    }
    Path candidate = Path.of(dataDir, "gpx", team.id(), mapId + ".gpx");
    return Files.isRegularFile(candidate) ? new DiskFile(candidate) : null;
  }

  /** The first file of {@code {data-dir}/{kind dir}/{team}/} named after the entity, any extension. */
  @Override
  public @Nullable SourceFile image(ImageKind kind, String entityId) {
    if (dataDir.isBlank()) {
      return null;
    }
    Path found = findByBaseName(Path.of(dataDir, kind.legacyDirectory(), team.id()), entityId);
    return found == null ? null : new DiskFile(found);
  }

  /** {@code misc/<team>/logo.<ext>}, whatever the extension — and never {@code heatmap.png}. */
  @Override
  public @Nullable SourceFile logo() {
    if (dataDir.isBlank()) {
      return null;
    }
    Path found = findByBaseName(Path.of(dataDir, "misc", team.id()), "logo");
    return found == null ? null : new DiskFile(found);
  }

  @Override
  public void close() {
    // Nothing downloaded: every file is read in place.
  }

  private static @Nullable Path findByBaseName(Path dir, String baseName) {
    if (!Files.isDirectory(dir)) {
      return null;
    }
    try (Stream<Path> stream = Files.list(dir)) {
      return stream.filter(p -> baseName(p).equals(baseName)).findFirst().orElse(null);
    } catch (IOException e) {
      LOG.warnf(e, "Failed to list %s", dir);
      return null;
    }
  }

  private static String baseName(Path path) {
    String name = path.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot == -1 ? name : name.substring(0, dot);
  }

  /**
   * A file already on local disk. Its MD5 and size are computed once, in one streamed read, and
   * remembered: the placeholder-logo check, the up-to-date check and the fingerprint recorded after
   * the route would otherwise each read the whole file again.
   */
  private static final class DiskFile implements SourceFile {

    private final Path path;
    private boolean digested;
    private @Nullable String md5;
    private long size;

    DiskFile(Path path) {
      this.path = path;
    }

    @Override
    public String fileName() {
      return path.getFileName().toString();
    }

    /** Size and MD5 of the file, as read together. Null when it cannot be read. */
    @Override
    public synchronized @Nullable String fingerprint() {
      String digest = md5();
      if (digest == null) {
        LOG.warnf("Could not digest %s — it will be reprocessed on every replay", path);
        return null;
      }
      return size + ":" + digest;
    }

    @Override
    public synchronized @Nullable String md5() {
      if (!digested) {
        digested = true;
        try {
          MessageDigest md = MessageDigest.getInstance("MD5");
          try (InputStream in = new DigestInputStream(Files.newInputStream(path), md)) {
            size = in.transferTo(OutputStream.nullOutputStream());
          }
          md5 = HexFormat.of().formatHex(md.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
          md5 = null;
        }
      }
      return md5;
    }

    @Override
    public Path open() {
      return path;
    }
  }
}
