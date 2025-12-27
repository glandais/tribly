package com.tribly.service.common;

import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;

public abstract class TeamEntityService {

  @Inject protected TeamSecurityService securityService;
}
