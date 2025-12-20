package com.tribly.domain.common.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

public class TriblyQuery {

  private final String baseQuery;
  private final List<String> ands = new ArrayList<>();
  @Getter private final Map<String, @Nullable Object> params = new HashMap<>();
  private @Nullable String order = null;

  public TriblyQuery() {
    this("");
  }

  public TriblyQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public TriblyQuery and(String clause, Map<String, @Nullable Object> params) {
    this.ands.add(clause);
    this.params.putAll(params);
    return this;
  }

  public TriblyQuery order(String order) {
    this.order = order;
    return this;
  }

  public String getStringQuery() {
    return String.join(
        " ", baseQuery, String.join(" and ", ands), order != null ? " order by " + order : "");
  }

  public void addParam(String key, @Nullable Object value) {
    params.put(key, value);
  }
}
