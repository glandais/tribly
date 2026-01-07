package com.tribly.service.security.annotation;

import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CheckAccess {
  @Nonbinding
  EntityType entityType() default EntityType.AD;

  @Nonbinding
  ActionType action() default ActionType.READ;
}
