package fr.pedalons.api;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.service.auth.JwtService;
import fr.pedalons.util.QueryStats;
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
  @Inject protected QueryStats queryStats;

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
    // Domain is created first - DomainResolver will find it from HTTP request Host header
    TestDataService.StandardFixture fixture = dataService.createStandardFixture();
    domain = fixture.domain();
    user1 = fixture.user1();
    user2 = fixture.user2();
    user3 = fixture.user3();
    user4 = fixture.user4();
    user5 = fixture.user5();
    team1 = fixture.team1();
    team1Slug = team1.getSlug();
    team2 = fixture.team2();
    team2Slug = team2.getSlug();
  }
}
