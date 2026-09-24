package fr.pedalons.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class PersistenceErrorsTest {

  @Test
  void aUniqueViolation_isFound_howeverDeeplyWrapped() {
    SQLException unique = new SQLException("duplicate key", "23505");
    assertTrue(PersistenceErrors.isUniqueViolation(unique));
    assertTrue(
        PersistenceErrors.isUniqueViolation(
            new RuntimeException(
                new PersistenceException(
                    new org.hibernate.exception.ConstraintViolationException(
                        "could not execute statement", unique, "uk_teams_domain_slug")))));
  }

  @Test
  void aUniqueViolation_chainedBehindAnotherSqlException_isFound() {
    SQLException batch = new SQLException("batch failed", "08000");
    batch.setNextException(new SQLException("duplicate key", "23505"));
    assertTrue(PersistenceErrors.isUniqueViolation(new PersistenceException(batch)));
  }

  @Test
  void otherConstraintViolations_andOtherErrors_areNot() {
    assertFalse(
        PersistenceErrors.isUniqueViolation(
            new org.hibernate.exception.ConstraintViolationException(
                "fk", new SQLException("fk", "23503"), "fk_x")));
    assertFalse(PersistenceErrors.isUniqueViolation(new IllegalStateException("boom")));
  }
}
