package com.tribly.domain.common.repository;

import com.tribly.domain.common.query.TriblyQuery;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

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
}
