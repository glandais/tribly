package com.tribly.infrastructure.exception;

import lombok.Getter;

@Getter
public class NewSlugException extends BusinessException {
  private final String oldSlug;
  private final String teamSlug;
  private final String newSlug;

  public NewSlugException(String oldSlug, String teamSlug, String newSlug) {
    super("Slug has changed", ErrorType.NEW_SLUG);
    this.oldSlug = oldSlug;
    this.teamSlug = teamSlug;
    this.newSlug = newSlug;
  }
}
