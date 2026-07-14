package fr.pedalons.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestDataCleaner {

  @Inject EntityManager entityManager;

  @Transactional
  public void cleanAll() {
    // DELETE instead of TRUNCATE: tables are near-empty between tests, and TRUNCATE pays
    // per-table relfilenode swaps + fsyncs (~40 cascaded tables). session_replication_role
    // = replica (tx-local) skips FK ordering; DELETE on empty tables is sub-millisecond.
    entityManager
        .createNativeQuery(
            """
            DO $$
            DECLARE t text;
            BEGIN
              SET LOCAL session_replication_role = replica;
              FOR t IN SELECT tablename FROM pg_tables
                       WHERE schemaname = 'public' AND tablename <> 'spatial_ref_sys' LOOP
                EXECUTE 'DELETE FROM ' || quote_ident(t);
              END LOOP;
            END $$
            """)
        .executeUpdate();
  }
}
