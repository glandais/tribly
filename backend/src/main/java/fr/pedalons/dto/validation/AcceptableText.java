package fr.pedalons.dto.validation;

import fr.pedalons.infrastructure.validation.AcceptableTextValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The text passes the publication filter ({@code TextFilter}). A violation answers {@code 400
 * CONTENT_REJECTED} rather than {@code VALIDATION}: see {@code GlobalExceptionMapper}. Null is
 * acceptable — pair with {@code @NotBlank} where the field is required.
 */
@Constraint(validatedBy = AcceptableTextValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptableText {
  String message() default "contains a term that cannot be published";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
