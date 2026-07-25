package fr.pedalons.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * Counts the SQL statements issued by EVERY HTTP request made in the test suite and feeds them to
 * {@link QueryCountReport}, keyed by the matched JAX-RS path template ({@code GET
 * /api/teams/{teamSlug}/publications}).
 *
 * <p>This lives in {@code src/test/java}, so it is only part of the application Quarkus augments
 * for {@code @QuarkusTest} — production requests are never filtered. Every existing resource test
 * therefore contributes query-count data without being modified: run the suite, read the table,
 * and the worst N+1 offenders sort to the top.
 *
 * <p><b>Why deltas, not {@code Statistics.clear()}:</b> the counters are global to the
 * SessionFactory. Clearing them would race with anything else touching the database and would also
 * wipe the numbers {@link QueryStats} is accumulating for an enclosing explicit measurement. Taking
 * a before/after snapshot of the monotonic {@code getPrepareStatementCount()} composes with both.
 * A negative delta (someone called {@code clear()} mid-request) is dropped rather than recorded.
 */
@ApplicationScoped
public class QueryCountFilter {

  private static final String BEFORE_KEY = QueryCountFilter.class.getName() + ".before";

  /** Resolved path templates, keyed by resource method — reflection once per endpoint, not per call. */
  private static final Map<Method, String> LABELS = new ConcurrentHashMap<>();

  @Inject EntityManagerFactory emf;

  private Statistics stats() {
    return emf.unwrap(SessionFactory.class).getStatistics();
  }

  private record Snapshot(long statements, long entityLoads, long queries, long writes) {}

  private Snapshot snapshot() {
    Statistics s = stats();
    long writes =
        s.getEntityInsertCount()
            + s.getEntityUpdateCount()
            + s.getEntityDeleteCount()
            + s.getCollectionRecreateCount()
            + s.getCollectionRemoveCount()
            + s.getCollectionUpdateCount();
    return new Snapshot(
        s.getPrepareStatementCount(), s.getEntityLoadCount(), s.getQueryExecutionCount(), writes);
  }

  @ServerRequestFilter
  public void before(ContainerRequestContext ctx) {
    if (!stats().isStatisticsEnabled()) {
      return;
    }
    ctx.setProperty(BEFORE_KEY, snapshot());
  }

  @ServerResponseFilter
  public void after(ContainerRequestContext ctx, ResourceInfo resourceInfo) {
    if (!(ctx.getProperty(BEFORE_KEY) instanceof Snapshot before)) {
      return;
    }
    Snapshot after = snapshot();
    long statements = after.statements() - before.statements();
    if (statements < 0) {
      // Statistics were cleared mid-request; the delta is meaningless.
      return;
    }
    Method method = resourceInfo.getResourceMethod();
    if (method == null) {
      return;
    }
    QueryCountReport.record(
        LABELS.computeIfAbsent(method, QueryCountFilter::label),
        statements,
        after.entityLoads() - before.entityLoads(),
        after.queries() - before.queries(),
        after.writes() - before.writes());
  }

  /** Builds {@code GET /api/teams/{teamSlug}/publications} from the resource method's annotations. */
  private static String label(Method method) {
    StringBuilder path = new StringBuilder();
    Path classPath = method.getDeclaringClass().getAnnotation(Path.class);
    if (classPath != null) {
      path.append(trimSlashes(classPath.value()));
    }
    Path methodPath = method.getAnnotation(Path.class);
    if (methodPath != null) {
      String suffix = trimSlashes(methodPath.value());
      if (!suffix.isEmpty()) {
        path.append('/').append(suffix);
      }
    }
    return httpMethod(method) + " /" + path;
  }

  private static String httpMethod(Method method) {
    for (Annotation annotation : method.getAnnotations()) {
      HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
      if (httpMethod != null) {
        return httpMethod.value();
      }
    }
    return "???";
  }

  private static String trimSlashes(String value) {
    String trimmed = value;
    while (trimmed.startsWith("/")) {
      trimmed = trimmed.substring(1);
    }
    while (trimmed.endsWith("/")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed;
  }
}
