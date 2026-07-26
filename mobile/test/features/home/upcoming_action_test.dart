import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/home/providers/upcoming_provider.dart';

/// S11-4 — la règle de bouton du carrousel, **entièrement calculable côté
/// client**.
///
/// Elle repose sur `registered`, `full` et `groupCount`, tous trois portés par
/// une ligne de liste depuis la 1.3.0. C'est ce qui permet à l'accueil de
/// décider sans un seul appel supplémentaire — la v1 aurait dû charger le
/// détail de chaque carte pour savoir quoi écrire sur son bouton.
void main() {
  PublicationDtoRide ride({
    bool registered = false,
    bool full = false,
    int groupCount = 1,
  }) => PublicationDtoRide(
    team: const TeamPublicationDto(
      id: 't1',
      slug: 'n-peloton',
      name: 'N-Peloton',
      visibility: 'PUBLIC',
    ),
    id: 'r1',
    slug: 'np-666',
    name: 'N-Peloton #666',
    media: const MediaDto(
      markdown: '',
      assets: AssetsDto(images: <AssetDto>[], attachments: <AssetDto>[]),
    ),
    dateTime: '2099-08-05T19:30:00Z',
    status: 'PUBLISHED',
    visibility: 'PUBLIC',
    participantCount: 2,
    groupCount: groupCount,
    groups: const <RideGroupDto>[],
    topParticipants: const <PublicUserDto>[],
    deleted: false,
    registered: registered,
    full: full,
  );

  test('inscrit : un badge, pas de bouton', () {
    expect(upcomingAction(ride(registered: true)), UpcomingAction.registered);
  });

  test('complet prime sur le nombre de groupes', () {
    expect(
      upcomingAction(ride(full: true, groupCount: 4)),
      UpcomingAction.full,
    );
  });

  test('un seul groupe : « Rejoindre »', () {
    expect(upcomingAction(ride()), UpcomingAction.join);
  });

  test('plusieurs groupes : « Choisir un groupe »', () {
    expect(upcomingAction(ride(groupCount: 10)), UpcomingAction.chooseGroup);
  });

  test('l\'inscription prime sur tout le reste', () {
    // Une sortie pleine où l'on est déjà inscrit se lit « Inscrit », pas
    // « Complet » : l'information utile est qu'on y a sa place.
    expect(
      upcomingAction(ride(registered: true, full: true, groupCount: 10)),
      UpcomingAction.registered,
    );
  });

  test('la ligne de liste ne porte jamais les groupes', () {
    // C'est le fait qui interdit à « Rejoindre » d'inscrire sur place :
    // l'identifiant du groupe unique n'existe pas ici.
    expect(ride().groups, isEmpty);
  });
}
