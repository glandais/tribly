package fr.pedalons.infrastructure.valhalla;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ValhallaRequest(
    List<ValhallaLocation> locations,
    String costing,
    @JsonProperty("costing_options") Map<String, Map<String, Object>> costingOptions) {}
