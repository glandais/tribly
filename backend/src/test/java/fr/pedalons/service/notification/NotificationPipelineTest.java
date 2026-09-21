package fr.pedalons.service.notification;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.notification.NotificationDelivery;
import fr.pedalons.domain.notification.NotificationEventEntry;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.notifications.request.NotificationPreferenceUpdate;
import fr.pedalons.dto.notifications.request.NotificationPreferencesRequest;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationDeliveryStatus;
import fr.pedalons.enums.NotificationEventStatus;
import fr.pedalons.enums.NotificationType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.ride.RideService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.NotificationTestData;
import fr.pedalons.util.NotificationTestData.EventView;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The pipeline end to end: a change made through the API, the dispatcher run by hand (the scheduler
 * is off in tests), then the channel senders.
 *
 * <p>Fixture: team1 has user1 (admin), user2 (organizer) and user3 (member); user4 and user5 are
 * outsiders.
 */
@QuarkusTest
class NotificationPipelineTest extends AbstractResourceTest {

  @Inject NotificationDispatchService dispatchService;
  @Inject NotificationDeliveryService deliveryService;
  @Inject NotificationRetentionService retentionService;
  @Inject NotificationTestData notifications;
  @Inject MockMailbox mailbox;
  @Inject NotificationPublisher publisher;
  @Inject RideService rideService;
  @Inject PedalonsQueryContext pedalonsContext;
  @Inject DomainResolver domainResolver;

  private final Instant nextWeek = Instant.now().plus(7, ChronoUnit.DAYS);

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    mailbox.clear();
  }

  private void drain() {
    while (dispatchService.dispatchOne()) {
      // fan out everything queued
    }
  }

  private RideRequest ride(Status status, Instant dateTime, @Nullable String groupId) {
    return new RideRequest(
        "Sortie du dimanche",
        MediaDto.builder().build(),
        dateTime,
        status,
        Visibility.PUBLIC,
        null,
        null,
        null,
        null,
        List.of(new GroupRequest(groupId, "G1", null, null, null, null)));
  }

  private String createRide(Status status, Instant dateTime) {
    return given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(ride(status, dateTime, null))
        .when()
        .post("/api/teams/" + team1Slug + "/rides")
        .then()
        .statusCode(201)
        .extract()
        .path("slug");
  }

  private void updateRide(String slug, RideRequest request) {
    given()
        .auth()
        .oauth2(getAccessToken(USER1))
        .contentType("application/json")
        .body(request)
        .when()
        .put("/api/teams/" + team1Slug + "/rides/" + slug)
        .then()
        .statusCode(200);
  }

  // ------------------------------------------------------------------ publication

  @Test
  void publishedRide_notifiesTeamMembers_butNotItsAuthor() {
    createRide(Status.PUBLISHED, nextWeek);
    drain();

    assertEquals(
        List.of(NotificationType.RIDE_PUBLISHED), notifications.notificationTypesFor(user2));
    assertEquals(
        List.of(NotificationType.RIDE_PUBLISHED), notifications.notificationTypesFor(user3));
    assertTrue(notifications.notificationTypesFor(user1).isEmpty(), "the author");
    assertTrue(notifications.notificationTypesFor(user4).isEmpty(), "not a member");
  }

  @Test
  void draftRide_notifiesNobody_untilPublished() {
    String slug = createRide(Status.DRAFT, nextWeek);
    drain();
    assertTrue(notifications.events().isEmpty());

    updateRide(slug, ride(Status.PUBLISHED, nextWeek, null));
    drain();
    assertEquals(1, notifications.notificationTypesFor(user3).size());
  }

  @Test
  void rideRepublished_isAnnouncedOnce() {
    String slug = createRide(Status.PUBLISHED, nextWeek);
    updateRide(slug, ride(Status.DRAFT, nextWeek, null));
    updateRide(slug, ride(Status.PUBLISHED, nextWeek, null));
    drain();

    assertEquals(1, notifications.events().size());
    assertEquals(1, notifications.notificationTypesFor(user3).size());
  }

  @Test
  void pastRide_isSkipped() {
    createRide(Status.PUBLISHED, Instant.now().minus(1, ChronoUnit.DAYS));
    drain();

    List<EventView> events = notifications.events();
    assertEquals(1, events.size());
    assertEquals(NotificationEventStatus.SKIPPED, events.getFirst().status());
    assertEquals(0, notifications.notificationCount());
  }

  @Test
  void rideUnpublishedBeforeDispatch_isSkipped() {
    String slug = createRide(Status.PUBLISHED, nextWeek);
    updateRide(slug, ride(Status.DRAFT, nextWeek, null));
    drain();

    assertEquals(NotificationEventStatus.SKIPPED, notifications.events().getFirst().status());
    assertEquals(0, notifications.notificationCount());
  }

  /** A skipped event notified nobody, so it must not swallow the real publication after it. */
  @Test
  void rideUnpublishedBeforeDispatch_isAnnouncedWhenPublishedAgain() {
    String slug = createRide(Status.PUBLISHED, nextWeek);
    updateRide(slug, ride(Status.DRAFT, nextWeek, null));
    drain();
    assertEquals(0, notifications.notificationCount());

    updateRide(slug, ride(Status.PUBLISHED, nextWeek, null));
    drain();

    assertEquals(
        List.of(NotificationType.RIDE_PUBLISHED), notifications.notificationTypesFor(user3));
  }

  /** What the biketeam migration relies on: history replayed through the services is not news. */
  @Test
  void silenced_queuesNothing() {
    domainResolver.setDomainForTest(domain);
    pedalonsContext.setUserForTest(user1);
    publisher.silently(
        () -> rideService.createRide(team1Slug, ride(Status.PUBLISHED, nextWeek, null)));

    assertTrue(notifications.events().isEmpty());
  }

  // ------------------------------------------------------------------ cancellation

  /** A published ride next week, user3 registered to its only group. */
  private record RegisteredRide(Ride ride, RideGroup group) {}

  private RegisteredRide registeredRide() {
    Ride ride = dataService.createRide(team1, user1, "Sortie", "sortie", nextWeek);
    RideGroup group = dataService.createRideGroup(user1, ride, "G1");
    dataService.createParticipation(group, user3);
    return new RegisteredRide(ride, group);
  }

  /** The group is sent back by id: a group left out of the request is deleted, registrations too. */
  private void cancel(RegisteredRide registered) {
    updateRide(
        registered.ride().getSlug(),
        ride(Status.CANCELLED, nextWeek, TsidUtils.toString(registered.group().getId())));
  }

  @Test
  void cancelledRide_notifiesRegisteredRidersOnly_byEmailToo() {
    cancel(registeredRide());
    drain();

    assertEquals(
        List.of(NotificationType.RIDE_CANCELLED), notifications.notificationTypesFor(user3));
    assertTrue(notifications.notificationTypesFor(user2).isEmpty(), "not registered");

    List<NotificationDelivery> deliveries = notifications.deliveriesFor(user3);
    assertEquals(1, deliveries.size());
    assertEquals(NotificationChannel.EMAIL, deliveries.getFirst().getChannel());

    deliveryService.sendDue(NotificationChannel.EMAIL);

    assertEquals(
        NotificationDeliveryStatus.SENT, notifications.deliveriesFor(user3).getFirst().getStatus());
    List<Mail> mails = mailbox.getMailsSentTo(EMAIL3);
    assertEquals(1, mails.size());
    assertTrue(mails.getFirst().getSubject().contains("Sortie"), mails.getFirst().getSubject());
    assertTrue(
        mails.getFirst().getText().contains("/teams/" + team1Slug + "/rides/sortie"),
        mails.getFirst().getText());
  }

  @Test
  void emailSwitchedOff_createsNoDelivery_butStillReachesTheInbox() {
    given()
        .auth()
        .oauth2(getAccessToken(USER3))
        .contentType("application/json")
        .body(
            new NotificationPreferencesRequest(
                List.of(
                    new NotificationPreferenceUpdate(
                        NotificationType.RIDE_CANCELLED, NotificationChannel.EMAIL, false))))
        .when()
        .put("/api/notifications/preferences")
        .then()
        .statusCode(200);

    cancel(registeredRide());
    drain();

    assertEquals(1, notifications.notificationTypesFor(user3).size());
    assertTrue(notifications.deliveriesFor(user3).isEmpty());
  }

  @Test
  void riderWhoLeftTheTeam_isNotToldOfTheCancellation() {
    RegisteredRide registered = registeredRide();
    notifications.removeFromTeam(user3, team1);
    cancel(registered);
    drain();

    assertTrue(notifications.notificationTypesFor(user3).isEmpty());
  }

  // ------------------------------------------------------------------ comments

  @Test
  void reply_notifiesTheParentAuthor() {
    Ride ride = dataService.createRide(team1, user1, "Sortie", "sortie", nextWeek);
    String rootId =
        given()
            .auth()
            .oauth2(getAccessToken(USER3))
            .contentType("application/json")
            .body(new CommentRequest("On part à quelle heure ?", null))
            .when()
            .post("/api/teams/" + team1Slug + "/rides/" + ride.getSlug() + "/comments")
            .then()
            .statusCode(201)
            .extract()
            .path("id");
    given()
        .auth()
        .oauth2(getAccessToken(USER2))
        .contentType("application/json")
        .body(new CommentRequest("8h30 au parking", rootId))
        .when()
        .post("/api/teams/" + team1Slug + "/rides/" + ride.getSlug() + "/comments")
        .then()
        .statusCode(201);
    drain();

    assertEquals(
        List.of(NotificationType.COMMENT_REPLY), notifications.notificationTypesFor(user3));
    assertTrue(notifications.notificationTypesFor(user2).isEmpty(), "the replier");
    // The ride's author hears of the thread once, when it starts — not of every reply in it.
    assertEquals(
        List.of(NotificationType.COMMENT_ON_MY_PUBLICATION),
        notifications.notificationTypesFor(user1));
  }

  // ------------------------------------------------------------------ housekeeping

  @Test
  void retention_purgesOldEvents_withTheirNotifications() {
    cancel(registeredRide());
    drain();
    assertFalse(notifications.events().isEmpty());

    notifications.backdateEvents(365);
    retentionService.purgeExpired();

    assertTrue(notifications.events().isEmpty());
    assertEquals(0, notifications.notificationCount());
  }

  // ------------------------------------------------------------------ failures and recovery

  @Test
  void failedEvent_backsOff_insteadOfBeingRetriedInTheSameTick() {
    notifications.queueUnreadableEvent(team1);

    assertTrue(dispatchService.dispatchOne());
    assertFalse(dispatchService.dispatchOne(), "backing off, not due again yet");

    NotificationEventEntry entry = notifications.eventEntries().getFirst();
    assertEquals(NotificationEventStatus.PENDING, entry.getStatus());
    assertEquals(1, entry.getAttempts());
    assertTrue(entry.getNextAttemptAt().isAfter(Instant.now()), "next attempt in the future");
  }

  @Test
  void failedEvent_givesUpAfterMaxAttempts_andReleasesItsDedupKey() {
    notifications.queueUnreadableEvent(team1);
    String key = notifications.eventEntries().getFirst().getDedupKey();

    for (int i = 0; i < dispatchService.maxAttempts(); i++) {
      notifications.makeEventsDue();
      assertTrue(dispatchService.dispatchOne());
    }

    NotificationEventEntry entry = notifications.eventEntries().getFirst();
    assertEquals(NotificationEventStatus.FAILED, entry.getStatus());
    assertTrue(entry.getDedupKey().startsWith(key + "#"), entry.getDedupKey());
  }

  @Test
  void eventLeftInProgressByACrash_isRequeued() {
    createRide(Status.PUBLISHED, nextWeek);
    assertTrue(dispatchService.claimNextPending().isPresent());
    notifications.backdateEventClaims();

    assertEquals(1, dispatchService.recoverStuck());
    assertEquals(NotificationEventStatus.PENDING, notifications.events().getFirst().status());

    drain();
    assertEquals(1, notifications.notificationTypesFor(user3).size());
  }

  @Test
  void deliveryLeftSendingByACrash_isRequeued_whileItHasAttemptsLeft() {
    cancel(registeredRide());
    drain();
    notifications.strandDeliveries(user3, 1);

    deliveryService.recoverStuck();

    assertEquals(
        NotificationDeliveryStatus.PENDING,
        notifications.deliveriesFor(user3).getFirst().getStatus());
  }

  @Test
  void deliveryLeftSendingByACrash_fails_onceOutOfAttempts() {
    cancel(registeredRide());
    drain();
    notifications.strandDeliveries(user3, deliveryService.maxAttempts());

    deliveryService.recoverStuck();

    assertEquals(
        NotificationDeliveryStatus.FAILED,
        notifications.deliveriesFor(user3).getFirst().getStatus());
  }
}
