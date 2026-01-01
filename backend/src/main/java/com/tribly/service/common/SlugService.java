package com.tribly.service.common;

import com.ibm.icu.text.Transliterator;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ApplicationScoped
public class SlugService {
  private static final String ASCII =
      "Cyrillic-Latin; Any-Latin; Latin-ASCII; [^\\p{Print}] Remove";
  private static final Pattern PATTERN_NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]+");
  private static final Pattern PATTERN_TRIM_DASH = Pattern.compile("^-|-$");
  private static final String EMPTY = "";
  private static final String HYPHEN = "-";

  public String generateSlug(String name, Function<String, Boolean> checkExists) {
    String baseSlug = slugify(name);
    String slug = baseSlug;
    int i = 1;
    while (checkExists.apply(slug)) {
      slug = baseSlug + "-" + i;
      i++;
    }
    return slug;
  }

  public static String slugify(String name) {
    return Optional.of(name)
        // remove leading and trailing whitespaces
        .map(String::trim)
        // run subsequent calls only if string is not empty
        .filter(Predicate.not(EMPTY::equals))
        // transliterate or normalize
        .map(SlugService::transliterate)
        // replace non-alphanumeric chars with hyphen
        .map(str -> PATTERN_NON_ALPHANUMERIC.matcher(str).replaceAll(HYPHEN))
        // remove leading and trailing dashes
        .map(str -> PATTERN_TRIM_DASH.matcher(str).replaceAll(EMPTY))
        // convert to lower case if needed
        .map(String::toLowerCase)
        // return empty string if input is null or empty
        .orElse(EMPTY);
  }

  private static String transliterate(final String input) {
    return Transliterator.getInstance(ASCII).transliterate(input);
  }
}
