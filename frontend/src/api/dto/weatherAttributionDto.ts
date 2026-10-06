/**
 * Who the forecast comes from. Its licence (CC BY 4.0) asks every display to credit it: show the name, linked to the url.
 */
export interface WeatherAttributionDto {
  /** Name to display, e.g. "Open-Meteo.com" */
  name: string
  /** Link of the credit */
  url: string
}
