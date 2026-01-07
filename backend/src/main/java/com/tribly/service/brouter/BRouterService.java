package com.tribly.service.brouter;

import com.tribly.common.exception.BusinessException;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.router.request.RouterRequest;
import com.tribly.dto.router.response.RouterResponse;
import com.tribly.infrastructure.brouter.BRouterClient;
import com.tribly.infrastructure.brouter.ResultFeature;
import com.tribly.infrastructure.brouter.RouterResult;
import com.tribly.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class BRouterService {

  @Inject @RestClient BRouterClient bRouterClient;

  @Logged
  public RouterResponse getRoute(RouterRequest routerRequest) {
    String lonlats =
        routerRequest.from().lng()
            + ","
            + routerRequest.from().lat()
            + "|"
            + routerRequest.to().lng()
            + ","
            + routerRequest.to().lat();
    RouterResult geojson =
        bRouterClient.route(lonlats, routerRequest.profile().getProfileName(), 0, "geojson");
    List<ResultFeature> features = geojson.features();
    if (features.isEmpty()) {
      throw new BusinessException(ErrorCode.UNKNOWN);
    }
    ResultFeature feature = features.getFirst();
    Map<String, Object> properties = feature.properties();
    double dist = getDouble(properties, "track-length");
    double ascend = getDouble(properties, "plain-ascend");
    return new RouterResponse(feature.geometry(), dist, ascend);
  }

  private double getDouble(Map<String, Object> properties, String name) {
    Object value = properties.get(name);
    return switch (value) {
      case String string -> Double.parseDouble(string);
      case Number number -> number.doubleValue();
      case null, default -> 0.0;
    };
  }
}
