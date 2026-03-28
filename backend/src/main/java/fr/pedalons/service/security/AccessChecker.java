package fr.pedalons.service.security;

import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import java.util.List;

public interface AccessChecker {
  EntityType getType();

  boolean hasRights(ActionType action, List<Object> params);
}
