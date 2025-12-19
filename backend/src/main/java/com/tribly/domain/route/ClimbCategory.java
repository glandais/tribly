package com.tribly.domain.route;

/**
 * Cycling climb categorization based on elevation gain and gradient.
 * Categories follow standard cycling classification.
 */
public enum ClimbCategory {
  /**
   * Hors Catégorie (Beyond Category) - Most difficult climbs
   * Typically > 1500m elevation gain or > 1000m with > 8% average gradient
   */
  HC,

  /**
   * Category 1 - Very difficult climbs
   * Typically 800-1500m elevation gain
   */
  CAT1,

  /**
   * Category 2 - Difficult climbs
   * Typically 500-800m elevation gain
   */
  CAT2,

  /**
   * Category 3 - Moderate climbs
   * Typically 300-500m elevation gain
   */
  CAT3,

  /**
   * Category 4 - Easy climbs
   * Typically < 300m elevation gain
   */
  CAT4
}
