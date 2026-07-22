package fr.pedalons.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Accumulates the per-endpoint query measurements taken via {@link QueryStats#measure} and prints a
 * single formatted table at JVM shutdown — i.e. at the very end of the test run — so the numbers
 * show up in the GitHub Actions log without failing any build.
 *
 * <p>Surefire runs {@code forkCount=1}, so there is exactly one JVM and one report.
 */
public final class QueryCountReport {

  private record Row(long statements, long entityLoads, long queries) {}

  private static final Map<String, Row> ROWS = new LinkedHashMap<>();
  private static final AtomicBoolean HOOK_REGISTERED = new AtomicBoolean(false);

  private QueryCountReport() {}

  /** Records one measurement. If a label is measured more than once, the worst case is kept. */
  public static synchronized void record(
      String label, long statements, long entityLoads, long queries) {
    ensureHook();
    ROWS.merge(
        label,
        new Row(statements, entityLoads, queries),
        (a, b) -> a.statements() >= b.statements() ? a : b);
  }

  private static void ensureHook() {
    if (HOOK_REGISTERED.compareAndSet(false, true)) {
      Runtime.getRuntime()
          .addShutdownHook(new Thread(QueryCountReport::print, "query-count-report"));
    }
  }

  static synchronized void print() {
    if (ROWS.isEmpty()) {
      return;
    }
    int labelWidth = "Endpoint".length();
    for (String label : ROWS.keySet()) {
      labelWidth = Math.max(labelWidth, label.length());
    }
    String border = "+" + "-".repeat(labelWidth + 2) + "+-----------+--------------+-----------+";
    StringBuilder sb = new StringBuilder();
    sb.append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    sb.append(
        String.format(
            "| %-" + labelWidth + "s | %-9s | %-12s | %-9s |",
            "SQL QUERY COUNT REPORT",
            "",
            "",
            ""));
    sb.append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    sb.append(
        String.format(
            "| %-" + labelWidth + "s | %-9s | %-12s | %-9s |",
            "Endpoint",
            "SELECTs",
            "entityLoads",
            "hqlQueries"));
    sb.append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    for (Map.Entry<String, Row> e : ROWS.entrySet()) {
      Row r = e.getValue();
      sb.append(
          String.format(
              "| %-" + labelWidth + "s | %9d | %12d | %9d |",
              e.getKey(),
              r.statements(),
              r.entityLoads(),
              r.queries()));
      sb.append(System.lineSeparator());
    }
    sb.append(border).append(System.lineSeparator());
    // Written to stdout so it lands in the "Run backend tests" step log.
    System.out.println(sb);
  }
}
