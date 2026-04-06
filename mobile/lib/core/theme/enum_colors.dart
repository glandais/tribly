import 'dart:ui';

import '../../api/generated/models/ad_type.dart';
import '../../api/generated/models/climb_category.dart';
import '../../api/generated/models/publication_type.dart';
import '../../api/generated/models/status.dart';
import '../../api/generated/models/surface_type.dart';
import '../../api/generated/models/team_role.dart';
import '../../api/generated/models/visibility.dart';
import 'pedalons_colors.dart';

// ── Status ──────────────────────────────────────────────────────────────────

extension StatusColors on Status {
  Color color(Brightness brightness) => switch (this) {
        Status.draft => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
        Status.published => BrandColors.resolve(
            BrandColors.greenLight, BrandColors.greenDark, brightness),
        Status.cancelled => BrandColors.resolve(
            BrandColors.redLight, BrandColors.redDark, brightness),
        Status.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Team Role ───────────────────────────────────────────────────────────────

extension TeamRoleColors on TeamRole {
  Color color(Brightness brightness) => switch (this) {
        TeamRole.admin => BrandColors.resolve(
            BrandColors.grapeLight, BrandColors.grapeDark, brightness),
        TeamRole.organizer => BrandColors.resolve(
            BrandColors.blueLight, BrandColors.blueDark, brightness),
        TeamRole.member => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
        TeamRole.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Surface Type ────────────────────────────────────────────────────────────

extension SurfaceTypeColors on SurfaceType {
  Color color(Brightness brightness) => switch (this) {
        SurfaceType.road => BrandColors.resolve(
            BrandColors.darkLight, BrandColors.darkDark, brightness),
        SurfaceType.gravel => BrandColors.resolve(
            BrandColors.orangeLight, BrandColors.orangeDark, brightness),
        SurfaceType.mtb => BrandColors.resolve(
            BrandColors.greenLight, BrandColors.greenDark, brightness),
        SurfaceType.mixed => BrandColors.resolve(
            BrandColors.tealLight, BrandColors.tealDark, brightness),
        SurfaceType.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Climb Category ──────────────────────────────────────────────────────────

extension ClimbCategoryColors on ClimbCategory {
  Color color(Brightness brightness) => switch (this) {
        ClimbCategory.hc => BrandColors.resolve(
            BrandColors.grapeLight, BrandColors.grapeDark, brightness),
        ClimbCategory.cat1 => BrandColors.resolve(
            BrandColors.redLight, BrandColors.redDark, brightness),
        ClimbCategory.cat2 => BrandColors.resolve(
            BrandColors.orangeLight, BrandColors.orangeDark, brightness),
        ClimbCategory.cat3 => BrandColors.resolve(
            BrandColors.yellowLight, BrandColors.yellowDark, brightness),
        ClimbCategory.cat4 => BrandColors.resolve(
            BrandColors.greenLight, BrandColors.greenDark, brightness),
        ClimbCategory.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Ad Type ─────────────────────────────────────────────────────────────────

extension AdTypeColors on AdType {
  Color color(Brightness brightness) => switch (this) {
        AdType.sale => BrandColors.resolve(
            BrandColors.greenLight, BrandColors.greenDark, brightness),
        AdType.rental => BrandColors.resolve(
            BrandColors.indigoLight, BrandColors.indigoDark, brightness),
        AdType.wanted => BrandColors.resolve(
            BrandColors.orangeLight, BrandColors.orangeDark, brightness),
        AdType.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Visibility ──────────────────────────────────────────────────────────────

extension VisibilityColors on Visibility {
  Color color(Brightness brightness) => switch (this) {
        Visibility.public => BrandColors.resolve(
            BrandColors.blueLight, BrandColors.blueDark, brightness),
        Visibility.publicUnlisted => BrandColors.resolve(
            BrandColors.orangeLight, BrandColors.orangeDark, brightness),
        Visibility.team => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
        Visibility.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}

// ── Publication Type ────────────────────────────────────────────────────────

extension PublicationTypeColors on PublicationType {
  Color color(Brightness brightness) => switch (this) {
        PublicationType.ride => BrandColors.resolve(
            BrandColors.blueLight, BrandColors.blueDark, brightness),
        PublicationType.post => BrandColors.resolve(
            BrandColors.grapeLight, BrandColors.grapeDark, brightness),
        PublicationType.trip => BrandColors.resolve(
            BrandColors.tealLight, BrandColors.tealDark, brightness),
        PublicationType.$unknown => BrandColors.resolve(
            BrandColors.grayLight, BrandColors.grayDark, brightness),
      };
}
