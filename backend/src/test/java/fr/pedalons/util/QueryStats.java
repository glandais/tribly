package fr.pedalons.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

/**
 * Counts the SQL statements executed during a block of code — typically a single HTTP call issued
 * with RestAssured — so integration tests can assert a query budget and catch N+1 regressions.
 *
 * <p>This works because {@code @QuarkusTest} runs the server in the SAME JVM as the test: the
 * CDI-injected {@link EntityManagerFactory} is the exact {@link SessionFactory} the request worker
 * threads use. {@link Statistics} is global to that SessionFactory, hence {@link #reset()} must be
 * called immediately before the HTTP call and the counters read immediately after.
 *
 * <p>Surefire runs with {@code forkCount=1} and JUnit executes methods sequentially, so no other
 * request touches the database between {@code reset()} and the read — the counts are deterministic.
 */
@ApplicationScoped
public class QueryStats {

  @Inject EntityManagerFactory emf;

  private Statistics stats() {
    Statistics s = emf.unwrap(SessionFactory.class).getStatistics();
    if (!s.isStatisticsEnabled()) {
      s.setStatisticsEnabled(true);
    }
    return s;
  }

  /** Resets ALL Hibernate counters. Call this immediately before the request under test. */
  public void reset() {
    stats().clear();
  }

  /**
   * Total JDBC statements prepared since {@link #reset()} (SELECT + INSERT + UPDATE + DELETE). For a
   * pure GET endpoint every statement is a SELECT, so this IS the read-query count.
   */
  public long statementCount() {
    return stats().getPrepareStatementCount();
  }

  /** Entity + collection write statements (inserts, updates, deletes) since {@link #reset()}. */
  public long writeCount() {
    Statistics s = stats();
    return s.getEntityInsertCount()
        + s.getEntityUpdateCount()
        + s.getEntityDeleteCount()
        + s.getCollectionRecreateCount()
        + s.getCollectionRemoveCount()
        + s.getCollectionUpdateCount();
  }

  /** Approximate reads = total statements - writes. Exact on pure-GET endpoints. */
  public long readCount() {
    return statementCount() - writeCount();
  }

  /** HQL/Criteria/native query executions (EXCLUDES find()/lazy-load by id). */
  public long queryExecutionCount() {
    return stats().getQueryExecutionCount();
  }

  /** Number of entity rows hydrated — one real SELECT each (L2 cache is off in tests). */
  public long entityLoadCount() {
    return stats().getEntityLoadCount();
  }

  /**
   * Runs {@code httpCall} between a {@link #reset()} and a statement-count read, records the result
   * under {@code label} for the end-of-run report, and returns the number of statements executed.
   *
   * <pre>{@code
   * long selects = queryStats.measure(
   *     "GET /rides (20 rides)",
   *     () -> given().auth().oauth2(token).when().get(path).then().statusCode(200));
   * }</pre>
   */
  public long measure(String label, Runnable httpCall) {
    reset();
    httpCall.run();
    long statements = statementCount();
    QueryCountReport.record(label, statements, entityLoadCount(), queryExecutionCount());
    return statements;
  }
}
