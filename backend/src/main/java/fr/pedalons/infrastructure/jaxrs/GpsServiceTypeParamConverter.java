package fr.pedalons.infrastructure.jaxrs;

import fr.pedalons.enums.GpsServiceType;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class GpsServiceTypeParamConverter implements ParamConverterProvider {

  @Override
  @SuppressWarnings("unchecked")
  public <T> ParamConverter<T> getConverter(
      Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (rawType == GpsServiceType.class) {
      return (ParamConverter<T>)
          new ParamConverter<GpsServiceType>() {
            @Override
            public GpsServiceType fromString(String value) {
              if (value == null || value.isBlank()) {
                return null;
              }
              return GpsServiceType.valueOf(value.toUpperCase());
            }

            @Override
            public String toString(GpsServiceType value) {
              return value == null ? null : value.name().toLowerCase();
            }
          };
    }
    return null;
  }
}
