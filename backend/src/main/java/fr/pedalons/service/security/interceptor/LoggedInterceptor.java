package fr.pedalons.service.security.interceptor;

import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggedInterceptor {

  @Inject PedalonsQueryContext pedalonsQueryContext;

  @AroundInvoke
  public Object check(InvocationContext ctx) throws Exception {
    Logged ann = ctx.getMethod().getAnnotation(Logged.class);
    if (ann != null && pedalonsQueryContext.getUserNullable() == null) {
      throw new ForbiddenException();
    }
    return ctx.proceed();
  }
}
