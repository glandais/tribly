package com.tribly.repository.common;

import com.tribly.repository.query.TriblyQuery;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> extends PanacheRepository<T> {

  default TriblyPage<T> getPage(TriblyQuery triblyQuery, int page, int size) {
    PanacheQuery<T> panacheQuery = find(triblyQuery.getStringQuery(), triblyQuery.getParams());
    return getPage(panacheQuery, page, size);
  }

  default <X> TriblyPage<X> getPage(PanacheQuery<X> panacheQuery, int page, int size) {
    if (size == 0) {
      size = 20;
    }
    return new TriblyPage<>(panacheQuery.page(page, size).list(), panacheQuery.count());
  }

  default TriblyPage<T> getPage(TriblyQuery triblyQuery, PageInterface pageInterface) {
    return getPage(triblyQuery, pageInterface.page(), pageInterface.size());
  }

  default Optional<T> findOne(TriblyQuery triblyQuery) {
    PanacheQuery<T> panacheQuery = find(triblyQuery.getStringQuery(), triblyQuery.getParams());
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

  default List<T> findAll(TriblyQuery triblyQuery) {
    PanacheQuery<T> panacheQuery = find(triblyQuery.getStringQuery(), triblyQuery.getParams());
    return panacheQuery.list();
  }
}
