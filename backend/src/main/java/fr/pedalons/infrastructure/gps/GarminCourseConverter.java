package fr.pedalons.infrastructure.gps;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Converts GPX files to Garmin Connect course JSON format.
 */
@ApplicationScoped
public class GarminCourseConverter {

  /**
   * Convert GPX content to Garmin course JSON.
   *
   * @param gpxContent GPX file content
   * @param courseName Name for the course
   * @return JSON string for Garmin course API
   */
  public String convertToGarminCourse(byte[] gpxContent, String courseName) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(gpxContent));

      List<GeoPoint> geoPoints = new ArrayList<>();
      double totalDistance = 0;
      double elevationGain = 0;
      double elevationLoss = 0;
      double prevLat = 0, prevLon = 0, prevEle = 0;
      boolean first = true;

      // Try track points first (trk/trkseg/trkpt)
      NodeList trkpts = doc.getElementsByTagName("trkpt");
      if (trkpts.getLength() == 0) {
        // Fall back to route points (rte/rtept)
        trkpts = doc.getElementsByTagName("rtept");
      }

      for (int i = 0; i < trkpts.getLength(); i++) {
        Element pt = (Element) trkpts.item(i);
        double lat = Double.parseDouble(pt.getAttribute("lat"));
        double lon = Double.parseDouble(pt.getAttribute("lon"));

        // Get elevation if present
        Double ele = null;
        NodeList eleNodes = pt.getElementsByTagName("ele");
        if (eleNodes.getLength() > 0) {
          ele = Double.parseDouble(eleNodes.item(0).getTextContent());
        }

        geoPoints.add(new GeoPoint(lat, lon, ele));

        if (!first) {
          totalDistance += haversineDistance(prevLat, prevLon, lat, lon);
          if (ele != null && prevEle != 0) {
            double eleDiff = ele - prevEle;
            if (eleDiff > 0) {
              elevationGain += eleDiff;
            } else {
              elevationLoss += Math.abs(eleDiff);
            }
          }
        }

        prevLat = lat;
        prevLon = lon;
        prevEle = ele != null ? ele : 0;
        first = false;
      }

      return buildJsonCourse(courseName, totalDistance, elevationGain, elevationLoss, geoPoints);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }
  }

  private String buildJsonCourse(
      String courseName,
      double distance,
      double elevationGain,
      double elevationLoss,
      List<GeoPoint> geoPoints) {
    StringBuilder json = new StringBuilder();
    json.append("{");
    json.append("\"courseName\":").append(jsonString(courseName)).append(",");
    json.append("\"distance\":").append(Math.round(distance)).append(",");
    json.append("\"elevationGain\":").append(Math.round(elevationGain)).append(",");
    json.append("\"elevationLoss\":").append(Math.round(elevationLoss)).append(",");
    json.append("\"activityType\":\"ROAD_CYCLING\",");
    json.append("\"geoPoints\":[");

    for (int i = 0; i < geoPoints.size(); i++) {
      if (i > 0) json.append(",");
      GeoPoint p = geoPoints.get(i);
      json.append("{\"lat\":").append(p.lat).append(",\"lon\":").append(p.lon);
      if (p.altitude != null) {
        json.append(",\"altitude\":").append(p.altitude);
      }
      json.append("}");
    }

    json.append("]}");
    return json.toString();
  }

  private String jsonString(String value) {
    return "\""
        + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        + "\"";
  }

  /**
   * Calculate distance between two points using Haversine formula.
   *
   * @return Distance in meters
   */
  private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371000; // Earth radius in meters
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private record GeoPoint(double lat, double lon, Double altitude) {}
}
