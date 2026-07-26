import 'package:flutter/material.dart';

import '../../../../core/widgets/coming_soon_page.dart';

/// A free-form page published by a team (`/teams/{slug}/pages/{pageSlug}`).
///
/// Placeholder: the markdown rendering is delivered by batch 5. `team_about_page`
/// already lists these pages; this route is where its links will land.
class TeamCustomPage extends StatelessWidget {
  final String teamSlug;
  final String pageSlug;

  const TeamCustomPage({
    super.key,
    required this.teamSlug,
    required this.pageSlug,
  });

  @override
  Widget build(BuildContext context) => const ComingSoonPage(
    titleKey: 'placeholders.teamPage.title',
    messageKey: 'placeholders.teamPage.message',
    icon: Icons.article_outlined,
  );
}
