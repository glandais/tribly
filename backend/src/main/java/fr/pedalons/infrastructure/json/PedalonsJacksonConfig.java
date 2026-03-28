package fr.pedalons.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import org.geolatte.geom.json.GeolatteGeomModule;

@Singleton
public class PedalonsJacksonConfig implements ObjectMapperCustomizer {
  @Override
  public void customize(ObjectMapper objectMapper) {
    objectMapper.registerModule(new GeolatteGeomModule());
    objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
  }
}
