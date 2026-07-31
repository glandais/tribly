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
import '../../../routes/domain/route_filters.dart';
import '../../../routes/presentation/pages/routes_page.dart';
import '../../../routes/providers/route_list_provider.dart';
import '../../providers/team_membership_controller.dart';
import '../../providers/team_providers.dart';
import '../widgets/team_header.dart';
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
    // Le contrôleur d'adhésion **fait autorité** sur le détail rendu : c'est
    // lui qui porte la bascule optimiste. Il adopte `teamDetailProvider` et
    // le rend inchangé le reste du temps.
    final AsyncValue<TeamDetailDto> teamAsync = ref.watch(
      teamMembershipControllerProvider(
        teamSlug,
      ).select((TeamMembershipState s) => s.team),
    );

    return teamAsync.when(
      data: (TeamDetailDto team) =>
          _TeamSectionScaffold(team: team, section: section),
      loading: () => const _TeamChrome.bare(
        body: Padding(
          padding: EdgeInsets.all(PdlSpacing.section),
          child: PdlSkeletonCardList(count: 3),
        ),
      ),
      error: (Object error, _) => _TeamChrome.bare(
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

/// The frame every section hangs from.
///
/// The [Scaffold] is also what makes an iOS status-bar tap scroll a section
/// back to the top: it animates the [PrimaryScrollController] of **its own**
/// route, and `MainShell`'s Scaffold lives in the root navigator's route while
/// a team lives in the Teams branch — two different controllers.
class _TeamChrome extends StatelessWidget {
  /// L'équipe est là : son en-tête porte déjà la flèche de retour, l'action
  /// d'adhésion et le partage. [header] est `null` quand cet en-tête vit dans
  /// les slivers du corps (fil, à propos) — **pas** de barre de plus alors,
  /// sans quoi l'écran affiche deux retours l'un sous l'autre.
  const _TeamChrome({required this.header, required this.body})
    : ownBar = false;

  /// Chargement et erreur : rien à rétracter, pas d'identité à inventer, mais
  /// il faut de quoi sortir. C'est le seul cas qui pose une barre à lui.
  const _TeamChrome.bare({required this.body}) : header = null, ownBar = true;

  final Widget? header;

  final Widget body;

  final bool ownBar;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: context.pdl.bg,
      appBar: ownBar
          ? PdlAppBar(
              onBack: () => context.go(Paths.teams()),
              backSemanticLabel: 'teams.title'.tr(),
            )
          : null,
      body: header == null
          ? body
          : Column(
              children: <Widget>[
                SafeArea(bottom: false, child: header!),
                Expanded(child: body),
              ],
            ),
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

    // Les deux sections qui sont **une liste de slivers** portent l'en-tête
    // interpolé : il se rétracte de 112 à 56 px sous le doigt. Les quatre
    // autres possèdent leur propre défileur et reçoivent l'en-tête déjà
    // rétracté — voir [TeamHeaderSliver].
    return switch (section) {
      TeamSectionKind.feed => _TeamChrome(
        header: null,
        body: TeamFeedPage(
          teamSlug: team.slug,
          team: team,
          leadingSlivers: <Widget>[
            TeamHeaderSliver(team: team),
            SliverToBoxAdapter(
              child: _TeamStats(team: team, section: section),
            ),
            SliverToBoxAdapter(child: _MembershipBanner(team: team)),
          ],
          toolbar: TeamSectionsToolbar(sections: sections, current: section),
        ),
      ),
      TeamSectionKind.about => _TeamChrome(
        header: null,
        body: TeamAboutPage(
          teamSlug: team.slug,
          team: team,
          leadingSlivers: <Widget>[
            TeamHeaderSliver(team: team),
            SliverToBoxAdapter(
              child: _TeamStats(team: team, section: section),
            ),
            SliverToBoxAdapter(child: _MembershipBanner(team: team)),
          ],
          toolbar: TeamSectionsToolbar(sections: sections, current: section),
        ),
      ),
      TeamSectionKind.calendar => _TeamChrome(
        header: TeamHeaderBar(team: team),
        body: _WithSectionsBar(
          sections: sections,
          current: section,
          child: CalendarPage(teamSlug: team.slug, embedded: true),
        ),
      ),
      TeamSectionKind.routes => _TeamChrome(
        header: TeamHeaderBar(team: team),
        body: _WithSectionsBar(
          sections: sections,
          current: section,
          child: RoutesPage(teamSlug: team.slug, embedded: true),
        ),
      ),
      TeamSectionKind.ads => _TeamChrome(
        header: TeamHeaderBar(team: team),
        body: _WithSectionsBar(
          sections: sections,
          current: section,
          child: AdsPage(teamSlug: team.slug, team: team),
        ),
      ),
      TeamSectionKind.members => _TeamChrome(
        header: TeamHeaderBar(team: team),
        body: _WithSectionsBar(
          sections: sections,
          current: section,
          child: TeamMembersPage(teamSlug: team.slug),
        ),
      ),
    };
  }
}

/// Les trois cellules chiffrées — et c'est ici que le silo se casse.
///
/// Elles ne mènent pas à des écrans dupliqués par équipe mais à des **surfaces
/// racine pré-filtrées** : le trombinoscope de l'équipe, le calendrier de
/// portée équipe, et l'onglet Parcours global dont la portée est réglée sur
/// cette équipe. Une application qui recopie ses écrans par équipe finit avec
/// deux parcothèques qui divergent.
///
/// Sur « À propos », deux cellules seulement : membres et année de création.
/// Il n'y a pas de `foundedYear` au contrat — c'est `createdAt`, et le dire
/// autrement serait inventer une histoire à l'équipe.
class _TeamStats extends ConsumerWidget {
  const _TeamStats({required this.team, required this.section});

  final TeamDetailDto team;
  final TeamSectionKind section;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final bool about = section == TeamSectionKind.about;
    final DateTime? created = DateTime.tryParse(team.createdAt);

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        PdlSpacing.chipGap,
        PdlSpacing.section,
        0,
      ),
      child: PdlStatCellRow(
        cells: <PdlStatCell>[
          PdlStatCell(
            value: '${team.memberCount}',
            label: 'teams.membersLabel'.tr(),
            semanticLabel: 'teams.membersList.count'.plural(team.memberCount),
            onTap: () => context.go(Paths.teamMembers(team.slug)),
          ),
          if (about)
            PdlStatCell(
              value: created == null ? '—' : '${created.year}',
              label: 'teams.aboutPage.createdYear'.tr(),
            )
          else ...<PdlStatCell>[
            PdlStatCell(
              value: '${team.upcomingRideCount}',
              label: 'teams.stats.upcomingRides'.tr(),
              onTap: () => context.go(Paths.teamCalendar(team.slug)),
            ),
            PdlStatCell(
              value: '${team.routeCount}',
              label: 'teams.tabs.routes'.tr(),
              // **L'onglet Parcours global**, portée pré-réglée sur l'équipe :
              // c'est la même parcothèque, filtrée, et non une seconde.
              onTap: () {
                ref.read(routeFiltersProvider(null).notifier).state =
                    const RouteFilters().copyWith(teamSlug: team.slug);
                context.go(Paths.allRoutes());
              },
            ),
          ],
        ],
      ),
    );
  }
}

/// Le bandeau d'échec d'adhésion, nommant sa cause.
class _MembershipBanner extends ConsumerWidget {
  const _MembershipBanner({required this.team});

  final TeamDetailDto team;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final String? failure = ref.watch(
      teamMembershipControllerProvider(
        team.slug,
      ).select((TeamMembershipState s) => s.failure),
    );
    if (failure == null) return const SizedBox.shrink();

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        PdlSpacing.chipGap,
        PdlSpacing.section,
        0,
      ),
      child: PdlBanner(
        tone: PdlBannerTone.danger,
        title: 'teams.membership.failed'.tr(),
        message: failure,
        onDismiss: () => ref
            .read(teamMembershipControllerProvider(team.slug).notifier)
            .dismissFailure(),
        dismissSemanticLabel: 'common.close'.tr(),
      ),
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
