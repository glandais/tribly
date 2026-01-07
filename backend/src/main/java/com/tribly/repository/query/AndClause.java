package com.tribly.repository.query;

public class AndClause extends MultipleClause {

  @Override
  String getJoiner() {
    return "and";
  }
}
