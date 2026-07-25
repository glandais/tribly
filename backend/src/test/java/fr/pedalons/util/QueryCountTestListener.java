package fr.pedalons.util;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

/**
 * Prints the {@link QueryCountReport} table once the whole test plan has finished.
 *
 * <p>Registered through {@code META-INF/services/org.junit.platform.launcher.TestExecutionListener}.
 * A JVM shutdown hook would be simpler but does not work under Surefire: it closes the forked JVM's
 * stdout forwarding channel before shutdown hooks run, so the table never reaches the build log.
 */
public class QueryCountTestListener implements TestExecutionListener {

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    QueryCountReport.print();
  }
}
