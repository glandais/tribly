package fr.pedalons.repository.query;

public class AndClause extends MultipleClause {

  @Override
  String getJoiner() {
    return "and";
  }
}
