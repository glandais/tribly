package com.tribly.service.security.interceptor;

import com.tribly.common.exception.ForbiddenException;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggedInterceptor {

  @Inject TriblyQueryContext triblyQueryContext;

  @AroundInvoke
  public Object check(InvocationContext ctx) throws Exception {
    Logged ann = ctx.getMethod().getAnnotation(Logged.class);
    if (ann != null && triblyQueryContext.getUserNullable() == null) {
      throw new ForbiddenException();
    }
    return ctx.proceed();
  }
}
