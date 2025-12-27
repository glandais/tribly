package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.*;
import com.tribly.domain.route.Route;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RouteRepository implements TeamEntityRepository<Route, TeamEntityQueryBasic> {
  @Override
  public String getTypeName() {
    return "Route";
  }
}
