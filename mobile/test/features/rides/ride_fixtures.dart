import 'package:pedalons/api/generated/export.dart';

/// Jeux de données du lot 2 pour les sorties.
///
/// Partagés par les tests de la carte de groupe, du contrôleur d'inscription
/// et de l'écran : construire un `RideDto` à la main coûte une quinzaine de
/// champs obligatoires, et deux copies de ce coût divergent dès qu'un champ
/// s'ajoute au contrat.
RideGroupDto fixtureGroup({
  String id = 'g1',
  String name = 'Chill Route Long',
  bool registered = false,
  bool full = false,
  int countParticipants = 3,
  int? maxParticipants = 16,
  int sortOrder = 0,
  PublicUserDto? leader,
  String? routeSlug,
}) => RideGroupDto(
  id: id,
  name: name,
  countParticipants: countParticipants,
  participants: <PublicUserDto>[
    for (int i = 0; i < countParticipants; i++)
      PublicUserDto(id: 'u$i', displayName: 'Membre $i'),
  ],
  sortOrder: sortOrder,
  registered: registered,
  full: full,
  maxParticipants: maxParticipants,
  averageSpeed: 26,
  distance: 66500,
  elevationGain: 413,
  leader: leader,
  routeSlug: routeSlug,
);

RideDto fixtureRide({
  String status = 'PUBLISHED',
  String dateTime = '2099-07-29T19:30:00Z',
  bool registered = false,
  String? registeredGroupId,
  bool full = false,
  List<RideGroupDto>? groups,
}) {
  final List<RideGroupDto> gs = groups ?? <RideGroupDto>[fixtureGroup()];
  return RideDto(
    type: 'RIDE',
    team: const TeamPublicationDto(
      id: 't1',
      slug: 'n-peloton',
      name: 'N-Peloton',
      visibility: 'PUBLIC',
    ),
    id: 'r1',
    slug: 'np-665',
    name: 'N-Peloton #665',
    media: const MediaDto(
      markdown: '',
      assets: AssetsDto(images: <AssetDto>[], attachments: <AssetDto>[]),
    ),
    dateTime: dateTime,
    status: status,
    visibility: 'PUBLIC',
    participantCount: 40,
    groupCount: gs.length,
    groups: gs,
    topParticipants: const <PublicUserDto>[],
    deleted: false,
    registered: registered,
    registeredGroupId: registeredGroupId,
    full: full,
  );
}
