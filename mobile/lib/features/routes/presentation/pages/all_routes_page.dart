import 'package:flutter/material.dart';

import '../../../../core/widgets/coming_soon_page.dart';

/// Cross-team route library — the root of the Routes tab.
///
/// Placeholder: the real screen (filters, sorting, list/map toggle) is
/// delivered by batch 3. The tab exists now so the five-tab bar is complete on
/// every screen.
class AllRoutesPage extends StatelessWidget {
  const AllRoutesPage({super.key});

  @override
  Widget build(BuildContext context) => const ComingSoonPage(
    titleKey: 'placeholders.allRoutes.title',
    messageKey: 'placeholders.allRoutes.message',
    icon: Icons.route_outlined,
  );
}
