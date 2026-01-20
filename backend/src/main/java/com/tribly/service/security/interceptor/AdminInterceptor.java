package com.tribly.service.security.interceptor;

import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.user.User;
import com.tribly.enums.PlatformRole;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Admin;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Admin
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class AdminInterceptor {

  @Inject TriblyQueryContext triblyQueryContext;

  @AroundInvoke
  public Object check(InvocationContext ctx) throws Exception {
    Admin ann = ctx.getMethod().getAnnotation(Admin.class);
    if (ann != null) {
      User user = triblyQueryContext.getUserNullable();
      if (user == null || user.getPlatformRole() != PlatformRole.PLATFORM_ADMIN) {
        throw new ForbiddenException();
      }
    }
    return ctx.proceed();
  }
}
