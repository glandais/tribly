package fr.pedalons.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.util.QueryStats;
import io.restassured.specification.RequestSpecification;
import java.util.function.Supplier;

/**
 * Base for tests that assert an endpoint's database cost does not grow with the amount of data it
 * returns.
 *
 * <p>The assertion is deliberately a <b>shape</b> assertion rather than a magic bound: the same
 * endpoint is measured over a small page and a large page and the counts must stay flat. A fixed
 * "must be under N queries" limit rots — it starts passing for the wrong reason as soon as the
 * fixture changes, and it never answers the question that actually matters: <i>does serving 10x the
 * rows cost 10x the work?</i>
 *
 * <p>Two dimensions are checked, because they fail independently:
 *
 * <ul>
 *   <li><b>statements</b> — classic N+1. Batch fetching usually flattens this one.
 *   <li><b>entities hydrated</b> — the cost batch fetching hides. A list row that walks an
 *       association to count it pulls every row of that association into the persistence context;
 *       the query count stays flat while the work grows with the data.
 * </ul>
 *
 * <p>Absolute per-endpoint numbers for the whole suite are collected automatically by {@link
 * fr.pedalons.util.QueryCountFilter} and printed by {@link fr.pedalons.util.QueryCountReport} at the
 * end of the run (also written to {@code target/query-count-report.txt}).
 */
public abstract class AbstractQueryCountTest extends AbstractResourceTest {

  protected static final int SMALL_PAGE = 3;
  protected static final int LARGE_PAGE = 30;

  /**
   * Headroom between the small-page and large-page statement counts. Lazy associations are
   * batch-loaded ({@code quarkus.hibernate-orm.fetch.batch-size}), so going from 3 to 30 rows may
   * add at most one extra batch round-trip per batched association — a handful of queries, never 27
   * of them.
   */
  protected static final long MAX_STATEMENT_GROWTH = 8;

  /**
   * Headroom between the small-page and large-page entity-hydration counts. 27 extra rows must cost
   * roughly 27 extra entities (the rows themselves) plus a little, NOT 27 x the size of some
   * association hanging off each row.
   */
  protected static final long MAX_ENTITY_GROWTH = (LARGE_PAGE - SMALL_PAGE) * 2L;

  /** Authenticated as user1. */
  protected Supplier<RequestSpecification> asUser1() {
    return () -> given().auth().oauth2(getAccessToken(USER1));
  }

  /** No credentials — exercises the anonymous query shape, which has no UserTeam join. */
  protected Supplier<RequestSpecification> anonymous() {
    return io.restassured.RestAssured::given;
  }

  private QueryStats.Counters measurePage(
      String label, Supplier<RequestSpecification> as, String path, int size) {
    String url = path + (path.contains("?") ? "&" : "?") + "size=" + size;
    return queryStats.measureAll(label, () -> as.get().when().get(url).then().statusCode(200));
  }

  /**
   * Measures {@code path} at {@link #SMALL_PAGE} and {@link #LARGE_PAGE} rows and fails if either
   * the statement count or the entity-hydration count grew beyond its budget.
   *
   * <p>The caller must have already seeded at least {@link #LARGE_PAGE} rows.
   */
  protected void assertFlatQueryCount(String name, Supplier<RequestSpecification> as, String path) {
    QueryStats.Counters small =
        measurePage(name + " [" + SMALL_PAGE + " rows]", as, path, SMALL_PAGE);
    QueryStats.Counters large =
        measurePage(name + " [" + LARGE_PAGE + " rows]", as, path, LARGE_PAGE);
    assertFlat(name, small, large, MAX_ENTITY_GROWTH);
  }

  /**
   * Headroom for {@link #assertFlatAcrossResources}: the detail renders a bounded preview, so going
   * from 3 related rows to 30 may hydrate only the extra previewed rows — up to 8 per ride group,
   * and the fixtures use two groups — never one entity per related row. The page budget ({@link
   * #MAX_ENTITY_GROWTH}) would be too loose here: hydrating every registration and its user is
   * exactly +54.
   */
  protected static final long MAX_BOUNDED_ENTITY_GROWTH = 16;

  /**
   * Same budget for an endpoint that takes no page size, but embeds an association whose size the
   * fixture controls: {@code smallPath} must name a resource holding {@link #SMALL_PAGE} related
   * rows and {@code largePath} one holding {@link #LARGE_PAGE}, and serving the second must not
   * cost more than the first beyond the budgets. A detail that embeds a bounded preview of a list
   * is the case in point — the list may grow, the detail must not.
   */
  protected void assertFlatAcrossResources(
      String name, Supplier<RequestSpecification> as, String smallPath, String largePath) {
    QueryStats.Counters small =
        queryStats.measureAll(
            name + " [" + SMALL_PAGE + " related]",
            () -> as.get().when().get(smallPath).then().statusCode(200));
    QueryStats.Counters large =
        queryStats.measureAll(
            name + " [" + LARGE_PAGE + " related]",
            () -> as.get().when().get(largePath).then().statusCode(200));
    assertFlat(name, small, large, MAX_BOUNDED_ENTITY_GROWTH);
  }

  private static void assertFlat(
      String name, QueryStats.Counters small, QueryStats.Counters large, long maxEntityGrowth) {
    long statementGrowth = large.statements() - small.statements();
    assertTrue(
        statementGrowth <= MAX_STATEMENT_GROWTH,
        () ->
            "N+1 queries on "
                + name
                + ": "
                + SMALL_PAGE
                + " rows cost "
                + small.statements()
                + " SQL statements, "
                + LARGE_PAGE
                + " rows cost "
                + large.statements()
                + " (+"
                + statementGrowth
                + ", budget +"
                + MAX_STATEMENT_GROWTH
                + "). Batch-fetch or join-fetch the association being walked per row.");

    long entityGrowth = large.entityLoads() - small.entityLoads();
    assertTrue(
        entityGrowth <= maxEntityGrowth,
        () ->
            "Entity hydration scales with the association size on "
                + name
                + ": "
                + SMALL_PAGE
                + " rows hydrated "
                + small.entityLoads()
                + " entities, "
                + LARGE_PAGE
                + " rows hydrated "
                + large.entityLoads()
                + " (+"
                + entityGrowth
                + ", budget +"
                + maxEntityGrowth
                + "). The query count may look flat because of batch fetching while the work still"
                + " grows with the data — a list row is walking an association it only needs an"
                + " aggregate of. Load that aggregate in bulk for the page instead.");
  }
}
