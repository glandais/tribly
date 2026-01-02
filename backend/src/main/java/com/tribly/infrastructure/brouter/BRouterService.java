package com.tribly.infrastructure.brouter;

import com.tribly.dto.router.request.RouterRequest;
import com.tribly.dto.router.response.RouterResponse;
import com.tribly.infrastructure.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.geolatte.geom.Feature;
import org.geolatte.geom.G3D;
import org.geolatte.geom.LineString;
import org.geolatte.geom.json.GeoJsonFeatureCollection;

@ApplicationScoped
public class BRouterService {

  @Inject @RestClient BRouterClient bRouterClient;

  public RouterResponse getRoute(RouterRequest routerRequest) {
    String lonlats =
        routerRequest.from().lng()
            + ","
            + routerRequest.from().lat()
            + "|"
            + routerRequest.to().lng()
            + routerRequest.to().lat();
    GeoJsonFeatureCollection<G3D, ?> geojson =
        bRouterClient.route(lonlats, routerRequest.profile(), 0, "geojson");
    List<? extends Feature<G3D, ?>> features = geojson.getFeatures();
    if (features.isEmpty()) {
      throw BusinessException.conflict("No features found");
    }
    Feature<G3D, ?> feature = features.getFirst();
    if (feature.getGeometry() instanceof LineString<G3D> lineString) {
      double dist = (double) feature.getProperty("track-length");
      double ascend = (double) feature.getProperty("plain-ascend");
      return new RouterResponse(lineString, dist, ascend);
    } else {
      throw BusinessException.conflict("No features found");
    }
  }
}
