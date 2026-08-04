package fr.pedalons.infrastructure.nominatim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

/** The seam to OpenStreetMap: what it sends, and what it does when the provider misbehaves. */
@QuarkusTest
class NominatimLookupTest {

  @Inject NominatimLookup nominatimLookup;

  @InjectMock @RestClient NominatimClient nominatimClient;

  @Test
  void search_whenTheProviderFails_shouldReturnEmptyRatherThanPropagate() {
    // A geocoder assists a form field that also accepts a position entered by hand. An outage at
    // OpenStreetMap must not stop someone from saving their team's meeting point, so the failure
    // is swallowed here rather than surfacing as a 500 on a form.
    when(nominatimClient.search(anyString(), anyString(), anyInt(), any()))
        .thenThrow(new RuntimeException("upstream down"));

    assertTrue(nominatimLookup.search("chambery-outage", "fr").isEmpty());
  }

  @Test
  void normalize_shouldFoldCaseAndCollapseWhitespace() {
    // This is what makes the cache hit at all: an autocomplete sends every prefix of what is being
    // typed, and the policy asks us to cache. A key that told "Lyon " from "lyon" would not.
    assertEquals(
        "saint jean de maurienne", NominatimLookup.normalize("  Saint   Jean   de  Maurienne "));
    assertEquals(NominatimLookup.normalize("lyon"), NominatimLookup.normalize("LYON"));
  }

  @Test
  void search_shouldAskForTheFiveHitsTheAutocompleteShows() {
    when(nominatimClient.search(anyString(), anyString(), anyInt(), any())).thenReturn(List.of());

    nominatimLookup.search("annecy-limit", "fr");

    org.mockito.Mockito.verify(nominatimClient).search("annecy-limit", "jsonv2", 5, "fr");
  }
}
