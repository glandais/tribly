package fr.pedalons.api.notifications;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.util.NotificationTestData;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the inbox. A row renders from the event snapshot, join-fetched with the
 * page; if someone starts looking rides or teams up per row, this is what says so.
 *
 * <p>See {@link AbstractQueryCountTest} for why the assertion is a shape rather than a bound.
 */
@QuarkusTest
class NotificationQueryCountTest extends AbstractQueryCountTest {

  @Inject NotificationTestData notifications;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  void listMyNotifications_costDoesNotScaleWithRowCount() {
    notifications.seedInbox(user1, team1, LARGE_PAGE);
    assertFlatQueryCount("GET /api/notifications", asUser1(), "/api/notifications");
  }
}
