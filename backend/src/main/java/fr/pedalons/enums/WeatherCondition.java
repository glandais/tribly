package fr.pedalons.enums;

/**
 * The WMO weather interpretation code, folded into what a rider decides on.
 *
 * <table>
 *   <caption>WMO code → condition</caption>
 *   <tr><td>0</td><td>CLEAR</td></tr>
 *   <tr><td>1</td><td>MOSTLY_CLEAR</td></tr>
 *   <tr><td>2</td><td>PARTLY_CLOUDY</td></tr>
 *   <tr><td>3</td><td>OVERCAST</td></tr>
 *   <tr><td>45, 48</td><td>FOG</td></tr>
 *   <tr><td>51, 53, 55</td><td>DRIZZLE</td></tr>
 *   <tr><td>56, 57, 66, 67</td><td>FREEZING_RAIN</td></tr>
 *   <tr><td>61, 63</td><td>RAIN</td></tr>
 *   <tr><td>65</td><td>HEAVY_RAIN</td></tr>
 *   <tr><td>71, 73, 75, 77, 85, 86</td><td>SNOW</td></tr>
 *   <tr><td>80, 81, 82</td><td>SHOWERS</td></tr>
 *   <tr><td>95, 96, 99</td><td>THUNDERSTORM</td></tr>
 *   <tr><td>other</td><td>OVERCAST</td></tr>
 * </table>
 */
public enum WeatherCondition {
  CLEAR,
  MOSTLY_CLEAR,
  PARTLY_CLOUDY,
  OVERCAST,
  FOG,
  DRIZZLE,
  RAIN,
  HEAVY_RAIN,
  FREEZING_RAIN,
  SHOWERS,
  SNOW,
  THUNDERSTORM;

  /** The condition of a WMO code; an unknown code reads as {@link #OVERCAST}. */
  public static WeatherCondition fromWmo(int code) {
    return switch (code) {
      case 0 -> CLEAR;
      case 1 -> MOSTLY_CLEAR;
      case 2 -> PARTLY_CLOUDY;
      case 3 -> OVERCAST;
      case 45, 48 -> FOG;
      case 51, 53, 55 -> DRIZZLE;
      case 56, 57, 66, 67 -> FREEZING_RAIN;
      case 61, 63 -> RAIN;
      case 65 -> HEAVY_RAIN;
      case 71, 73, 75, 77, 85, 86 -> SNOW;
      case 80, 81, 82 -> SHOWERS;
      case 95, 96, 99 -> THUNDERSTORM;
      default -> OVERCAST;
    };
  }
}
