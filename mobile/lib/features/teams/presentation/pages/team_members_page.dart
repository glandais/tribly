import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../../../../core/pdl/pdl.dart';

/// Public member list of a team.
///
/// Placeholder: the real screen (member cards, roles, profile links) is
/// delivered by batch 5. Not to be confused with the web-only admin screen
/// under `/admin/members`.
///
/// A **section body**, not a screen of its own: the header, the row of
/// sections and the team's loading state all belong to `TeamHomePage`, so the
/// page renders neither `Scaffold` nor app bar — hence the empty state rather
/// than `ComingSoonPage`, which carries both.
class TeamMembersPage extends StatelessWidget {
  final String teamSlug;

  const TeamMembersPage({super.key, required this.teamSlug});

  @override
  Widget build(BuildContext context) => Center(
    child: SingleChildScrollView(
      child: PdlEmptyState(
        variant: PdlEmptyVariant.empty,
        icon: Icons.people_outline,
        title: 'placeholders.teamMembers.title'.tr(),
        message:
            '${'placeholders.teamMembers.message'.tr()} '
            '${'common.comingSoon'.tr()}',
      ),
    ),
  );
}
