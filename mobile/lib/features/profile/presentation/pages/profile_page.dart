import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:package_info_plus/package_info_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../providers/participations_provider.dart';
import '../widgets/connected_services_section.dart';
import '../widgets/data_and_account_section.dart';
import '../widgets/passkeys_section.dart';
import '../widgets/preferences_section.dart';
import '../widgets/profile_identity_section.dart';

final _serverVersionProvider = FutureProvider<VersionDto>((ref) async {
  return ref.watch(serverVersionClientProvider).getVersion();
});

final _packageInfoProvider = FutureProvider<PackageInfo>(
  (ref) => PackageInfo.fromPlatform(),
);

/// Le profil : une colonne unique bornée à 600 px, et dix sections au même
/// motif — un en-tête, puis une carte.
///
/// **Deux sections de la maquette ne sont pas livrées, et c'est délibéré.**
/// Les notifications (quatre interrupteurs) n'ont ni endpoint de préférences ni
/// push : les dessiner produirait quatre réglages sans effet, ce que le brief
/// §5 interdit explicitement. La cloche de la barre supérieure disparaît pour
/// la même raison (§1.0.4).
class ProfilePage extends ConsumerWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;

    return Scaffold(
      backgroundColor: c.bg,
      appBar: PdlAppBar(
        title: 'profile.title'.tr(),
        // Le profil est un onglet, donc souvent sans pile derrière lui : on
        // arrive de l'avatar de l'accueil par un changement de branche, qui ne
        // laisse rien à dépiler. La flèche retombe alors sur l'accueil plutôt
        // que de disparaître — même arbitrage que l'accueil d'équipe.
        onBack: () =>
            context.canPop() ? context.pop() : context.go(Paths.home()),
        backSemanticLabel: 'common.back'.tr(),
      ),
      body: PdlScreenScaffold(
        constrainWidth: true,
        slivers: <Widget>[
          const SliverToBoxAdapter(child: ProfileIdentitySection()),
          _section(
            title: 'profile.participations.title'.tr(),
            child: const _ParticipationsCard(),
          ),
          _section(
            title: 'profile.preferences'.tr(),
            child: const PreferencesSection(),
          ),
          const SliverToBoxAdapter(child: PasskeysSection()),
          _section(
            title: 'profile.gps.title'.tr(),
            child: const GpsServicesCard(),
          ),
          _section(
            title: 'profile.linked.title'.tr(),
            child: const LinkedAccountsCard(),
          ),
          _section(
            title: 'profile.data.title'.tr(),
            child: const DataExportCard(),
          ),
          _section(title: 'profile.about'.tr(), child: const _AboutCard()),
          const SliverToBoxAdapter(child: AccountSection()),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }

  Widget _section({required String title, required Widget child}) {
    return SliverToBoxAdapter(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(
          PdlSpacing.section,
          0,
          PdlSpacing.section,
          PdlSpacing.section,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            PdlSectionHeader(title: title),
            child,
          ],
        ),
      ),
    );
  }
}

/// « Mes sorties à venir » et « Historique », comptés par le `total` de
/// l'endpoint — deux appels d'une ligne, aucun endpoint de comptage.
class _ParticipationsCard extends ConsumerWidget {
  const _ParticipationsCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;

    Widget row({
      required bool upcoming,
      required IconData icon,
      required String title,
    }) {
      final int? count = ref.watch(participationCountProvider(upcoming)).value;
      return PdlSettingRow(
        icon: icon,
        title: title,
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            if (count != null)
              PdlBadge(
                label: '$count',
                size: PdlBadgeSize.lg,
                tone: upcoming
                    ? PdlDerivedTones.registered(c)
                    : PdlTone.pair(c.softGray, c.neutral),
              ),
            const SizedBox(width: 4),
            Icon(PdlIcons.chevronRight, size: 20, color: c.textPlaceholder),
          ],
        ),
        showDivider: upcoming,
        onTap: () => context.push(Paths.myParticipations(), extra: upcoming),
      );
    }

    return PdlCard(
      padding: PdlCardPadding.none,
      child: Column(
        children: <Widget>[
          row(
            upcoming: true,
            icon: PdlIcons.ride,
            title: 'profile.participations.upcoming'.tr(),
          ),
          row(
            upcoming: false,
            icon: PdlIcons.time,
            title: 'profile.participations.past'.tr(),
          ),
        ],
      ),
    );
  }
}

/// Versions et pages légales.
class _AboutCard extends ConsumerWidget {
  const _AboutCard();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlTypography t = context.pdlText;

    // Une version absente n'est pas une erreur d'écran : on n'affiche rien
    // plutôt qu'un « Chargement impossible » pour un numéro de build.
    final String appVersion = ref
        .watch(_packageInfoProvider)
        .maybeWhen(
          data: (PackageInfo info) => info.buildNumber.isEmpty
              ? info.version
              : '${info.version} (${info.buildNumber})',
          orElse: () => '',
        );
    final String serverVersion = ref
        .watch(_serverVersionProvider)
        .maybeWhen(
          data: (VersionDto version) => version.apiVersion,
          orElse: () => '',
        );

    return PdlCard(
      padding: PdlCardPadding.none,
      child: Column(
        children: <Widget>[
          PdlSettingRow(
            title: 'profile.version'.tr(),
            trailing: Text(appVersion, style: t.mono),
            showDivider: true,
          ),
          PdlSettingRow(
            title: 'profile.serverVersion'.tr(),
            trailing: Text(serverVersion, style: t.mono),
            showDivider: true,
          ),
          PdlSettingRow(
            title: 'profile.privacy'.tr(),
            onTap: () => context.push(Paths.privacy()),
            showDivider: true,
          ),
          PdlSettingRow(
            title: 'profile.terms'.tr(),
            onTap: () => context.push(Paths.terms()),
          ),
        ],
      ),
    );
  }
}
