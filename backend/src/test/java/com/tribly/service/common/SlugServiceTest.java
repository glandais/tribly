package com.tribly.service.common;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SlugServiceTest {

  @Inject SlugService slugService;

  @Nested
  class Slugify {

    @Test
    void shouldConvertSimpleName() {
      String result = SlugService.slugify("Hello World");

      assertEquals("hello-world", result);
    }

    @Test
    void shouldHandleSpecialCharacters() {
      String result = SlugService.slugify("Côte d'Azur & Beyond!");

      assertEquals("cote-d-azur-beyond", result);
    }

    @Test
    void shouldConvertUnderscoresToHyphens() {
      String result = SlugService.slugify("hello_world_test");

      assertEquals("hello-world-test", result);
    }

    @Test
    void shouldHandleAccentedCharacters() {
      String result = SlugService.slugify("Café élégant");

      assertEquals("cafe-elegant", result);
    }

    @Test
    void shouldHandleNumbers() {
      String result = SlugService.slugify("Route 66");

      assertEquals("route-66", result);
    }

    @Test
    void shouldHandleEmptyString() {
      String result = SlugService.slugify("");

      assertEquals("", result);
    }

    @Test
    void shouldTrimWhitespace() {
      String result = SlugService.slugify("  Trimmed  ");

      assertEquals("trimmed", result);
    }

    @Test
    void shouldHandleMultipleSpaces() {
      String result = SlugService.slugify("Multiple   Spaces   Here");

      assertEquals("multiple-spaces-here", result);
    }
  }

  @Nested
  class GenerateSlug {

    @Test
    void shouldReturnBaseSlugWhenNotExists() {
      String result = slugService.generateSlug("Test Name", slug -> false);

      assertEquals("test-name", result);
    }

    @Test
    void shouldAppendNumberWhenSlugExists() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("test-name");

      String result = slugService.generateSlug("Test Name", existingSlugs::contains);

      assertEquals("test-name-1", result);
    }

    @Test
    void shouldIncrementNumberUntilUnique() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("test-name");
      existingSlugs.add("test-name-1");
      existingSlugs.add("test-name-2");

      String result = slugService.generateSlug("Test Name", existingSlugs::contains);

      assertEquals("test-name-3", result);
    }

    @Test
    void shouldHandleManyCollisions() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("route");
      for (int i = 1; i <= 100; i++) {
        existingSlugs.add("route-" + i);
      }

      String result = slugService.generateSlug("Route", existingSlugs::contains);

      assertEquals("route-101", result);
    }

    @Test
    void shouldWorkWithSpecialCharactersInName() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("cafe-paris");

      String result = slugService.generateSlug("Café Paris", existingSlugs::contains);

      assertEquals("cafe-paris-1", result);
    }
  }
}
