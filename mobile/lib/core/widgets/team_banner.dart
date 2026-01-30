import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../api/generated/export.dart';
import '../../config/paths.dart';
import '../utils/safe_string.dart';
import 'authenticated_image.dart';

/// Displays a team logo (initials fallback) and name.
/// Taps navigate to the team detail page.
class TeamBanner extends StatelessWidget {
  final TeamPublicationDto team;

  const TeamBanner({super.key, required this.team});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(8),
      onTap: () => context.push(Paths.team(team.slug)),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            AuthenticatedCircleAvatar(
              imageUrl: null,
              fallbackText: team.name.safeFirstUpper(),
              radius: 14,
              fontSize: 10,
            ),
            const SizedBox(width: 8),
            Flexible(
              child: Text(
                team.name,
                style: Theme.of(context).textTheme.titleSmall,
                overflow: TextOverflow.ellipsis,
              ),
            ),
            const SizedBox(width: 4),
            Icon(
              Icons.chevron_right,
              size: 16,
              color: Theme.of(context).colorScheme.outline,
            ),
          ],
        ),
      ),
    );
  }
}
