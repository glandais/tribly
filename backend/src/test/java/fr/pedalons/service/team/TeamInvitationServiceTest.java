package fr.pedalons.service.team;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TokenUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.teams.request.CreateInvitationRequest;
import fr.pedalons.dto.teams.response.InvitationPreviewDto;
import fr.pedalons.dto.teams.response.MemberDto;
import fr.pedalons.dto.teams.response.TeamInvitationDto;
import fr.pedalons.enums.InvitationStatus;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.team.TeamInvitationRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamInvitationServiceTest extends AbstractBaseTest {

  @Inject TeamInvitationService invitationService;
  @Inject TeamInvitationRepository invitationRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;
  @Inject MockMailbox mailbox;

  private Domain domain;
  private Team team;
  private User admin;
  private User member;
  private User outsider;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    dataService.setTeamAddMemberAllowed(team, true);
    member = dataService.createUser("member@example.com", "A Member");
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    outsider = dataService.createVerifiedUser(domain, "outsider@example.com", "An Outsider");
    mailbox.clear();
  }

  private TeamInvitationDto invite(String email) {
    queryContext.setUserForTest(admin);
    return invitationService.invite(team.getSlug(), new CreateInvitationRequest(email, null));
  }

  // ==================== Creating ====================

  @Test
  void invite_shouldCreatePendingInvitationAndSendOneEmail() {
    TeamInvitationDto dto = invite("newcomer@example.com");

    assertEquals("newcomer@example.com", dto.email());
    assertEquals(TeamRole.MEMBER, dto.role());
    assertEquals(InvitationStatus.PENDING, dto.status());
    assertEquals(1, mailbox.getMailsSentTo("newcomer@example.com").size());
  }

  /**
   * The property the whole design turns on: an administrator must not be able to use this endpoint
   * to find out whether an address has an account. Same status, same shape of body; only the
   * wording of the e-mail differs.
   */
  @Test
  void invite_shouldAnswerIdenticallyWhetherOrNotTheAccountExists() {
    TeamInvitationDto known = invite(outsider.getEmail());
    TeamInvitationDto unknown = invite("nobody@example.com");

    assertEquals(known.status(), unknown.status());
    assertEquals(known.role(), unknown.role());
    assertNull(known.acceptedAt());
    assertNull(unknown.acceptedAt());

    // ...and yet the two readers get different wording, which is the only place the difference
    // shows and the only place it is harmless.
    String toKnown = mailbox.getMailsSentTo(outsider.getEmail()).getFirst().getText();
    String toUnknown = mailbox.getMailsSentTo("nobody@example.com").getFirst().getText();
    assertTrue(toKnown.contains("Votre compte"));
    assertTrue(toUnknown.contains("Vous n'avez pas encore de compte"));
  }

  @Test
  void invite_shouldNormaliseTheAddress() {
    TeamInvitationDto dto = invite("  MiXeD@Example.COM  ");
    assertEquals("mixed@example.com", dto.email());
  }

  @Test
  void invite_toAnExistingMember_shouldConflict() {
    PedalonsException e = assertThrows(PedalonsException.class, () -> invite(member.getEmail()));
    assertEquals(ErrorCode.TEAM_INVITE_ALREADY_MEMBER, e.getErrorCode());
  }

  @Test
  void invite_toSelf_shouldFail() {
    PedalonsException e = assertThrows(PedalonsException.class, () -> invite(admin.getEmail()));
    assertEquals(ErrorCode.TEAM_INVITE_SELF, e.getErrorCode());
  }

  @Test
  void invite_whenAddMemberNotAllowed_shouldFail() {
    dataService.setTeamAddMemberAllowed(team, false);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invite("newcomer@example.com"));
    assertEquals(ErrorCode.TEAM_ADD_MEMBER_NOT_ALLOWED, e.getErrorCode());
  }

  @Test
  void invite_asPlainMember_shouldBeForbidden() {
    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class,
        () ->
            invitationService.invite(
                team.getSlug(), new CreateInvitationRequest("newcomer@example.com", null)));
  }

  /** Re-inviting is the resend: the live invitation is revoked and a fresh one issued. */
  @Test
  void invite_twice_shouldRevokeThePreviousOneAndLeaveASinglePending() {
    invite("newcomer@example.com");
    invite("newcomer@example.com");

    assertEquals(2, mailbox.getMailsSentTo("newcomer@example.com").size());
    assertEquals(
        1,
        invitationRepository
            .findByTeam(team.getId(), 0, 50, InvitationStatus.PENDING)
            .items()
            .size());
    assertEquals(
        1,
        invitationRepository
            .findByTeam(team.getId(), 0, 50, InvitationStatus.REVOKED)
            .items()
            .size());
  }

  // ==================== Redeeming ====================

  /**
   * The token never leaves the mail, so the tests read it back out of the e-mail exactly as a
   * recipient would — which also asserts that only its hash is in the database.
   */
  private String tokenFromMailTo(String email) {
    String body = mailbox.getMailsSentTo(email).getLast().getText();
    int start = body.indexOf("/invitation?token=") + "/invitation?token=".length();
    int end = start;
    while (end < body.length() && !Character.isWhitespace(body.charAt(end))) {
      end++;
    }
    return body.substring(start, end);
  }

  @Test
  void accept_shouldCreateTheMembershipAndCreditTheInviter() {
    invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());

    queryContext.setUserForTest(outsider);
    MemberDto joined = invitationService.accept(token);

    assertEquals(TeamRole.MEMBER, joined.role());
    var membership = userTeamRepository.findByUserAndTeam(outsider.getId(), team.getId());
    assertTrue(membership.isPresent());
    // Who brought this member in outlives the invitation.
    assertEquals(admin.getId(), membership.get().getCreatedBy().getId());
  }

  @Test
  void accept_shouldStoreOnlyTheTokenHash() {
    invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());

    assertTrue(invitationRepository.findByTokenHash(token).isEmpty());
    assertTrue(invitationRepository.findByTokenHash(TokenUtils.hashToken(token)).isPresent());
  }

  @Test
  void accept_replayed_shouldBeIdempotent() {
    invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());

    queryContext.setUserForTest(outsider);
    invitationService.accept(token);
    MemberDto again = invitationService.accept(token);

    assertNotNull(again);
    assertEquals(1, userTeamRepository.findByUserId(outsider.getId()).size());
  }

  /** An invitation as MEMBER must never demote someone who is already an administrator. */
  @Test
  void accept_whenAlreadyAnAdmin_shouldNotDowngradeTheRole() {
    User promoted = dataService.createVerifiedUser(domain, "promoted@example.com", "Promoted");
    invite(promoted.getEmail());
    String token = tokenFromMailTo(promoted.getEmail());
    dataService.addUserToTeam(promoted, team, TeamRole.ADMIN);

    queryContext.setUserForTest(promoted);
    MemberDto result = invitationService.accept(token);

    assertEquals(TeamRole.ADMIN, result.role());
  }

  @Test
  void accept_bySomeoneElse_shouldBeForbidden() {
    invite("newcomer@example.com");
    String token = tokenFromMailTo("newcomer@example.com");

    queryContext.setUserForTest(outsider);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invitationService.accept(token));
    assertEquals(ErrorCode.TEAM_INVITE_EMAIL_MISMATCH, e.getErrorCode());
    assertTrue(userTeamRepository.findByUserAndTeam(outsider.getId(), team.getId()).isEmpty());
  }

  /**
   * Without this, signing up with someone else's address would be enough to capture their
   * invitation. A no-op in practice today — {@code verifyEmail} is what creates a {@code User} —
   * but Strava-migrated accounts carry an unverified placeholder address, so the guard is real.
   */
  @Test
  void accept_withAnUnverifiedAddress_shouldBeForbidden() {
    User unverified = dataService.createUser("unverified@example.com", "Unverified");
    invite(unverified.getEmail());
    String token = tokenFromMailTo(unverified.getEmail());

    queryContext.setUserForTest(unverified);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invitationService.accept(token));
    assertEquals(ErrorCode.EMAIL_NOT_VERIFIED, e.getErrorCode());
    assertTrue(userTeamRepository.findByUserAndTeam(unverified.getId(), team.getId()).isEmpty());
  }

  @Test
  void accept_withAnUnknownToken_shouldFail() {
    queryContext.setUserForTest(outsider);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invitationService.accept("not-a-token"));
    assertEquals(ErrorCode.TEAM_INVITE_INVALID, e.getErrorCode());
  }

  @Test
  void accept_afterRevocation_shouldFail() {
    TeamInvitationDto dto = invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());

    queryContext.setUserForTest(admin);
    invitationService.revoke(team.getSlug(), dto.id());

    queryContext.setUserForTest(outsider);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invitationService.accept(token));
    assertEquals(ErrorCode.TEAM_INVITE_REVOKED, e.getErrorCode());
  }

  @Test
  void accept_whenExpired_shouldFailAndMarkTheRowExpired() {
    invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());
    dataService.expireInvitation(TokenUtils.hashToken(token));

    queryContext.setUserForTest(outsider);
    PedalonsException e =
        assertThrows(PedalonsException.class, () -> invitationService.accept(token));
    assertEquals(ErrorCode.TEAM_INVITE_EXPIRED, e.getErrorCode());
    assertEquals(
        InvitationStatus.EXPIRED,
        invitationRepository
            .findByTokenHash(TokenUtils.hashToken(token))
            .orElseThrow()
            .getStatus());
  }

  /**
   * Deliberate: {@code addMemberAllowed} guards the administrator's act of inviting, not the
   * invitee's consent. Re-reading it here would silently kill legitimate invitations because of a
   * platform-admin toggle, with a message the invitee could make no sense of.
   */
  @Test
  void accept_afterAddMemberAllowedIsTurnedOff_shouldStillWork() {
    invite(outsider.getEmail());
    String token = tokenFromMailTo(outsider.getEmail());
    dataService.setTeamAddMemberAllowed(team, false);

    queryContext.setUserForTest(outsider);
    assertNotNull(invitationService.accept(token));
  }

  // ==================== Preview ====================

  @Test
  void preview_shouldNameTheTeamAndMaskTheAddress() {
    invite("newcomer@example.com");
    String token = tokenFromMailTo("newcomer@example.com");

    InvitationPreviewDto preview = invitationService.preview(token);

    assertEquals(team.getName(), preview.teamName());
    assertEquals(admin.getDisplayName(), preview.inviterName());
    assertEquals("n***@example.com", preview.maskedEmail());
    assertTrue(preview.redeemable());
  }

  // ==================== The invitee's own list ====================

  @Test
  void listMine_shouldSurfaceInvitationsToSomeoneWhoSignedUpSeparately() {
    invite(outsider.getEmail());

    queryContext.setUserForTest(outsider);
    assertEquals(1, invitationService.listMine().invitations().size());
  }

  @Test
  void listMine_shouldLeaveOutTeamsTheUserAlreadyBelongsTo() {
    invite(outsider.getEmail());
    dataService.addUserToTeam(outsider, team, TeamRole.MEMBER);

    queryContext.setUserForTest(outsider);
    assertEquals(0, invitationService.listMine().invitations().size());
  }

  @Test
  void listMine_shouldKeepTwoInvitationsToTwoTeamsIndependent() {
    Team other = dataService.createTeam(admin, "Other Team", "other-team", Visibility.PUBLIC);
    dataService.setTeamAddMemberAllowed(other, true);
    invite(outsider.getEmail());
    queryContext.setUserForTest(admin);
    invitationService.invite(
        other.getSlug(), new CreateInvitationRequest(outsider.getEmail(), null));

    queryContext.setUserForTest(outsider);
    assertEquals(2, invitationService.listMine().invitations().size());

    String token = tokenFromMailTo(outsider.getEmail());
    invitationService.accept(token);
    assertEquals(1, invitationService.listMine().invitations().size());
  }

  // ==================== Revocation ====================

  @Test
  void revoke_twice_shouldConflict() {
    TeamInvitationDto dto = invite("newcomer@example.com");
    queryContext.setUserForTest(admin);
    invitationService.revoke(team.getSlug(), dto.id());

    PedalonsException e =
        assertThrows(
            PedalonsException.class, () -> invitationService.revoke(team.getSlug(), dto.id()));
    assertEquals(ErrorCode.TEAM_INVITE_NOT_PENDING, e.getErrorCode());
  }
}
