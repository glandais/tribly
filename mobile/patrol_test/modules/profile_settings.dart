import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pdl/pdl.dart';

import 'module.dart';

/// The profile's settings: identity, display preferences, data export, sessions.
final class ProfileSettings extends Module {
  ProfileSettings(super.$);

  // ── Identity ────────────────────────────────────────────────────────────

  /// Types [name] in « Nom affiché » and taps « Enregistrer ».
  Future<void> renameTo(String name) async {
    await (await scrolledTo(keys.profile.displayNameField)).enterText(name);
    await $(keys.profile.displayNameSave).tap();
  }

  // ── Display preferences ─────────────────────────────────────────────────

  Future<void> chooseUnits(UnitSystem units) async {
    await (await scrolledTo(keys.profile.unitSegment(units))).tap();
  }

  Future<void> waitUntilUnitsExampleSays(String text) => _waitUntil(
    () => shows(keys.profile.unitsExample, text),
    'units example « $text »',
  );

  Future<void> chooseTheme(ThemePreference theme) async {
    await (await scrolledTo(keys.profile.themeSegment(theme))).tap();
  }

  /// The brightness the app's screens are drawn in right now.
  Brightness get appBrightness =>
      Theme.of($.tester.element($(keys.profile.languageRow))).brightness;

  Future<void> waitUntilAppIs(Brightness brightness) =>
      _waitUntil(() => appBrightness == brightness, 'the app in $brightness');

  /// « Langue », then the language of [code] in its sheet.
  Future<void> chooseLanguage(String code) async {
    await (await scrolledTo(keys.profile.languageRow)).tap();
    await $(keys.profile.languageOption(code)).tap();
    await waitUntilGone(keys.profile.languageOption(code));
  }

  Future<void> waitUntilLanguageRowSays(String text) => _waitUntil(
    () => shows(keys.profile.languageRow, text),
    'the language row « $text »',
  );

  Future<void> toggleContactable() async {
    await (await scrolledTo(keys.profile.contactableSwitch)).tap();
  }

  bool get contactableIsOn =>
      ($(keys.profile.contactableSwitch).evaluate().single.widget as PdlSwitch)
          .value;

  // ── Data export ─────────────────────────────────────────────────────────

  Future<void> requestExport() async {
    await (await scrolledTo(keys.profile.dataExportButton)).tap();
  }

  Future<void> waitUntilExportStatusSays(
    String text, {
    Duration timeout = const Duration(seconds: 15),
  }) async {
    await scrolledTo(keys.profile.dataExportStatus);
    final deadline = DateTime.now().add(timeout);
    while (!shows(keys.profile.dataExportStatus, text)) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure(
          'the export status is « $exportStatus » after $timeout, not « $text »',
        );
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  String get exportStatus =>
      ($(keys.profile.dataExportStatus).evaluate().single.widget as Text)
          .data ??
      '';

  // ── Sessions ────────────────────────────────────────────────────────────

  /// « Déconnecter tous les appareils », then its confirmation.
  Future<void> logOutEverywhere() async {
    await (await scrolledTo(keys.profile.logoutAllButton)).tap();
    await $(keys.profile.confirmDestructiveButton).tap();
  }

  // ── Paired devices ──────────────────────────────────────────────────────

  Future<void> waitUntilDeviceIsListed(String id) async {
    await scrolledTo(keys.profile.pairedDevice(id));
  }

  /// The cross of the device [id], then the confirmation.
  Future<void> unpairDevice(String id) async {
    await (await scrolledTo(keys.profile.unpairDevice(id))).tap();
    await $(keys.profile.confirmDestructiveButton).tap();
  }

  Future<void> waitUntilDeviceIsGone(String id) => _waitUntil(
    () => !$(keys.profile.pairedDevice(id)).exists,
    'device $id unlisted',
  );

  Future<void> _waitUntil(
    bool Function() condition,
    String what, {
    Duration timeout = const Duration(seconds: 10),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (!condition()) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('not shown after $timeout: $what');
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }
}
