package fr.pedalons.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accumulates per-endpoint SQL statement counts and prints a single formatted table at the end of
 * the test run.
 *
 * <p>Two feeds land here:
 *
 * <ul>
 *   <li>{@link QueryCountFilter} — automatic, every HTTP request of every {@code @QuarkusTest} in
 *       the suite, keyed by the matched JAX-RS path template. Zero test changes required.
 *   <li>{@link QueryStats#measureAll} — explicit, for tests that seed a known row count and assert a
 *       budget; the label is whatever the test passes.
 * </ul>
 *
 * <h2>Why the accumulator hangs off {@code System.getProperties()}</h2>
 *
 * A plain {@code static} field does NOT work here. {@code @QuarkusTest} runs application beans in
 * Quarkus's own augmentation classloader while the JUnit {@link QueryCountTestListener} runs in
 * Surefire's, so each side would load its <em>own</em> copy of this class with its own static map —
 * the recorder would fill one and the printer would read the other, empty one. {@code
 * System.getProperties()} is loaded by the bootstrap classloader and is therefore the same object on
 * both sides. Only JDK types cross that boundary (the values are {@code long[]}, never a custom
 * class), because a custom class would arrive as a different {@code Class} and fail to cast.
 *
 * <p>Printing happens at {@code testPlanExecutionFinished}, not from a JVM shutdown hook: Surefire
 * tears down its stdout forwarding channel before shutdown hooks run, so anything printed there is
 * silently swallowed. The table is also written to {@code target/query-count-report.txt}.
 */
public final class QueryCountReport {

  private static final String ROWS_KEY = "fr.pedalons.queryCountReport.rows";
  private static final String PRINTED_KEY = "fr.pedalons.queryCountReport.printed";
  private static final Path REPORT_FILE = Path.of("target", "query-count-report.txt");

  // Indexes into the long[] stored per label.
  private static final int CALLS = 0;
  private static final int MAX_STATEMENTS = 1;
  private static final int TOTAL_STATEMENTS = 2;
  private static final int MAX_ENTITY_LOADS = 3;
  private static final int MAX_QUERIES = 4;
  private static final int MAX_WRITES = 5;
  private static final int SLOTS = 6;

  private QueryCountReport() {}

  @SuppressWarnings("unchecked")
  private static Map<String, long[]> rows() {
    return (Map<String, long[]>)
        System.getProperties()
            .computeIfAbsent(ROWS_KEY, k -> new ConcurrentHashMap<String, long[]>());
  }

  /**
   * Records one measurement under {@code label}. Thread-safe.
   *
   * <p>{@code writes} is reported separately because a high statement count means two completely
   * different things depending on it. {@code PUT /routes} issues ~27 statements, but most are the
   * inserts and deletes of a GPX track's points — real work, not waste. A read endpoint at the same
   * count with zero writes is a fetch-strategy problem. Lumping them together makes the table sort
   * the busiest writer to the top and bury the actual N+1.
   */
  public static void record(
      String label, long statements, long entityLoads, long queries, long writes) {
    rows()
        .compute(
            label,
            (k, row) -> {
              long[] updated = row == null ? new long[SLOTS] : row;
              updated[CALLS]++;
              updated[TOTAL_STATEMENTS] += statements;
              updated[MAX_STATEMENTS] = Math.max(updated[MAX_STATEMENTS], statements);
              updated[MAX_ENTITY_LOADS] = Math.max(updated[MAX_ENTITY_LOADS], entityLoads);
              updated[MAX_QUERIES] = Math.max(updated[MAX_QUERIES], queries);
              updated[MAX_WRITES] = Math.max(updated[MAX_WRITES], writes);
              return updated;
            });
  }

  /**
   * Read statements = total statements minus the entity/collection write statements. Both maxima are
   * over different calls in principle, so this is an estimate; in practice a label is one endpoint
   * doing one thing, and it is the number worth ranking on.
   */
  private static long reads(long[] row) {
    return Math.max(0, row[MAX_STATEMENTS] - row[MAX_WRITES]);
  }

  /** Clears everything. Only meant for the report's own unit test. */
  static void reset() {
    rows().clear();
    System.getProperties().remove(PRINTED_KEY);
  }

  /**
   * Renders the table, sorted by worst-case READ count descending — statements minus writes.
   *
   * <p>Sorting on total statements would put the busiest write endpoint on top, which is the one
   * line in the table that is least likely to be a problem. Reads are what a fetch strategy
   * controls, so reads are what the first line you look at should be.
   */
  static String render() {
    Map<String, long[]> rows = rows();
    if (rows.isEmpty()) {
      return "";
    }
    List<Map.Entry<String, long[]>> entries = new ArrayList<>(rows.entrySet());
    entries.sort(
        Comparator.<Map.Entry<String, long[]>>comparingLong(e -> reads(e.getValue()))
            .reversed()
            .thenComparing(Map.Entry::getKey));

    int labelWidth = "Endpoint".length();
    for (String label : rows.keySet()) {
      labelWidth = Math.max(labelWidth, label.length());
    }
    String rowFormat = "| %-" + labelWidth + "s | %7s | %9s | %9s | %9s | %11s | %10s |";
    String border =
        "+"
            + "-".repeat(labelWidth + 2)
            + "+---------+-----------+-----------+-----------+-------------+------------+";

    StringBuilder sb = new StringBuilder();
    sb.append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    sb.append(String.format(rowFormat, "SQL QUERY COUNT REPORT", "", "", "", "", "", ""))
        .append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    sb.append(
            String.format(
                rowFormat,
                "Endpoint",
                "calls",
                "maxReads",
                "maxWrites",
                "avgSQL",
                "maxEntities",
                "maxHqlQry"))
        .append(System.lineSeparator());
    sb.append(border).append(System.lineSeparator());
    for (Map.Entry<String, long[]> e : entries) {
      long[] r = e.getValue();
      sb.append(
              String.format(
                  rowFormat,
                  e.getKey(),
                  r[CALLS],
                  reads(r),
                  r[MAX_WRITES],
                  r[CALLS] == 0 ? 0 : r[TOTAL_STATEMENTS] / r[CALLS],
                  r[MAX_ENTITY_LOADS],
                  r[MAX_QUERIES]))
          .append(System.lineSeparator());
    }
    sb.append(border).append(System.lineSeparator());
    return sb.toString();
  }

  /** Prints the table to stdout and writes it to {@code target/query-count-report.txt}. */
  public static void print() {
    if (System.getProperties().putIfAbsent(PRINTED_KEY, Boolean.TRUE) != null) {
      return;
    }
    String table = render();
    if (table.isEmpty()) {
      table =
          System.lineSeparator()
              + "SQL QUERY COUNT REPORT: no measurements recorded."
              + " Is quarkus.hibernate-orm.statistics enabled for the test profile,"
              + " and was any HTTP request made?"
              + System.lineSeparator();
    }
    System.out.println(table);
    System.out.flush();
    try {
      Files.createDirectories(REPORT_FILE.getParent());
      Files.writeString(REPORT_FILE, table, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("Could not write " + REPORT_FILE + ": " + e.getMessage());
    }
  }
}
