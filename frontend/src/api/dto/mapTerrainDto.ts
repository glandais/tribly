/**
 * The elevation source used to shade the relief
 */
export interface MapTerrainDto {
  /** URL of a TileJSON document describing raster-DEM tiles. The document declares the encoding; the clients do not. */
  url: string
  /** Deepest zoom the provider renders. Honoured by the web, which sets it on the source; the mobile SDKs take their zoom range from the TileJSON instead. */
  maxZoom: number
}
