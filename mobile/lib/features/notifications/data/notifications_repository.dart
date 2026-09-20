import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';

final notificationsRepositoryProvider = Provider<NotificationsRepository>((
  ref,
) {
  return NotificationsRepository(ref.watch(notificationsClientProvider));
});

/// Tout ce que la boîte de réception demande au serveur.
///
/// Les six endpoints de `docs/plans/2026-09-18-notifications.md` §9, sans
/// autre logique : c'est la couche qui traduit une page de l'API en
/// [PageResult], la forme que [PagedListNotifier] attend.
class NotificationsRepository {
  NotificationsRepository(this._client);

  final NotificationsClient _client;

  /// Une page de la boîte, la plus récente d'abord.
  ///
  /// Le `unreadCount` de la réponse n'est pas retenu ici : il vaut pour la
  /// boîte entière, pas pour la page, et c'est `unread-count` qui le sert à la
  /// pastille — le garder au fil des pages le ferait diverger dès qu'une
  /// notification est lue ailleurs.
  Future<PageResult<NotificationDto>> fetchPage({
    int page = 0,
    int size = 20,
    bool unreadOnly = false,
  }) async {
    final NotificationListResponse response = await _client.listMyNotifications(
      page: page,
      size: size,
      unreadOnly: unreadOnly,
    );
    return PageResult<NotificationDto>(
      items: response.items,
      total: response.total,
    );
  }

  Future<int> unreadCount() async {
    final UnreadCountDto dto = await _client.countMyUnreadNotifications();
    return dto.count;
  }

  Future<void> markRead(String id) =>
      _client.markNotificationRead(notificationId: id);

  Future<void> markAllRead() => _client.markAllNotificationsRead();

  Future<NotificationPreferencesDto> preferences() =>
      _client.getMyNotificationPreferences();

  /// Une seule case à la fois : l'endpoint est un patch, et lui envoyer la
  /// matrice entière transformerait chaque défaut en dérogation explicite que
  /// l'utilisateur n'a jamais choisie.
  Future<NotificationPreferencesDto> setPreference({
    required NotificationType type,
    required NotificationChannel channel,
    required bool enabled,
  }) {
    return _client.updateMyNotificationPreferences(
      body: NotificationPreferencesRequest(
        preferences: <NotificationPreferenceUpdate>[
          NotificationPreferenceUpdate(
            type: type.toJson(),
            channel: channel.toJson(),
            enabled: enabled,
          ),
        ],
      ),
    );
  }
}
