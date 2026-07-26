import 'dart:ui';

/// All Pedalons brand colors from BRANDING.md.
///
/// Light values use Mantine shade-6, dark values use shade-8
/// for higher contrast on dark backgrounds.
abstract final class BrandColors {
  // ── Brand identity ──────────────────────────────────────────────────────
  static const brandBlue = Color(0xFF228be6);
  static const brandOrange = Color(0xFFfd7e14);

  // ── Named palette (shade-6 light / shade-8 dark) ───────────────────────
  static const blueLight = Color(0xFF228be6);
  static const blueDark = Color(0xFF1971c2);

  static const indigoLight = Color(0xFF4c6ef5);
  static const indigoDark = Color(0xFF3b5bdb);

  static const grapeLight = Color(0xFFbe4bdb);
  static const grapeDark = Color(0xFF9c36b5);

  static const tealLight = Color(0xFF12b886);
  static const tealDark = Color(0xFF099268);

  static const greenLight = Color(0xFF40c057);
  static const greenDark = Color(0xFF2f9e44);

  static const orangeLight = Color(0xFFfd7e14);
  static const orangeDark = Color(0xFFe8590c);

  static const redLight = Color(0xFFfa5252);
  static const redDark = Color(0xFFe03131);

  static const yellowLight = Color(0xFFfab005);
  static const yellowDark = Color(0xFFf08c00);

  static const grayLight = Color(0xFF868e96);
  static const grayDark = Color(0xFF343a40);

  static const violetLight = Color(0xFF7950f2);
  static const violetDark = Color(0xFF6741d9);

  static const cyanLight = Color(0xFF15aabf);
  static const cyanDark = Color(0xFF0c8599);

  static const pinkLight = Color(0xFFe64980);
  static const pinkDark = Color(0xFFc2255c);

  static const limeLight = Color(0xFF82c91e);
  static const limeDark = Color(0xFF66a80f);

  // "dark" in Mantine is a near-black scale used for ROAD surface type.
  // In dark mode, use dark-2 (#828282) for visibility on dark backgrounds.
  static const darkLight = Color(0xFF2e2e2e);
  static const darkDark = Color(0xFF828282);

  // ── Map colors (hex strings for MapLibre paint properties) ─────────────
  static const mapRouteLineHexLight = '#228be6';
  static const mapRouteLineHexDark = '#4dabf7';
  static const mapStartMarkerHex = '#40c057';
  static const mapEndMarkerHex = '#fa5252';

  /// Picks the light or dark shade based on [brightness].
  static Color resolve(Color light, Color dark, Brightness brightness) =>
      brightness == Brightness.light ? light : dark;
}
