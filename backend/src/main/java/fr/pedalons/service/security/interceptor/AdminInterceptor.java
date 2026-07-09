package fr.pedalons.service.security.interceptor;

import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.user.User;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Admin;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Admin
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class AdminInterceptor {

  @Inject PedalonsQueryContext pedalonsQueryContext;

  @AroundInvoke
  public Object check(InvocationContext ctx) throws Exception {
    Admin ann = ctx.getMethod().getAnnotation(Admin.class);
    if (ann != null) {
      User user = pedalonsQueryContext.getUserNullable();
      if (user == null || !user.isPlatformAdmin()) {
        throw new ForbiddenException();
      }
    }
    return ctx.proceed();
  }
}
