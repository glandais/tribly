package fr.pedalons.repository.query;

public class OrClause extends MultipleClause {

  @Override
  String getJoiner() {
    return "or";
  }
}
