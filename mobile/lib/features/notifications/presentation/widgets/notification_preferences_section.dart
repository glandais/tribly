import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../data/notifications_repository.dart';
import '../../providers/notifications_provider.dart';

/// L'ordre des lignes : celui de l'enum, pas celui de la réponse.
const List<NotificationType> _typeOrder = <NotificationType>[
  NotificationType.ridePublished,
  NotificationType.rideCancelled,
  NotificationType.tripPublished,
  NotificationType.tripCancelled,
  NotificationType.postPublished,
  NotificationType.commentReply,
];

/// La matrice type × canal du profil.
///
/// **Elle ne rend rien** tant que le serveur ne déclare aucun canal
/// configurable — c'est le cas par défaut aujourd'hui : `IN_APP` n'est jamais
/// réglable (la boîte reçoit tout, et le `PUT` le refuse) et `PUSH` n'a pas
/// encore d'émetteur. Dessiner des interrupteurs sans effet est exactement ce
/// que le brief §5 interdit, et ce qui avait fait retirer la section en v2.
///
/// Un interrupteur écrit **une seule case** : envoyer la matrice entière
/// transformerait chaque défaut en dérogation explicite jamais choisie.
class NotificationPreferencesSection extends ConsumerStatefulWidget {
  const NotificationPreferencesSection({super.key});

  @override
  ConsumerState<NotificationPreferencesSection> createState() =>
      _NotificationPreferencesSectionState();
}

class _NotificationPreferencesSectionState
    extends ConsumerState<NotificationPreferencesSection> {
  String? _error;
  bool _busy = false;

  Future<void> _toggle({
    required NotificationType type,
    required NotificationChannel channel,
    required bool enabled,
  }) async {
    setState(() {
      _error = null;
      _busy = true;
    });
    try {
      await ref
          .read(notificationsRepositoryProvider)
          .setPreference(type: type, channel: channel, enabled: enabled);
      ref.invalidate(notificationPreferencesProvider);
      await ref.read(notificationPreferencesProvider.future);
    } catch (error, stackTrace) {
      if (mounted) setState(() => _error = getErrorMessage(error, stackTrace));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final AsyncValue<NotificationPreferencesDto> async = ref.watch(
      notificationPreferencesProvider,
    );

    // Chargement et échec sont muets l'un comme l'autre : la section est un
    // réglage secondaire du profil, elle n'a pas à y planter un bandeau rouge
    // ni à réserver de la place pour un tableau qui n'arrivera peut-être pas.
    final NotificationPreferencesDto? data = async.value;
    if (data == null || data.channels.isEmpty) return const SizedBox.shrink();

    final PdlTypography t = context.pdlText;
    // `$unknown` : un canal qu'une version plus ancienne de l'app ne connaît
    // pas. Il n'aurait ni nom traduit ni sens ici, on le laisse de côté.
    final List<NotificationChannel> channels = data.channels
        .where(
          (NotificationChannel channel) =>
              channel != NotificationChannel.$unknown,
        )
        .toList();
    if (channels.isEmpty) return const SizedBox.shrink();

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        0,
        PdlSpacing.section,
        PdlSpacing.section,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlSectionHeader(title: 'notifications.preferences.title'.tr()),
          PdlCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                Text(
                  'notifications.preferences.inAppAlwaysOn'.tr(),
                  style: t.xs,
                ),
                const SizedBox(height: PdlSpacing.chipGap),
                for (final NotificationType type in _typeOrder)
                  for (final NotificationChannel channel in channels)
                    _cell(data, type, channel),
                if (_error != null) ...<Widget>[
                  const SizedBox(height: PdlSpacing.chipGap),
                  PdlBanner(tone: PdlBannerTone.danger, message: _error!),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }

  /// Une ligne par case de la matrice.
  ///
  /// Avec un seul canal — le cas d'aujourd'hui — le nom du canal serait répété
  /// six fois pour rien : la ligne ne porte alors que le type, et le canal est
  /// dit une fois dans l'en-tête de section.
  Widget _cell(
    NotificationPreferencesDto data,
    NotificationType type,
    NotificationChannel channel,
  ) {
    final bool single = data.channels.length == 1;
    final String typeLabel = 'notifications.typeLabel.${type.toJson()}'.tr();
    final String channelLabel = 'notifications.channel.${channel.toJson()}'
        .tr();
    final NotificationPreferenceDto? cell = data.preferences
        .where(
          (NotificationPreferenceDto p) =>
              p.type == type.toJson() && p.channel == channel.toJson(),
        )
        .firstOrNull;
    if (cell == null) return const SizedBox.shrink();

    return PdlSettingRow(
      title: typeLabel,
      subtitle: single ? null : channelLabel,
      trailing: PdlSwitch(
        value: cell.enabled,
        semanticLabel: '$typeLabel — $channelLabel',
        onChanged: _busy
            ? null
            : (bool value) =>
                  _toggle(type: type, channel: channel, enabled: value),
      ),
    );
  }
}
