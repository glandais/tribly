import 'package:pedalons/api/generated/export.dart';

/// Jeux de données du lot 5 pour les équipes, les membres et la découverte.
const MediaDto kEmptyMedia = MediaDto(
  markdown: '',
  assets: AssetsDto(images: <AssetDto>[], attachments: <AssetDto>[]),
);

const TeamPublicationDto kMemberTeam = TeamPublicationDto(
  id: 't1',
  slug: 'n-peloton',
  name: 'N-Peloton',
  visibility: 'PUBLIC',
);

TeamDetailDto fixtureTeam({
  String slug = 'n-peloton',
  String name = 'N-Peloton',
  String visibility = 'PUBLIC',
  bool joinable = true,
  String? role,
  String? excerpt = 'Le club du Nord.',
  int memberCount = 40,
}) => TeamDetailDto(
  id: 'team-$slug',
  slug: slug,
  name: name,
  about: kEmptyMedia,
  visibility: visibility,
  enableTrips: true,
  enableAds: true,
  enablePosts: true,
  enableRides: true,
  enableRoutes: true,
  visibilityEditable: true,
  joinable: joinable,
  addMemberAllowed: true,
  memberCount: memberCount,
  upcomingRideCount: 3,
  routeCount: 12,
  createdAt: '2024-01-01T00:00:00Z',
  excerpt: excerpt,
  role: role,
);

MemberDto fixtureMember({
  required int index,
  String role = 'MEMBER',
  String? joinedAt = '2019-03-08T10:00:00Z',
}) => MemberDto(
  team: kMemberTeam,
  id: 'm$index',
  user: PublicUserDto(id: 'u$index', displayName: 'Membre $index'),
  role: role,
  joinedAt: joinedAt,
);

List<MemberDto> fixtureMembers(int count, {int from = 0}) => <MemberDto>[
  for (int i = from; i < from + count; i++) fixtureMember(index: i),
];

List<TeamDetailDto> fixtureTeams(int count, {int from = 0}) => <TeamDetailDto>[
  for (int i = from; i < from + count; i++)
    fixtureTeam(slug: 'equipe-$i', name: 'Équipe $i'),
];
