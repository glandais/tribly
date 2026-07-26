import 'package:flutter/material.dart';

import '../../../../core/widgets/coming_soon_page.dart';

/// Public team directory — join a team you are not a member of.
///
/// Placeholder: the real screen (search, public team cards, join request) is
/// delivered by batch 5 / S34-3. The route exists now so the contract, the
/// deep links and the Teams tab are complete.
class TeamsDiscoverPage extends StatelessWidget {
  const TeamsDiscoverPage({super.key});

  @override
  Widget build(BuildContext context) => const ComingSoonPage(
    titleKey: 'placeholders.teamsDiscover.title',
    messageKey: 'placeholders.teamsDiscover.message',
    icon: Icons.travel_explore_outlined,
  );
}
