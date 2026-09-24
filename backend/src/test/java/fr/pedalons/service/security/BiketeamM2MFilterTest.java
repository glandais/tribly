package fr.pedalons.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.pedalons.service.migration.live.BiketeamLiveMigrationConfig;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * The M2M gate, without Quarkus: the test context runs with the migration configured, so the
 * disabled case is checked here rather than through a restart under another profile.
 */
class BiketeamM2MFilterTest {

  private static BiketeamM2MFilter filter(boolean enabled) {
    BiketeamLiveMigrationConfig config = mock(BiketeamLiveMigrationConfig.class);
    when(config.isEnabled()).thenReturn(enabled);
    when(config.triggerSecret()).thenReturn("secret");
    BiketeamM2MFilter filter = new BiketeamM2MFilter();
    filter.config = config;
    return filter;
  }

  /** The response the filter aborted with, or {@code null} when it let the request through. */
  private static Response abortOf(BiketeamM2MFilter filter, String secret) {
    ContainerRequestContext ctx = mock(ContainerRequestContext.class);
    when(ctx.getHeaderString(BiketeamM2MFilter.SECRET_HEADER)).thenReturn(secret);
    filter.filter(ctx);
    ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
    verify(ctx, atMostOnce()).abortWith(captor.capture());
    return captor.getAllValues().isEmpty() ? null : captor.getValue();
  }

  @Test
  void disabled_isABare404_whateverTheSecret() {
    Response response = abortOf(filter(false), "secret");
    assertEquals(404, response.getStatus());
    assertNull(response.getEntity());
  }

  @Test
  void enabled_wrongOrMissingSecret_is401() {
    assertEquals(401, abortOf(filter(true), "nope").getStatus());
    assertEquals(401, abortOf(filter(true), null).getStatus());
  }

  @Test
  void enabled_rightSecret_passes() {
    assertNull(abortOf(filter(true), "secret"));
  }
}
