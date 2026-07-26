import 'package:flutter/material.dart';

import '../../../../core/widgets/coming_soon_page.dart';

/// Map view of the cross-team route library.
///
/// Placeholder: delivered by batch 3, together with `PdlMap`.
class AllRoutesMapPage extends StatelessWidget {
  const AllRoutesMapPage({super.key});

  @override
  Widget build(BuildContext context) => const ComingSoonPage(
    titleKey: 'placeholders.allRoutesMap.title',
    messageKey: 'placeholders.allRoutesMap.message',
    icon: Icons.map_outlined,
  );
}
