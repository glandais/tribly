package fr.pedalons.infrastructure.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * One Nominatim hit, reduced to what the geocoder autocomplete shows and returns.
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} is not laziness here: the response carries
 * a couple of dozen fields we have no use for, and their set changes between Nominatim releases.
 * Coordinates come back as strings, which is why {@link #lat()} and {@link #lon()} are typed as
 * such and parsed by the service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimPlace(
    @JsonProperty("place_id") @Nullable Long placeId,
    @JsonProperty("display_name") @Nullable String displayName,
    @Nullable String lat,
    @Nullable String lon) {}
