package com.tribly.domain.common.query;

public class AndClause extends MultipleClause {

  @Override
  String getJoiner() {
    return "and";
  }
}
