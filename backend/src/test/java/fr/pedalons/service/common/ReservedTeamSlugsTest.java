package fr.pedalons.service.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Plain unit test, no Quarkus: the team slugs a web page answers come from the routes contract,
 * and a new literal segment next to /teams/{teamSlug} must be reserved the day it is added.
 */
class ReservedTeamSlugsTest {

  /** A locale path whose segment after /teams/ or /equipes/ is a literal, not {teamSlug}. */
  private static final Pattern TEAM_LEVEL_LITERAL =
      Pattern.compile("^\\s+\\w+: /(?:teams|equipes)/([a-z0-9-]+)(?:/|$)", Pattern.MULTILINE);

  @Test
  void everyLiteralSegmentNextToATeamSlugIsReserved() throws IOException {
    String contract = Files.readString(Path.of("..", "contracts", "routes.yaml"));
    Set<String> literals = new TreeSet<>();
    Matcher matcher = TEAM_LEVEL_LITERAL.matcher(contract);
    while (matcher.find()) {
      literals.add(matcher.group(1));
    }
    assertEquals(literals, new TreeSet<>(SlugService.RESERVED_TEAM_SLUGS));
  }
}
