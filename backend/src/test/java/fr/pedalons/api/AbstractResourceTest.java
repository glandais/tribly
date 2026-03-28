package fr.pedalons.api;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.auth.JwtService;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import jakarta.inject.Inject;

public abstract class AbstractResourceTest extends AbstractBaseTest {

  protected static final String USER1 = "user1";
  protected static final String EMAIL1 = "user1@example.com";
  protected static final String USER2 = "user2";
  protected static final String EMAIL2 = "user2@example.com";
  protected static final String USER3 = "user3";
  protected static final String EMAIL3 = "user3@example.com";
  protected static final String USER4 = "user4";
  protected static final String EMAIL4 = "user4@example.com";
  protected static final String USER5 = "user5";
  protected static final String EMAIL5 = "user5@example.com";

  @Inject protected TestDataService dataService;
  @Inject protected TestDataCleaner dataCleaner;
  @Inject protected JwtService jwtService;

  protected Domain domain;
  protected User user1;
  // public team
  protected Team team1;
  protected String team1Slug;
  // private team
  protected Team team2;
  protected String team2Slug;
  protected User user2;
  protected User user3;
  protected User user4;
  protected User user5;

  protected String getAccessToken(String userName) {
    // Get the user by email (userName is the prefix, e.g., "user1" -> "user1@example.com")
    User user =
        switch (userName) {
          case USER1 -> user1;
          case USER2 -> user2;
          case USER3 -> user3;
          case USER4 -> user4;
          case USER5 -> user5;
          default -> dataService.findUserByEmail(userName + "@example.com");
        };
    return jwtService.generateAccessToken(user);
  }

  protected void setUp() {
    dataCleaner.cleanAll();
    // Create domain first - DomainResolver will find it from HTTP request Host header
    domain = dataService.getOrCreateDefaultDomain();

    user1 = dataService.createUser(EMAIL1, "Test User 1");
    user2 = dataService.createUser(EMAIL2, "Test User 2");
    user3 = dataService.createUser(EMAIL3, "Test User 3");
    user4 = dataService.createUser(EMAIL4, "Test User 4");
    user5 = dataService.createUser(EMAIL5, "Test User 5");

    // Create test team with organizer
    team1 = dataService.createTeam(user1, "Team 1", "team-1", Visibility.PUBLIC);
    dataService.addUserToTeam(user2, team1, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user3, team1, TeamRole.MEMBER);
    team1Slug = team1.getSlug();

    team2 = dataService.createTeam(user1, "Team 2", "team-2", Visibility.TEAM);
    dataService.addUserToTeam(user2, team2, TeamRole.ORGANIZER);
    dataService.addUserToTeam(user3, team2, TeamRole.MEMBER);
    team2Slug = team2.getSlug();
  }
}
