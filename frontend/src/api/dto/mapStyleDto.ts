/**
 * A basemap style the clients may offer
 */
export interface MapStyleDto {
  /** Stable style identifier, e.g. 'colorful' */
  id: string
  /** Human-readable label for the style switcher */
  label: string
  /** URL of the MapLibre style document to load in light mode (or at all times when darkVariant is null) */
  url: string
  /** URL of the style document to load instead of 'url' when the client renders in dark mode. Null when the style has no dark counterpart — the client then keeps using 'url'. */
  darkVariant?: string
}
