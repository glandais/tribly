import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../ads/presentation/pages/ads_page.dart';
import '../../../calendar/presentation/pages/calendar_page.dart';
import '../../../routes/presentation/pages/routes_page.dart';
import '../../providers/team_providers.dart';
import '../widgets/team_sections.dart';
import '../widgets/team_sections_bar.dart';
import 'team_about_page.dart';
import 'team_feed_page.dart';
import 'team_members_page.dart';

/// A team, whatever section of it is being looked at.
///
/// **One page owns the team's loading and error state.** Every team section is
/// this page with another [section]; the sections themselves receive a team
/// that has already arrived. That is the whole point of the task: the router
/// used to wrap each section in a widget that watched the team *and* rendered
/// an error of its own, while the section rendered a second one under it —
/// hence two stacked errors on a cold deep link, or a blank screen when the
/// wrapper answered with `SizedBox.shrink()`.
///
/// The row of sections is [content](TeamSectionsBar), pinned under the team
/// header, and the five global tabs stay visible: entering a team no longer
/// replaces the app's navigation.
class TeamHomePage extends ConsumerWidget {
  const TeamHomePage({
    super.key,
    required this.teamSlug,
    required this.section,
  });

  final String teamSlug;
  final TeamSectionKind section;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<TeamDetailDto> teamAsync = ref.watch(
      teamDetailProvider(teamSlug),
    );

    return teamAsync.when(
      data: (TeamDetailDto team) =>
          _TeamSectionScaffold(team: team, section: section),
      loading: () => _TeamChrome(
        title: null,
        body: const Padding(
          padding: EdgeInsets.all(PdlSpacing.section),
          child: PdlSkeletonCardList(count: 3),
        ),
      ),
      error: (Object error, _) => _TeamChrome(
        title: null,
        body: Center(
          child: SingleChildScrollView(
            child: PdlEmptyState(
              variant: PdlEmptyVariant.error,
              title: 'teams.loadError.title'.tr(),
              message: getErrorMessage(error),
              actions: <Widget>[
                PdlButton(
                  label: 'common.retry'.tr(),
                  onPressed: () => ref.invalidate(teamDetailProvider(teamSlug)),
                ),
                PdlButton(
                  label: 'teams.title'.tr(),
                  variant: PdlButtonVariant.outline,
                  onPressed: () => context.go(Paths.teams()),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

/// The team header and the frame every section hangs from.
///
/// The [Scaffold] is also what makes an iOS status-bar tap scroll a section
/// back to the top: it animates the [PrimaryScrollController] of **its own**
/// route, and `MainShell`'s Scaffold lives in the root navigator's route while
/// a team lives in the Teams branch — two different controllers.
class _TeamChrome extends StatelessWidget {
  const _TeamChrome({required this.title, required this.body});

  final String? title;
  final Widget body;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: context.pdl.bg,
      appBar: PdlAppBar(
        title: title,
        onBack: () => context.go(Paths.teams()),
        backSemanticLabel: 'teams.title'.tr(),
      ),
      body: body,
    );
  }
}

/// Dispatches to the section's body, team in hand.
class _TeamSectionScaffold extends StatelessWidget {
  const _TeamSectionScaffold({required this.team, required this.section});

  final TeamDetailDto team;
  final TeamSectionKind section;

  @override
  Widget build(BuildContext context) {
    final List<TeamSection> sections = buildTeamSections(team);

    return _TeamChrome(
      title: team.name,
      body: switch (section) {
        // Sections whose body is a list of slivers take the row as a pinned
        // sliver of that same list.
        TeamSectionKind.feed => TeamFeedPage(
          teamSlug: team.slug,
          team: team,
          toolbar: TeamSectionsToolbar(sections: sections, current: section),
        ),
        TeamSectionKind.about => TeamAboutPage(
          teamSlug: team.slug,
          team: team,
          toolbar: TeamSectionsToolbar(sections: sections, current: section),
        ),
        // Sections that are pages of their own take the fixed form of the same
        // row, above them.
        TeamSectionKind.calendar => _WithSectionsBar(
          sections: sections,
          current: section,
          child: CalendarPage(teamSlug: team.slug, embedded: true),
        ),
        TeamSectionKind.routes => _WithSectionsBar(
          sections: sections,
          current: section,
          child: RoutesPage(teamSlug: team.slug, embedded: true),
        ),
        TeamSectionKind.ads => _WithSectionsBar(
          sections: sections,
          current: section,
          child: AdsPage(teamSlug: team.slug, team: team),
        ),
        TeamSectionKind.members => _WithSectionsBar(
          sections: sections,
          current: section,
          child: TeamMembersPage(teamSlug: team.slug),
        ),
      },
    );
  }
}

class _WithSectionsBar extends StatelessWidget {
  const _WithSectionsBar({
    required this.sections,
    required this.current,
    required this.child,
  });

  final List<TeamSection> sections;
  final TeamSectionKind current;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        TeamSectionsBar(sections: sections, current: current),
        Expanded(child: child),
      ],
    );
  }
}
