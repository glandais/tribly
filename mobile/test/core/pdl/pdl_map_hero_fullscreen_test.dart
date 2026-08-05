import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';

/// Le sélecteur de fond, **en plein écran**.
///
/// La page plein écran est une route dont le constructeur n'est appelé qu'une
/// fois : tout ce que le hero y recopiait à la poussée y restait figé, et
/// choisir un fond n'y changeait rien. Le test tient les deux moitiés — la
/// feuille s'ouvre et ses lignes sont **appuyables**, et l'appui remonte
/// jusqu'à l'écran, qui republie une configuration que la page plein écran
/// affiche.
void main() {
  const List<PdlMapStyleOption> styles = <PdlMapStyleOption>[
    PdlMapStyleOption(id: 'plan', label: 'Plan'),
    PdlMapStyleOption(id: 'satellite', label: 'Satellite'),
  ];

  const PdlMapHeroLabels labels = PdlMapHeroLabels(
    enterFullscreen: 'Plein écran',
    exitFullscreen: 'Quitter le plein écran',
    chooseBackground: 'Choisir le fond',
    backgroundSheetTitle: 'Fond de carte',
  );

  /// L'écran hôte : il tient le fond choisi, comme le fait un provider.
  Widget host({required List<String> picked}) => MaterialApp(
    theme: PedalonsTheme.build(Brightness.light),
    home: StatefulBuilder(
      builder: (BuildContext context, StateSetter setState) {
        final String selected = picked.isEmpty ? 'plan' : picked.last;
        return Scaffold(
          body: PdlMapHero(
            labels: labels,
            styles: styles,
            selectedStyleId: selected,
            onStyleSelected: (String id) => setState(() => picked.add(id)),
            // La carte est réduite à son étiquette : MapLibre ne tourne pas
            // dans un test, et ce qui est vérifié ici est ce que le hero lui
            // passe, pas ce qu'elle en fait.
            mapBuilder: (BuildContext context) => Center(child: Text(selected)),
          ),
        );
      },
    ),
  );

  testWidgets('le fond choisi en plein écran atteint la carte', (
    WidgetTester tester,
  ) async {
    final List<String> picked = <String>[];
    await tester.pumpWidget(host(picked: picked));

    // Plein écran.
    await tester.tap(find.bySemanticsLabel('Plein écran'));
    await tester.pumpAndSettle();
    expect(find.text('Quitter le plein écran'), findsNothing);

    // La feuille des fonds, depuis la page plein écran.
    await tester.tap(find.bySemanticsLabel('Choisir le fond'));
    await tester.pumpAndSettle();
    expect(find.text('Fond de carte'), findsOneWidget);
    expect(find.text('Satellite'), findsOneWidget);

    // L'appui remonte à l'écran…
    await tester.tap(find.text('Satellite'));
    await tester.pumpAndSettle();
    expect(picked, <String>['satellite']);

    // …et redescend jusqu'à la carte de la page plein écran, qui n'a pas été
    // reconstruite par la route mais par la republication du hero.
    expect(find.text('satellite'), findsOneWidget);
  });
}
