import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../feed/presentation/widgets/publication_feed_view.dart';
import '../widgets/team_sliver_app_bar.dart';

/// A team's feed. Same behaviour as the home feed — type chips, infinite
/// scroll, pull-to-refresh — scoped to one team.
class TeamFeedPage extends ConsumerWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const TeamFeedPage({super.key, required this.teamSlug, required this.team});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return PublicationFeedView(
      teamSlug: teamSlug,
      emptyMessage: 'teams.feed.empty'.tr(),
      leadingSlivers: [TeamSliverAppBar(team: team)],
    );
  }
}
