package com.tribly.service.security.interceptor;

import com.tribly.service.security.SecurityVerifier;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.List;

@CheckAccess
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class CheckAccessInterceptor {

  @Inject TriblyQueryContext context;

  @Inject SecurityVerifier securityVerifier;

  @AroundInvoke
  public Object check(InvocationContext ctx) throws Exception {
    CheckAccess ann = ctx.getMethod().getAnnotation(CheckAccess.class);
    if (ann != null) {
      Object[] parameters = ctx.getParameters();
      securityVerifier.verifyAccess(
          ann.entityType(),
          ann.action(),
          parameters == null ? List.of() : Arrays.asList(parameters));
    }
    return ctx.proceed();
  }
}
