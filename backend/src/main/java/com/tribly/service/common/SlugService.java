package com.tribly.service.common;

import com.github.slugify.Slugify;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.function.Function;

@ApplicationScoped
public class SlugService {
  static final Slugify slg = Slugify.builder().build();

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
    return slg.slugify(name);
  }
}
