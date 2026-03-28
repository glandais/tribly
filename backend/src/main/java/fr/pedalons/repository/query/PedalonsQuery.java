package fr.pedalons.repository.query;

import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class PedalonsQuery {

  private final String baseQuery;
  private final AndClause clause = new AndClause();
  private final Map<String, @Nullable Object> params = new HashMap<>();
  private @Nullable String order = null;

  public PedalonsQuery() {
    this("");
  }

  public PedalonsQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public PedalonsQuery and(String clause, Map<String, @Nullable Object> params) {
    this.and(new SimpleClause(clause, params));
    return this;
  }

  public PedalonsQuery and(Clause clause) {
    this.clause.add(clause);
    return this;
  }

  public PedalonsQuery addParam(String key, @Nullable Object value) {
    params.put(key, value);
    return this;
  }

  public PedalonsQuery order(String order) {
    this.order = order;
    return this;
  }

  public String getStringQuery() {
    return String.join(" ", baseQuery, clause.clause(), order != null ? " order by " + order : "");
  }

  public Map<String, @Nullable Object> getParams() {
    Map<String, @Nullable Object> allParams = new HashMap<>(params);
    allParams.putAll(this.clause.params());
    return allParams;
  }
}
