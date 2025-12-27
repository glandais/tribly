package com.tribly.domain.common.query;

public class OrClause extends MultipleClause {

  @Override
  String getJoiner() {
    return "or";
  }
}
