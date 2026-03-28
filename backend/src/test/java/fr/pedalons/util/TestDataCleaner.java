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
    entityManager.createNativeQuery("TRUNCATE TABLE users CASCADE").executeUpdate();
    entityManager.createNativeQuery("TRUNCATE TABLE domains CASCADE").executeUpdate();
  }
}
