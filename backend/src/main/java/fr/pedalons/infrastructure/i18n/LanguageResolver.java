package fr.pedalons.infrastructure.i18n;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.vertx.ext.web.LanguageHeader;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.Locale;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * The language to use for user-facing content of the current request, from {@code Accept-Language}.
 *
 * <p>Both clients send the language the user actually picked rather than the raw browser
 * preference: the web front-end sets the header from {@code i18next.language} (axios interceptor)
 * and the mobile app from its locale. So the first supported tag of the header is the user's
 * choice, not a guess.
 *
 * <p>Falls back to {@link #DEFAULT_LANGUAGE} when the header is absent, asks only for languages we
 * have no content for, or when there is no HTTP request at all (schedulers).
 */
@RequestScoped
public class LanguageResolver {

  public static final String DEFAULT_LANGUAGE = "fr";

  /** Languages we have email templates and UI translations for. */
  private static final Set<String> SUPPORTED_LANGUAGES = Set.of("fr", "en");

  @Inject CurrentVertxRequest currentVertxRequest;

  private @Nullable String language;

  public String getLanguage() {
    if (language == null) {
      language = resolve();
    }
    return language;
  }

  private String resolve() {
    RoutingContext context = currentVertxRequest.getCurrent();
    if (context == null) {
      return DEFAULT_LANGUAGE;
    }
    // Already sorted by descending q-value, so the first supported tag wins.
    for (LanguageHeader header : context.acceptableLanguages()) {
      String tag = header.tag().toLowerCase(Locale.ROOT);
      if (SUPPORTED_LANGUAGES.contains(tag)) {
        return tag;
      }
    }
    return DEFAULT_LANGUAGE;
  }

  // For testing purposes
  public void setLanguageForTest(@Nullable String language) {
    this.language = language;
  }
}
