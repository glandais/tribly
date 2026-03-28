package fr.pedalons.dto.validation;

import fr.pedalons.infrastructure.validation.OpenApiValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OpenApiValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateSchema {
  String message() default "Doesn't match schema";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
