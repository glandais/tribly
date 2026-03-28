package fr.pedalons.domain.common;

import fr.pedalons.repository.query.OrClause;
import fr.pedalons.repository.query.PedalonsQuery;
import fr.pedalons.repository.query.SimpleClause;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class SearchClause {

  public static PedalonsQuery addSearch(
      PedalonsQuery pedalonsQuery, Set<String> on, @Nullable String searched) {
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

        pedalonsQuery = pedalonsQuery.and(orClause);
        i++;
      }
    }
    return pedalonsQuery;
  }
}
