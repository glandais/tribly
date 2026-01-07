package com.tribly.service.security;

import com.tribly.common.exception.ForbiddenException;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SecurityVerifier {

  private static final Logger LOG = Logger.getLogger(SecurityVerifier.class);

  @Inject Instance<AccessChecker> accessCheckers;

  final Map<EntityType, AccessChecker> accessCheckerMap = new EnumMap<>(EntityType.class);

  @PostConstruct
  void init() {
    accessCheckers.forEach(
        accessChecker -> accessCheckerMap.put(accessChecker.getType(), accessChecker));
  }

  AccessChecker getAccessChecker(EntityType type) {
    AccessChecker accessChecker = accessCheckerMap.get(type);
    if (accessChecker == null) {
      LOG.infov("No AccessChecker for " + type);
      throw new ForbiddenException();
    }
    return accessChecker;
  }

  public void verifyAccess(EntityType entityType, ActionType action, List<Object> params) {
    if (!hasAccess(entityType, action, params)) {
      throw new ForbiddenException();
    }
  }

  protected boolean hasAccess(EntityType entityType, ActionType action, List<Object> params) {
    AccessChecker accessChecker = getAccessChecker(entityType);
    return accessChecker.hasRights(action, params);
  }
}
