import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../routes/presentation/route_export.dart';
import '../../domain/ride_group_action.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_group_selection_provider.dart';
import '../../providers/ride_registration_controller.dart';
import 'ride_group_card.dart';
import '../../../../keys.dart';

/// Combien de groupes sont montrés avant le repli.
///
/// La maquette en montre quatre puis « Voir les N autres groupes » : dix
/// groupes de quatre lignes pleines font plus de trois écrans, et le lecteur
/// perd la carte et le profil qui les surplombent.
const int _kCollapsedGroupCount = 4;

/// La section « Groupes » de l'écran 12 — compteur, bandeau d'échec, cartes.
///
/// C'est ici que se joue l'inscription : le choix de groupe **est** la liste de
/// cartes, il n'y a plus ni `AlertDialog`, ni bouton d'action global en bas
/// d'écran qui ne dirait pas dans quel groupe il inscrit.
class RideGroupsSection extends ConsumerStatefulWidget {
  const RideGroupsSection({
    super.key,
    required this.rideKey,
    required this.ride,
    required this.isMember,
    required this.selectedGroupId,
    required this.onSelect,
    this.onShowParticipants,
    this.onViewRoute,
    this.onViewTeam,
  });

  final RideKey rideKey;
  final RideDto ride;

  /// `null` quand l'appartenance n'est pas encore connue : on laisse alors
  /// l'API trancher au premier appel plutôt que de masquer les boutons à un
  /// membre.
  final bool? isMember;

  final String? selectedGroupId;
  final ValueChanged<String> onSelect;

  final ValueChanged<RideGroupDto>? onShowParticipants;
  final ValueChanged<RideGroupDto>? onViewRoute;
  final VoidCallback? onViewTeam;

  @override
  ConsumerState<RideGroupsSection> createState() => _RideGroupsSectionState();
}

class _RideGroupsSectionState extends ConsumerState<RideGroupsSection> {
  bool _expanded = false;

  /// L'issue du dernier export ou envoi d'un groupe, en bandeau au-dessus des
  /// cartes — comme sur la fiche parcours, pas en snackbar.
  Object? _exportError;
  String? _exportSuccess;
  bool _exporting = false;

  @override
  Widget build(BuildContext context) {
    final RideDto ride = widget.ride;
    final List<RideGroupDto> groups = ride.groups;

    if (groups.isEmpty) {
      return PdlEmptyState(
        variant: PdlEmptyVariant.empty,
        icon: PdlIcons.people,
        title: 'rides.noGroupTitle'.tr(),
        message: 'rides.noGroupMessage'.tr(),
      );
    }

    final RideRegistrationState registration = ref.watch(
      rideRegistrationProvider(widget.rideKey),
    );
    final RideRegistrationController controller = ref.read(
      rideRegistrationProvider(widget.rideKey).notifier,
    );
    final String? currentUserId = ref.watch(
      authProvider.select((AuthState s) => s.user?.id),
    );
    final UnitSystem units = ref.watch(unitSystemProvider);
    // Les parcours des groupes sont déjà chargés en lot pour la carte : leurs
    // assets GPX/FIT viennent de là, sans appel de plus.
    final Map<String, RouteDetailDto> routes = ref.watch(
      rideRouteGeometriesProvider(
        widget.rideKey,
      ).select((RideRouteGeometriesState s) => s.geometries),
    );
    final List<GpsServiceConnectionDto> services = ref.watch(
      authProvider.select(
        (AuthState s) => s.user?.connectedServices ?? const [],
      ),
    );

    // Tant qu'on ne sait pas, on propose : un 403 `FORBIDDEN` répondra, et son
    // bandeau est plus juste qu'un bouton absent sans explication.
    final bool isMember = widget.isMember ?? true;

    final List<RideGroupDto> shown = _expanded
        ? groups
        : groups.take(_kCollapsedGroupCount).toList();
    final int hidden = groups.length - shown.length;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(
          title: 'rides.groups'.tr(),
          count: 'rides.groupCount'.plural(groups.length),
          padding: const EdgeInsets.only(bottom: 10),
        ),
        if (registration.failure != null) ...<Widget>[
          _failureBanner(registration.failure!, controller),
          const SizedBox(height: 10),
        ],
        if (_exportError != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.danger,
            message: getErrorMessage(_exportError!),
            onDismiss: () => setState(() => _exportError = null),
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: 10),
        ],
        if (_exportSuccess != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.info,
            message: _exportSuccess!,
            onDismiss: () => setState(() => _exportSuccess = null),
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: 10),
        ],
        for (final RideGroupDto group in shown) ...<Widget>[
          _groupCard(
            group: group,
            ride: ride,
            route: routes[group.routeSlug ?? ride.routeSlug],
            services: services,
            isMember: isMember,
            registration: registration,
            controller: controller,
            currentUserId: currentUserId,
            units: units,
          ),
          const SizedBox(height: PdlSpacing.feedGap),
        ],
        if (hidden > 0)
          PdlButton(
            label: 'rides.otherGroups'.plural(hidden),
            variant: PdlButtonVariant.outline,
            fullWidth: true,
            onPressed: () => setState(() => _expanded = true),
          ),
      ],
    );
  }

  Widget _groupCard({
    required RideGroupDto group,
    required RideDto ride,
    required RouteDetailDto? route,
    required List<GpsServiceConnectionDto> services,
    required bool isMember,
    required RideRegistrationState registration,
    required RideRegistrationController controller,
    required String? currentUserId,
    required UnitSystem units,
  }) {
    final String? routeSlug = group.routeSlug ?? ride.routeSlug;
    final AssetDto? gpx = route?.media.assets.gpx;
    final AssetDto? fit = route?.media.assets.fit;

    return RideGroupCard(
      key: keys.ride.group(group.id),
      group: group,
      rideDateTime: ride.dateTime,
      timezone: ride.timezone,
      action: rideGroupAction(ride: ride, group: group, isMember: isMember),
      trackColor: multiTrackColor(group.sortOrder),
      units: units,
      selected: group.id == widget.selectedGroupId,
      pending: registration.pendingGroupId == group.id,
      currentUserId: currentUserId,
      onTap: () => widget.onSelect(group.id),
      onJoin: () => controller.join(group.id),
      onLeave: () => controller.leave(group.id),
      onShowParticipants: widget.onShowParticipants == null
          ? null
          : () => widget.onShowParticipants!(group),
      onViewRoute: widget.onViewRoute == null || routeSlug == null
          ? null
          : () => widget.onViewRoute!(group),
      onExportGpx: gpx == null ? null : () => _download(gpx),
      onExportFit: fit == null ? null : () => _download(fit),
      onSendToDevice: routeSlug == null || services.isEmpty
          ? null
          : () => _sendToDevice(ride.team.slug, routeSlug, services),
    );
  }

  Future<void> _download(AssetDto asset) async {
    await _runExport(() async {
      await downloadAndShareRouteAsset(
        ref,
        asset,
        origin: shareOriginOf(context),
      );
      return null;
    });
  }

  Future<void> _sendToDevice(
    String teamSlug,
    String routeSlug,
    List<GpsServiceConnectionDto> services,
  ) async {
    final GpsServiceConnectionDto? picked = await pickGpsService(
      context,
      services,
    );
    if (picked == null) return;
    await _runExport(() async {
      await uploadRouteToService(
        ref,
        picked,
        teamSlug: teamSlug,
        routeSlug: routeSlug,
      );
      return 'routes.uploadSuccess'.tr();
    });
  }

  /// Un export à la fois : un second tap pendant un téléchargement est ignoré
  /// plutôt que d'empiler deux feuilles de partage.
  Future<void> _runExport(Future<String?> Function() run) async {
    if (_exporting) return;
    setState(() {
      _exporting = true;
      _exportError = null;
      _exportSuccess = null;
    });
    try {
      final String? success = await run();
      if (mounted) setState(() => _exportSuccess = success);
    } catch (error) {
      if (mounted) setState(() => _exportError = error);
    } finally {
      if (mounted) setState(() => _exporting = false);
    }
  }

  /// Le bandeau d'échec — **persistant**, nommant le groupe, et porteur de la
  /// seule action qui résout le cas d'exclusivité.
  Widget _failureBanner(
    RegistrationFailure failure,
    RideRegistrationController controller,
  ) {
    final String title = switch (failure.reason) {
      RegistrationFailureReason.alreadyRegistered =>
        'rides.failure.exclusive'.tr(
          namedArgs: <String, String>{'group': failure.groupName},
        ),
      RegistrationFailureReason.groupFull => 'rides.failure.full'.tr(
        namedArgs: <String, String>{'group': failure.groupName},
      ),
      RegistrationFailureReason.notMember => 'rides.failure.notMember'.tr(),
      RegistrationFailureReason.generic => 'rides.failure.generic'.tr(
        namedArgs: <String, String>{'group': failure.groupName},
      ),
    };

    Widget? action;
    if (failure.canSwitch) {
      action = PdlButton(
        key: keys.ride.switchGroupButton,
        label: 'rides.failure.leaveAndJoin'.tr(
          namedArgs: <String, String>{'group': failure.blockingGroupName ?? ''},
        ),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        onPressed: () {
          controller.switchGroup(
            leaveGroupId: failure.blockingGroupId!,
            joinGroupId: failure.groupId,
          );
        },
      );
    } else if (failure.reason == RegistrationFailureReason.notMember &&
        widget.onViewTeam != null) {
      action = PdlButton(
        label: 'teams.viewTeam'.tr(),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        onPressed: widget.onViewTeam,
      );
    } else if (failure.reason == RegistrationFailureReason.generic) {
      action = PdlButton(
        label: 'common.retry'.tr(),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        onPressed: () => controller.join(failure.groupId),
      );
    }

    return PdlBanner(
      key: keys.ride.registrationFailure,
      tone: PdlBannerTone.danger,
      title: title,
      message: failure.message,
      action: action,
      onDismiss: controller.dismissFailure,
      dismissSemanticLabel: 'common.close'.tr(),
    );
  }
}
