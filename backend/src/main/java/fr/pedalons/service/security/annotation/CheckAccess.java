package fr.pedalons.service.security.annotation;

import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
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
