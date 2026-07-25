package fr.pedalons.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

/**
 * Counts the SQL statements executed during a block of code — typically a single HTTP call issued
 * with RestAssured — so integration tests can assert a query budget and catch N+1 regressions.
 *
 * <p>This works because {@code @QuarkusTest} runs the server in the SAME JVM as the test: the
 * CDI-injected {@link EntityManagerFactory} is the exact {@link SessionFactory} the request worker
 * threads use, and {@link Statistics} is global to that SessionFactory.
 *
 * <p>Measurements are before/after deltas of the monotonic counters, never {@code clear()}: clearing
 * would wipe the numbers {@link QueryCountFilter} is collecting for the same request. Surefire runs
 * with {@code forkCount=1} and JUnit executes methods sequentially, so nothing else touches the
 * database inside a measured block — the deltas are deterministic.
 */
@ApplicationScoped
public class QueryStats {

  @Inject EntityManagerFactory emf;

  /** An immutable read of the counters, taken at one instant. */
  public record Counters(
      long statements, long entityLoads, long queries, long writes, long collectionLoads) {

    /** How many of each counter accrued between {@code this} (the earlier read) and {@code later}. */
    public Counters minus(Counters earlier) {
      return new Counters(
          statements - earlier.statements,
          entityLoads - earlier.entityLoads,
          queries - earlier.queries,
          writes - earlier.writes,
          collectionLoads - earlier.collectionLoads);
    }

    /** Reads = total statements minus the entity/collection write statements. */
    public long reads() {
      return statements - writes;
    }
  }

  private Statistics stats() {
    Statistics s = emf.unwrap(SessionFactory.class).getStatistics();
    if (!s.isStatisticsEnabled()) {
      s.setStatisticsEnabled(true);
    }
    return s;
  }

  /** Reads every counter this class tracks, at this instant. */
  public Counters counters() {
    Statistics s = stats();
    long writes =
        s.getEntityInsertCount()
            + s.getEntityUpdateCount()
            + s.getEntityDeleteCount()
            + s.getCollectionRecreateCount()
            + s.getCollectionRemoveCount()
            + s.getCollectionUpdateCount();
    return new Counters(
        s.getPrepareStatementCount(),
        s.getEntityLoadCount(),
        s.getQueryExecutionCount(),
        writes,
        s.getCollectionLoadCount());
  }

  /**
   * Runs {@code block} and returns everything that happened to the database while it ran, recording
   * it under {@code label} for the end-of-run {@link QueryCountReport}.
   *
   * <pre>{@code
   * var counters = queryStats.measureAll(
   *     "GET /rides (20 rides)",
   *     () -> given().auth().oauth2(token).when().get(path).then().statusCode(200));
   * assertThat(counters.statements()).isLessThanOrEqualTo(15);
   * }</pre>
   */
  public Counters measureAll(String label, Runnable block) {
    Counters before = counters();
    block.run();
    Counters delta = counters().minus(before);
    QueryCountReport.record(label, delta.statements(), delta.entityLoads(), delta.queries());
    return delta;
  }

  /** {@link #measureAll} when all you need is the statement count. */
  public long measure(String label, Runnable block) {
    return measureAll(label, block).statements();
  }

  /**
   * Every HQL/native query Hibernate has executed since the statistics were last cleared, with its
   * execution count, most-executed first.
   *
   * <p>Put this in the failure message of a query-budget assertion: "18 statements, budget 12" tells
   * you a test failed, this tells you <em>which</em> query to go and fix. Note that it lists only
   * real queries — lazy-loads and batch-loads by id are not "queries" to Hibernate, so a breakdown
   * with few entries but a high statement count means the cost is in association fetching.
   *
   * <p>Counts are cumulative for the whole test run, not scoped to one {@link #measureAll} block:
   * Hibernate keeps per-query statistics only in aggregate. Read it as "what does this endpoint
   * run", not "how many times during that one call".
   */
  public String queryBreakdown(int limit) {
    Statistics s = stats();
    return Arrays.stream(s.getQueries())
        .map(hql -> Map.entry(hql, s.getQueryStatistics(hql).getExecutionCount()))
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
        .limit(limit)
        .map(e -> "  " + e.getValue() + "x  " + e.getKey())
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
