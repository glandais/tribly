package fr.pedalons.service.gpx;

import com.garmin.fit.CoursePointMesg;
import com.garmin.fit.CoursePointMesgListener;
import com.garmin.fit.Decode;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.RecordMesgListener;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import fr.pedalons.service.route.GpxSanitizer;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.GPXWaypoint;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.io.read.GPXFileReader;
import io.github.glandais.gpx.io.write.FitFileWriter;
import io.github.glandais.gpx.io.write.GPXFileWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Cleans a GPX or FIT file attached to content as a plain attachment, the way a route's files are
 * cleaned at import: only the tracks (position, altitude) and the waypoints (position, name)
 * survive (docs/LEDGER_*.md API-49 for GPX, API-55 for FIT, after API-44).
 *
 * <p>An attachment is a file offered for download, not a route: nothing is resampled, simplified or
 * re-elevated, the track keeps every point it had. What goes is what {@link GpxSanitizer} drops —
 * timestamps, heart rate, cadence, power, temperature — and what the readers never keep (author,
 * e-mail, device, descriptions).
 *
 * <p><b>A FIT comes back as a FIT course.</b> An activity recorded by a device is decoded (Garmin
 * SDK), reduced to its positioned records and course points, and written again by the writer that
 * produces every route's {@code route.fit}: laps, sessions, device information, HRV and every
 * other message of the activity are gone. The file still loads on a GPS device, as a course to
 * follow.
 */
@ApplicationScoped
public class TrackAttachmentSanitizer {

  /** The content type {@code FileTypeDetector} gives a {@code .gpx} file. */
  public static final String GPX_CONTENT_TYPE = "application/gpx+xml";

  /** The content type {@code FileTypeDetector} gives a {@code .fit} file. */
  public static final String FIT_CONTENT_TYPE = "application/vnd.ant.fit";

  /** FIT positions are in semicircles: 2^31 of them make 180 degrees. */
  private static final double SEMICIRCLES_TO_RADIANS = Math.PI / 2_147_483_648.0;

  @Inject GPXFileReader gpxFileReader;

  @Inject GPXFileWriter gpxFileWriter;

  @Inject FitFileWriter fitFileWriter;

  /** Whether an asset of this type and content type is a track file to clean before storing. */
  public static boolean applies(AssetType type, String contentType) {
    return type == AssetType.ATTACHMENT
        && (GPX_CONTENT_TYPE.equals(contentType) || FIT_CONTENT_TYPE.equals(contentType));
  }

  /**
   * The cleaned serialization of {@code raw}, in the same format.
   *
   * @param contentType {@link #GPX_CONTENT_TYPE} or {@link #FIT_CONTENT_TYPE}
   * @throws BusinessException {@link ErrorCode#GPX_FAILURE} when the file is not a readable GPX or
   *     FIT, {@link ErrorCode#GPX_EMPTY} when it holds neither a positioned point nor a waypoint —
   *     storing the rewrite would then keep nothing of what the member attached
   */
  public byte[] sanitize(byte[] raw, String contentType) {
    if (FIT_CONTENT_TYPE.equals(contentType) && isCleanFit(raw)) {
      // Already a course of ours: rewriting it would only change its creation time.
      return raw;
    }
    boolean fit = FIT_CONTENT_TYPE.equals(contentType);
    GPX gpx = fit ? readFit(raw) : readGpx(raw);
    if (gpx.paths().isEmpty() && gpx.waypoints().isEmpty()) {
      throw new BusinessException(ErrorCode.GPX_EMPTY);
    }
    GpxSanitizer.sanitize(gpx);
    try {
      return fit ? writeFit(gpx) : writeGpx(gpx);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  /**
   * Whether {@code raw} needs no rewrite: {@link #sanitize} would give it back as it is. A GPX is
   * compared with its cleaned serialization, which is deterministic. A FIT is not — the writer
   * stamps the file with its creation time — so it is clean when it is a course our writer
   * produced, holding only the messages it writes, with no clock or sensor value on any record.
   */
  public boolean isClean(byte[] raw, String contentType) {
    if (FIT_CONTENT_TYPE.equals(contentType)) {
      return isCleanFit(raw);
    }
    return Arrays.equals(raw, sanitize(raw, contentType));
  }

  /** The messages {@link FitFileWriter} writes; any other one comes from a device or an app. */
  private static final Set<Integer> COURSE_MESSAGES =
      Set.of(
          MesgNum.FILE_ID,
          MesgNum.COURSE,
          MesgNum.LAP,
          MesgNum.EVENT,
          MesgNum.RECORD,
          MesgNum.COURSE_POINT);

  private boolean isCleanFit(byte[] raw) {
    List<Mesg> messages = new ArrayList<>();
    try {
      Decode decode = new Decode();
      if (!decode.read(new ByteArrayInputStream(raw), messages::add)) {
        return false;
      }
    } catch (FitRuntimeException e) {
      return false;
    }
    FileIdMesg ours = writerFileId();
    boolean fileIdSeen = false;
    for (Mesg mesg : messages) {
      if (!COURSE_MESSAGES.contains(mesg.getNum())) {
        return false;
      }
      if (mesg.getNum() == MesgNum.FILE_ID) {
        FileIdMesg fileId = new FileIdMesg(mesg);
        fileIdSeen = true;
        if (fileId.getType() != com.garmin.fit.File.COURSE
            || !Objects.equals(fileId.getManufacturer(), ours.getManufacturer())
            || !Objects.equals(fileId.getProduct(), ours.getProduct())
            || !Objects.equals(fileId.getSerialNumber(), ours.getSerialNumber())) {
          return false;
        }
      }
      if (mesg.getNum() == MesgNum.RECORD) {
        RecordMesg record = new RecordMesg(mesg);
        if (record.getTimestamp() != null
            || record.getHeartRate() != null
            || record.getPower() != null
            || record.getCadence() != null
            || record.getTemperature() != null) {
          return false;
        }
      }
    }
    return fileIdSeen;
  }

  private volatile @Nullable FileIdMesg writerFileId;

  /** The {@code file_id} our writer puts on every course, read once from a one-point course. */
  private FileIdMesg writerFileId() {
    FileIdMesg known = writerFileId;
    if (known != null) {
      return known;
    }
    GPXPath path = new GPXPath("probe", GPXPathType.TRACK);
    Point point = point(0, 0);
    point.setEle(0.0);
    path.addPoint(point);
    path.computeArrays();
    List<Mesg> messages = new ArrayList<>();
    try {
      new Decode()
          .read(
              new ByteArrayInputStream(writeFit(new GPX("probe", List.of(path), List.of()))),
              messages::add);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    FileIdMesg fileId =
        messages.stream()
            .filter(m -> m.getNum() == MesgNum.FILE_ID)
            .findFirst()
            .map(FileIdMesg::new)
            .orElseThrow(() -> new IllegalStateException("The FIT writer wrote no file_id"));
    writerFileId = fileId;
    return fileId;
  }

  private GPX readGpx(byte[] raw) {
    try {
      return gpxFileReader.parseGPX(new ByteArrayInputStream(raw));
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  /**
   * The positioned records of a FIT file as one track, and its course points as waypoints. Nothing
   * else of the file is read: what the result is built from is exactly what the course keeps.
   */
  private static GPX readFit(byte[] raw) {
    Decode decode = new Decode();
    MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
    List<RecordMesg> records = new ArrayList<>();
    List<CoursePointMesg> coursePoints = new ArrayList<>();
    broadcaster.addListener((RecordMesgListener) records::add);
    broadcaster.addListener((CoursePointMesgListener) coursePoints::add);
    try {
      if (!decode.read(new ByteArrayInputStream(raw), broadcaster, broadcaster)) {
        throw new BusinessException(ErrorCode.GPX_FAILURE);
      }
    } catch (FitRuntimeException e) {
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }

    GPXPath path = new GPXPath("track", GPXPathType.TRACK);
    for (RecordMesg record : records) {
      Point point = point(record.getPositionLat(), record.getPositionLong());
      if (point == null) {
        continue;
      }
      Float altitude =
          record.getEnhancedAltitude() != null
              ? record.getEnhancedAltitude()
              : record.getAltitude();
      point.setEle(altitude != null ? altitude : 0.0);
      path.addPoint(point);
    }
    List<GPXPath> paths = new ArrayList<>();
    if (!path.getPoints().isEmpty()) {
      path.computeArrays();
      paths.add(path);
    }
    List<GPXWaypoint> waypoints = new ArrayList<>();
    for (CoursePointMesg coursePoint : coursePoints) {
      Point point = point(coursePoint.getPositionLat(), coursePoint.getPositionLong());
      if (point != null) {
        String name = coursePoint.getName();
        waypoints.add(new GPXWaypoint(name != null ? name : "", point));
      }
    }
    return new GPX("track", paths, waypoints);
  }

  private static @Nullable Point point(@Nullable Integer lat, @Nullable Integer lon) {
    if (lat == null || lon == null) {
      return null;
    }
    return point((int) lat, (int) lon);
  }

  private static Point point(int lat, int lon) {
    Point point = new Point();
    point.setLat(lat * SEMICIRCLES_TO_RADIANS);
    point.setLon(lon * SEMICIRCLES_TO_RADIANS);
    point.setInstant(null, Instant.EPOCH);
    return point;
  }

  private byte[] writeGpx(GPX gpx) throws IOException {
    StringWriter writer = new StringWriter();
    gpxFileWriter.writeGPX(gpx, writer, false);
    return writer.toString().getBytes(StandardCharsets.UTF_8);
  }

  private byte[] writeFit(GPX gpx) throws IOException {
    Path temp = Files.createTempFile("attachment-", ".fit");
    try {
      fitFileWriter.writeGPX(gpx, temp.toFile());
      return Files.readAllBytes(temp);
    } finally {
      Files.deleteIfExists(temp);
    }
  }
}
