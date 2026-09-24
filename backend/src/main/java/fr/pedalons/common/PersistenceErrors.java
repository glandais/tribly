package fr.pedalons.common;

import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Recognises database errors through whatever wraps them (Hibernate, JPA, JTA, Arc). */
public final class PersistenceErrors {

  /** PostgreSQL's {@code unique_violation}. */
  public static final String UNIQUE_VIOLATION = "23505";

  private PersistenceErrors() {}

  /**
   * Whether {@code e} was caused by a unique constraint violation — SQLState {@code 23505} on any
   * {@link SQLException} of its cause chain, or chained behind one by {@link
   * SQLException#getNextException()}. A foreign key or not-null violation is not one.
   */
  public static boolean isUniqueViolation(Throwable e) {
    Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
    for (Throwable t = e; t != null && seen.add(t); t = t.getCause()) {
      if (t instanceof SQLException sql) {
        for (SQLException s = sql;
            s != null && (s == sql || seen.add(s));
            s = s.getNextException()) {
          if (UNIQUE_VIOLATION.equals(s.getSQLState())) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
