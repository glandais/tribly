package com.tribly.repository.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public abstract class MultipleClause implements Clause {

  final List<Clause> clauses = new ArrayList<>();

  abstract String getJoiner();

  public void add(Clause clause) {
    clauses.add(clause);
  }

  @Override
  public String clause() {
    return clauses.stream()
        .map(Clause::clause)
        .map(s -> "(" + s + ")")
        .collect(Collectors.joining(" " + getJoiner() + " "));
  }

  @Override
  public Map<String, @Nullable Object> params() {
    Map<String, @Nullable Object> params = new HashMap<>();
    for (Clause clause : clauses) {
      params.putAll(clause.params());
    }
    return params;
  }
}
