import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/participants/presentation/widgets/participants_sheet.dart';

import 'ride_fixtures.dart';

/// S12-7 — la pastille « Organisateur du groupe » est **conditionnelle**.
///
/// Trois cas, et le premier est le plus fréquent : la plupart des groupes n'ont
/// pas de meneur désigné. `leader` nul ne rend rien — surtout pas un repli sur
/// `createdBy`, qui serait le créateur de la sortie, donc la même personne sur
/// tous ses groupes. C'est précisément le défaut que la colonne `leader_id`
/// corrige.
void main() {
  const String organizerKey = 'participants.groupOrganizer';

  const PublicUserDto antoine = PublicUserDto(
    id: 'u1',
    displayName: 'Antoine Yvon',
  );

  RideGroupDto groupWith({PublicUserDto? leader, int participants = 3}) {
    return fixtureGroup(
      countParticipants: participants,
      leader: leader,
    ).copyWith(
      participants: <PublicUserDto>[
        antoine,
        const PublicUserDto(id: 'u2', displayName: 'Hélène Ebrard'),
        const PublicUserDto(id: 'u3', displayName: 'Gaby Landais'),
      ],
    );
  }

  Future<void> open(WidgetTester tester, RideGroupDto group) async {
    await tester.pumpWidget(
      ProviderScope(
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: Scaffold(body: ParticipantsSheet(group: group)),
        ),
      ),
    );
    await tester.pump();
  }

  testWidgets('sans meneur désigné, aucune pastille — le cas courant', (
    WidgetTester tester,
  ) async {
    await open(tester, groupWith());
    expect(find.text(organizerKey), findsNothing);
  });

  testWidgets('un meneur qui participe porte la pastille, et lui seul', (
    WidgetTester tester,
  ) async {
    await open(tester, groupWith(leader: antoine));
    expect(find.text(organizerKey), findsOneWidget);
  });

  testWidgets('un meneur qui ne participe pas ne crée pas de ligne', (
    WidgetTester tester,
  ) async {
    await open(
      tester,
      groupWith(
        leader: const PublicUserDto(id: 'u9', displayName: 'Meneur Absent'),
      ),
    );
    expect(find.text(organizerKey), findsNothing);
    expect(find.text('Meneur Absent'), findsNothing);
    // Les trois participants restent rendus : l'absence du meneur n'enlève
    // rien à la liste.
    expect(find.text('Antoine Yvon'), findsOneWidget);
  });

  testWidgets('la recherche filtre côté client, sans appel', (
    WidgetTester tester,
  ) async {
    final RideGroupDto big = fixtureGroup(countParticipants: 12);
    await open(tester, big);
    expect(find.text('Membre 0'), findsOneWidget);

    await tester.enterText(find.byType(TextField), 'Membre 7');
    await tester.pump(const Duration(milliseconds: 400));
    await tester.pump();

    // Le champ lui-même porte aussi le texte saisi : on compte les lignes.
    expect(find.byType(PdlPersonRow), findsOneWidget);
    expect(find.text('Membre 0'), findsNothing);
  });
}
