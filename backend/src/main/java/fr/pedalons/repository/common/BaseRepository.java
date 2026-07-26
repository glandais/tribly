package fr.pedalons.repository.common;

import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.query.PedalonsQuery;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> extends PanacheRepository<T> {

  /** Default page size, applied when a caller asks for zero. */
  int DEFAULT_PAGE_SIZE = 20;

  /**
   * Server-side ceiling on page size, enforced here rather than in each resource.
   *
   * <p>Fourteen endpoints expose {@code ?size=} and none of them bounded it, so {@code size=100000}
   * was a supported request. That mattered little while a list row was a handful of columns; it
   * matters now that a row carries a generated excerpt, a thumbnail lookup and per-row "am I
   * registered" state. Clamping at the single point where pagination is actually applied covers the
   * endpoints that exist today and the ones nobody remembers to bound tomorrow.
   *
   * <p>A caller asking for more gets the ceiling, not an error: {@code total} still reports the real
   * count, so a client that wanted everything can page through it.
   */
  int MAX_PAGE_SIZE = 200;

  default PedalonsPage<T> getPage(PedalonsQuery pedalonsQuery, int page, int size) {
    String stringQuery = pedalonsQuery.getStringQuery();
    Log.debugf("Query: %s", stringQuery);
    PanacheQuery<T> panacheQuery = find(stringQuery, pedalonsQuery.getParams());
    return getPage(panacheQuery, page, size);
  }

  default <X> PedalonsPage<X> getPage(PanacheQuery<X> panacheQuery, int page, int size) {
    if (size == 0) {
      size = DEFAULT_PAGE_SIZE;
    }
    size = Math.clamp(size, 1, MAX_PAGE_SIZE);
    return new PedalonsPage<>(panacheQuery.page(page, size).list(), panacheQuery.count());
  }

  default PedalonsPage<T> getPage(PedalonsQuery pedalonsQuery, PageInterface pageInterface) {
    return getPage(pedalonsQuery, pageInterface.page(), pageInterface.size());
  }

  default Optional<T> findOne(PedalonsQuery pedalonsQuery) {
    PanacheQuery<T> panacheQuery = find(pedalonsQuery.getStringQuery(), pedalonsQuery.getParams());
    return findOne(panacheQuery);
  }

  default Optional<T> findOne(PanacheQuery<T> panacheQuery) {
    List<T> items = panacheQuery.page(0, 2).list();
    if (items.isEmpty()) {
      return Optional.empty();
    }
    if (items.size() == 1) {
      return Optional.of(items.getFirst());
    }
    throw new NonUniqueResultException("Query returned more than one result");
  }

  default List<T> findAll(PedalonsQuery pedalonsQuery) {
    PanacheQuery<T> panacheQuery = find(pedalonsQuery.getStringQuery(), pedalonsQuery.getParams());
    return panacheQuery.list();
  }
}
