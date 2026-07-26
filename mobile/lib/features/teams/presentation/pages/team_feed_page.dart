import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../feed/presentation/widgets/publication_feed_view.dart';

/// A team's feed. Same behaviour as the home feed — type chips, infinite
/// scroll, pull-to-refresh — scoped to one team.
///
/// The team header is no longer its business: `TeamHomePage` owns it, together
/// with the team's loading and error state. What the page receives instead is
/// the row of sections, as a pinned sliver of its own scroll view.
class TeamFeedPage extends ConsumerWidget {
  final String teamSlug;
  final TeamDetailDto team;

  /// The pinned section row, above the feed's own filters.
  final Widget? toolbar;

  /// Slivers rendus au-dessus de la rangée de sections — l'en-tête d'équipe
  /// interpolé et les cellules chiffrées.
  final List<Widget> leadingSlivers;

  const TeamFeedPage({
    super.key,
    required this.teamSlug,
    required this.team,
    this.toolbar,
    this.leadingSlivers = const <Widget>[],
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return PublicationFeedView(
      teamSlug: teamSlug,
      emptyMessage: 'teams.feed.empty'.tr(),
      leadingSlivers: <Widget>[...leadingSlivers, ?toolbar],
    );
  }
}
