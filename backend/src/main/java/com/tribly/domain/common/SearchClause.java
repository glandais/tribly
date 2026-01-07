package com.tribly.domain.common;

import com.tribly.repository.query.OrClause;
import com.tribly.repository.query.SimpleClause;
import com.tribly.repository.query.TriblyQuery;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class SearchClause {

  public static TriblyQuery addSearch(
      TriblyQuery triblyQuery, Set<String> on, @Nullable String searched) {
    if (searched != null && !searched.isBlank()) {
      String[] terms = searched.split(" ");
      int i = 0;
      for (String term : terms) {
        String trimmed = term.trim();
        String searchPattern = "%" + trimmed.toLowerCase() + "%";

        OrClause orClause = new OrClause();
        String param = "search" + i;
        for (String field : on) {
          orClause.add(
              new SimpleClause(
                  "lower(" + field + ") like :" + param, Map.of(param, searchPattern)));
        }

        triblyQuery = triblyQuery.and(orClause);
        i++;
      }
    }
    return triblyQuery;
  }
}
