import 'package:flutter/material.dart';

import '../../../../core/widgets/coming_soon_page.dart';

/// Public member list of a team.
///
/// Placeholder: the real screen (member cards, roles, profile links) is
/// delivered by batch 5. Not to be confused with the web-only admin screen
/// under `/admin/members`.
class TeamMembersPage extends StatelessWidget {
  final String teamSlug;

  const TeamMembersPage({super.key, required this.teamSlug});

  @override
  Widget build(BuildContext context) => const ComingSoonPage(
    titleKey: 'placeholders.teamMembers.title',
    messageKey: 'placeholders.teamMembers.message',
    icon: Icons.people_outline,
  );
}
