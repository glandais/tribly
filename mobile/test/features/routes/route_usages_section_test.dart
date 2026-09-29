import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/routes/presentation/widgets/route_usages_section.dart';
import 'package:pedalons/features/routes/providers/route_detail_provider.dart';

import '../../support/localization.dart';

/// S13 — « Utilisée dans » : un voyage rend sa plage de dates (`endDate`,
/// docs/LEDGER_*.md API-9), une sortie sa date et son heure.
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(loadTestTranslations);

  const RouteKey key = RouteKey(teamSlug: 'n-peloton', routeSlug: 'boucle');

  Future<void> pump(WidgetTester tester, List<RouteUsageDto> usages) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          routeUsagesProvider.overrideWith(
            (Ref ref, RouteKey k) async => usages,
          ),
        ],
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: const Scaffold(
            body: SingleChildScrollView(
              child: RouteUsagesSection(routeKey: key),
            ),
          ),
        ),
      ),
    );
    await tester.pump();
    await tester.pump();
  }

  // Midi UTC : le même jour quel que soit le fuseau de la machine de test.
  RouteUsageDto usage({
    required String type,
    required String slug,
    String? endDate,
  }) => RouteUsageDto(
    type: type,
    slug: slug,
    name: slug,
    dateTime: '2026-08-01T12:00:00Z',
    endDate: endDate,
    teamSlug: 'n-peloton',
    referencedDirectly: true,
    viaChildNames: const <String>[],
  );

  testWidgets('un voyage rend sa plage de dates', (WidgetTester tester) async {
    await pump(tester, <RouteUsageDto>[
      usage(type: 'TRIP', slug: 'alpes', endDate: '2026-08-04T12:00:00Z'),
    ]);

    expect(find.text('1 août → 4 août'), findsOneWidget);
  });

  testWidgets('une sortie, ou un voyage sans étape, garde sa date seule', (
    WidgetTester tester,
  ) async {
    await pump(tester, <RouteUsageDto>[
      usage(type: 'RIDE', slug: 'dimanche'),
      usage(type: 'TRIP', slug: 'nu'),
    ]);

    expect(find.textContaining('→'), findsNothing);
    expect(find.textContaining('1 août'), findsNWidgets(2));
  });
}
