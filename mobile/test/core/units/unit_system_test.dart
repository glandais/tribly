import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/models/unit_system.dart';
import 'package:pedalons/core/units/unit_system.dart';

/// La table de conversion, testée sans Flutter ni locale : ce sont des
/// nombres, et rien d'autre.
void main() {
  group('longDistance', () {
    test('metric : des mètres vers des kilomètres', () {
      expect(UnitSystem.metric.longDistance(46300), closeTo(46.3, 1e-9));
      expect(UnitSystem.metric.longDistanceSymbol, 'km');
    });

    test('imperial : des mètres vers des milles', () {
      expect(UnitSystem.imperial.longDistance(46300), closeTo(28.7695, 1e-3));
      expect(
        UnitSystem.imperial.longDistance(kMetersPerMile),
        closeTo(1, 1e-9),
      );
      expect(UnitSystem.imperial.longDistanceSymbol, 'mi');
    });
  });

  group('shortDistance', () {
    test('metric : les mètres restent des mètres', () {
      expect(UnitSystem.metric.shortDistance(9840), 9840);
      expect(UnitSystem.metric.shortDistanceSymbol, 'm');
    });

    test('imperial : des mètres vers des pieds', () {
      expect(UnitSystem.imperial.shortDistance(1000), closeTo(3280.84, 0.01));
      expect(UnitSystem.imperial.shortDistanceSymbol, 'ft');
    });
  });

  group('speed', () {
    test('metric : les km/h restent des km/h', () {
      expect(UnitSystem.metric.speed(25), 25);
      expect(UnitSystem.metric.speedSymbol, 'km/h');
    });

    test('imperial : des km/h vers des mph', () {
      expect(UnitSystem.imperial.speed(25), closeTo(15.534, 1e-3));
      expect(UnitSystem.imperial.speedSymbol, 'mph');
    });
  });

  group('conversions inverses', () {
    test('un aller-retour ne perd rien', () {
      for (final UnitSystem units in UnitSystem.values) {
        expect(
          units.longDistanceToMeters(units.longDistance(46300)),
          closeTo(46300, 1e-6),
        );
        expect(
          units.shortDistanceToMeters(units.shortDistance(9840)),
          closeTo(9840, 1e-6),
        );
      }
    });
  });

  group(r'$unknown', () {
    test('se comporte exactement comme metric', () {
      const UnitSystem unknown = UnitSystem.$unknown;
      expect(unknown.isImperial, isFalse);
      expect(
        unknown.longDistance(46300),
        UnitSystem.metric.longDistance(46300),
      );
      expect(unknown.longDistanceSymbol, UnitSystem.metric.longDistanceSymbol);
      expect(
        unknown.shortDistance(9840),
        UnitSystem.metric.shortDistance(9840),
      );
      expect(
        unknown.shortDistanceSymbol,
        UnitSystem.metric.shortDistanceSymbol,
      );
      expect(unknown.speed(25), UnitSystem.metric.speed(25));
      expect(unknown.speedSymbol, UnitSystem.metric.speedSymbol);
      expect(unknown.labelKey, UnitSystem.metric.labelKey);
    });
  });
}
