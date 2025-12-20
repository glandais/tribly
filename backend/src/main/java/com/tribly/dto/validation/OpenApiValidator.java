package com.tribly.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.NullMarked;

public class OpenApiValidator implements ConstraintValidator<ValidateSchema, Object> {
  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
    if (obj == null) return true;

    Class<?> clazz = obj.getClass();
    boolean isNullMarked = clazz.getPackage().isAnnotationPresent(NullMarked.class);

    if (!isNullMarked) {
      return true;
    }

    for (Field field : clazz.getDeclaredFields()) {
      boolean isRequired = isRequired(field);
      if (isRequired) {
        field.setAccessible(true);
        try {
          if (field.get(obj) == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("must not be null")
                .addPropertyNode(field.getName())
                .addConstraintViolation();
            return false;
          }
        } catch (IllegalAccessException e) {
          return true;
        }
      }
    }
    return true;
  }

  private boolean isRequired(Field field) {
    if (field.isAnnotationPresent(Schema.class)) {
      Schema schema = field.getAnnotation(Schema.class);
      if (schema.required()) {
        return true;
      }
      return !schema.nullable();
    }
    return false;
  }
}
