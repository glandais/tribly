package fr.pedalons.repository.common;

import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.query.PedalonsQuery;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> extends PanacheRepository<T> {

  default PedalonsPage<T> getPage(PedalonsQuery pedalonsQuery, int page, int size) {
    PanacheQuery<T> panacheQuery = find(pedalonsQuery.getStringQuery(), pedalonsQuery.getParams());
    return getPage(panacheQuery, page, size);
  }

  default <X> PedalonsPage<X> getPage(PanacheQuery<X> panacheQuery, int page, int size) {
    if (size == 0) {
      size = 20;
    }
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
