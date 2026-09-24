package fr.pedalons.infrastructure.validation;

import fr.pedalons.dto.validation.AcceptableText;
import fr.pedalons.service.moderation.TextFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.Nullable;

/**
 * Backs {@link AcceptableText} with the {@link TextFilter} bean.
 *
 * <p>A bean, so Quarkus's validator factory injects the filter. Should a validator ever be built
 * outside CDI, it falls back to a filter of its own rather than fail open or throw.
 */
@ApplicationScoped
public class AcceptableTextValidator implements ConstraintValidator<AcceptableText, String> {

  @Inject @Nullable TextFilter textFilter;

  @Override
  public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
    TextFilter filter = textFilter;
    return (filter != null ? filter : Fallback.FILTER).isAcceptable(value);
  }

  /** Loaded on first use only. */
  private static final class Fallback {
    static final TextFilter FILTER = new TextFilter();
  }
}
