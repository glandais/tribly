package com.tribly.service.common;

import com.github.slugify.Slugify;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.function.Function;
import java.util.regex.Pattern;

@ApplicationScoped
public class SlugService {
  static final Slugify slg = Slugify.builder().underscoreSeparator(true).build();
  private static final Pattern PATTERN_TRIM_DASH = Pattern.compile("^-|-$");
  private static final String EMPTY = "";

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
    String slug = slg.slugify(name).replace('_', '-');
    slug = PATTERN_TRIM_DASH.matcher(slug).replaceAll(EMPTY);
    return slug;
  }
}
