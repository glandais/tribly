package com.tribly.service.security;

import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import java.util.List;

public interface AccessChecker {
  EntityType getType();

  boolean hasRights(ActionType action, List<Object> params);
}
