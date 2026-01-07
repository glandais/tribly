package com.tribly.repository.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.UserTeam;
import com.tribly.enums.TeamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserTeam} utility methods.
 * No database required.
 */
class UserTeamTest {

  @Nested
  @DisplayName("isAdmin")
  class IsAdmin {

    @Test
    @DisplayName("Should return true for ADMIN role")
    void isAdmin_adminRole_shouldReturnTrue() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ADMIN);

      assertTrue(userTeam.isAdmin());
    }

    @Test
    @DisplayName("Should return false for ORGANIZER role")
    void isAdmin_organizerRole_shouldReturnFalse() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ORGANIZER);

      assertFalse(userTeam.isAdmin());
    }

    @Test
    @DisplayName("Should return false for MEMBER role")
    void isAdmin_memberRole_shouldReturnFalse() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.MEMBER);

      assertFalse(userTeam.isAdmin());
    }
  }

  @Nested
  @DisplayName("isOrganizer")
  class IsOrganizer {

    @Test
    @DisplayName("Should return true for ADMIN role")
    void isOrganizer_adminRole_shouldReturnTrue() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ADMIN);

      assertTrue(userTeam.isOrganizer());
    }

    @Test
    @DisplayName("Should return true for ORGANIZER role")
    void isOrganizer_organizerRole_shouldReturnTrue() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ORGANIZER);

      assertTrue(userTeam.isOrganizer());
    }

    @Test
    @DisplayName("Should return false for MEMBER role")
    void isOrganizer_memberRole_shouldReturnFalse() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.MEMBER);

      assertFalse(userTeam.isOrganizer());
    }
  }

  @Nested
  @DisplayName("Role Hierarchy")
  class RoleHierarchy {

    @Test
    @DisplayName("ADMIN should be both admin and organizer")
    void admin_shouldBeBothAdminAndOrganizer() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ADMIN);

      assertTrue(userTeam.isAdmin());
      assertTrue(userTeam.isOrganizer());
    }

    @Test
    @DisplayName("ORGANIZER should be organizer but not admin")
    void organizer_shouldBeOrganizerButNotAdmin() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.ORGANIZER);

      assertFalse(userTeam.isAdmin());
      assertTrue(userTeam.isOrganizer());
    }

    @Test
    @DisplayName("MEMBER should be neither admin nor organizer")
    void member_shouldBeNeitherAdminNorOrganizer() {
      UserTeam userTeam = new UserTeam();
      userTeam.setRole(TeamRole.MEMBER);

      assertFalse(userTeam.isAdmin());
      assertFalse(userTeam.isOrganizer());
    }
  }
}
