import 'dart:async';
import 'dart:developer';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../auth/domain/auth_state.dart';
import '../../auth/providers/auth_provider.dart';
import '../data/notifications_repository.dart';
import '../data/push_device_repository.dart';
import '../domain/push_message.dart';
import '../services/push_gateway.dart';
import 'notifications_provider.dart';

/// La passerelle vers la plateforme. Surchargée en test, où Firebase n'existe
/// pas et où aucune de ses méthodes ne doit être appelée.
final pushGatewayProvider = Provider<PushGateway>(
  (Ref ref) => FirebasePushGateway(),
);

/// La route qu'un push demande d'ouvrir, déposée ici par [PushController].
///
/// Elle ne navigue pas elle-même : `main.dart` la reprend et la fait passer
/// par le **même** chemin qu'un lien web (attendre l'initialisation de la
/// session, attendre que le routeur ait sa première route, reconstruire les
/// ancêtres). Un `go` direct depuis un message reçu avant que l'app ne soit
/// navigable ouvrirait une page sans retour possible.
final pendingPushRouteProvider = StateProvider<String?>((Ref ref) => null);

/// Le jeton actuellement enregistré auprès du serveur, ou `null`.
///
/// Il existe comme provider *à part* pour que la déconnexion puisse le lire
/// sans instancier [PushController] : celui-ci écoute `authProvider`, et le
/// créer depuis une méthode de `AuthNotifier` ferait tourner l'écoute en rond.
/// La désinscription doit partir **avant** l'effacement de la session — c'est
/// un appel authentifié.
final registeredPushTokenProvider = StateProvider<String?>((Ref ref) => null);

/// L'état du push sur cet appareil : ce que l'écran a besoin de savoir pour
/// décider s'il propose quelque chose.
final pushAuthorizationProvider =
    StateNotifierProvider<PushController, PushAuthorization>((Ref ref) {
      final PushController controller = PushController(ref);
      ref.listen<bool>(
        authProvider.select((AuthState s) => s.isAuthenticated),
        (bool? previous, bool next) => controller.onAuthChanged(next),
        fireImmediately: true,
      );
      return controller;
    });

/// Le cycle de vie du push côté appareil.
///
/// Trois responsabilités, et pas une de plus : tenir l'autorisation à jour,
/// tenir le jeton enregistré, et traduire un message reçu en effet dans l'app
/// (pastille rafraîchie, notification marquée lue, route déposée).
class PushController extends StateNotifier<PushAuthorization> {
  PushController(this._ref) : super(PushAuthorization.unsupported);

  final Ref _ref;

  final List<StreamSubscription<Object>> _subscriptions =
      <StreamSubscription<Object>>[];
  bool _wired = false;

  PushGateway get _gateway => _ref.read(pushGatewayProvider);

  String? get _registeredToken => _ref.read(registeredPushTokenProvider);

  set _registeredToken(String? token) =>
      _ref.read(registeredPushTokenProvider.notifier).state = token;

  Future<void> onAuthChanged(bool isAuthenticated) async {
    if (!isAuthenticated) {
      // Le jeton n'est pas supprimé de l'appareil : il identifie
      // l'installation, pas la personne, et le prochain compte connecté ici le
      // réutilisera. C'est le serveur qui l'a déplacé ou oublié.
      _registeredToken = null;
      return;
    }
    await _start();
  }

  Future<void> _start() async {
    await _gateway.ensureInitialized();
    final PushAuthorization authorization = await _gateway.authorization();
    if (!mounted) return;
    state = authorization;
    if (authorization == PushAuthorization.unsupported) return;

    _wire();
    if (authorization == PushAuthorization.granted) await _registerToken();

    // Une app tuée puis réveillée par un tap : le message n'arrive pas par le
    // flux, il attend ici.
    final PushMessage? initial = await _gateway.initialMessage();
    if (initial != null) _handleTap(initial);
  }

  /// Demande l'autorisation au système, et enregistre le jeton si elle est
  /// accordée. Appelée depuis l'écran des notifications, jamais au lancement :
  /// la boîte de dialogue ne s'affiche qu'une fois dans la vie de
  /// l'installation, et un membre qui n'a pas encore vu ce que l'app notifie
  /// n'a aucune raison de dire oui.
  Future<PushAuthorization> requestAuthorization() async {
    final PushAuthorization authorization = await _gateway
        .requestAuthorization();
    if (!mounted) return authorization;
    state = authorization;
    if (authorization == PushAuthorization.granted) {
      _wire();
      await _registerToken();
    }
    return authorization;
  }

  void _wire() {
    if (_wired) return;
    _wired = true;
    _subscriptions.add(_gateway.tokenRefreshes().listen(_onTokenRefreshed));
    _subscriptions.add(_gateway.foregroundMessages().listen(_handleForeground));
    _subscriptions.add(_gateway.taps().listen(_handleTap));
  }

  Future<void> _registerToken() async {
    final String? token = await _gateway.token();
    if (token == null || token == _registeredToken) return;
    try {
      await _ref.read(pushDeviceRepositoryProvider).register(token);
      _registeredToken = token;
    } catch (error) {
      // Sans effet visible : le membre n'a rien demandé à cet instant, et la
      // prochaine ouverture de session réessaiera. Une erreur affichée ici
      // parlerait d'un jeton dont personne n'a connaissance.
      log('Push device registration failed: $error', name: 'push');
    }
  }

  void _onTokenRefreshed(String token) {
    if (token == _registeredToken) return;
    _registeredToken = null;
    unawaited(_registerToken());
  }

  /// Un message reçu **app ouverte** : la bannière (Android ne l'affiche pas
  /// tout seul) et la pastille, qui doit bouger même si le membre ne touche à
  /// rien.
  void _handleForeground(PushMessage message) {
    unawaited(_gateway.showForeground(message));
    unawaited(_ref.read(unreadNotificationCountProvider.notifier).refresh());
    _ref.invalidate(notificationsProvider);
  }

  /// Un message **tapé** : on marque lu puis on ouvre. L'ordre compte — la
  /// page s'ouvre par-dessus, et une pastille qui ne bouge qu'au retour donne
  /// l'impression que le geste n'a rien fait.
  void _handleTap(PushMessage message) {
    final String? id = message.notificationId;
    if (id != null) unawaited(_markRead(id));

    final String? path = message.path;
    if (path != null) _ref.read(pendingPushRouteProvider.notifier).state = path;
  }

  Future<void> _markRead(String id) async {
    try {
      await _ref.read(notificationsRepositoryProvider).markRead(id);
      await _ref.read(unreadNotificationCountProvider.notifier).refresh();
      _ref.invalidate(notificationsProvider);
    } catch (error) {
      log('Could not mark notification $id read: $error', name: 'push');
    }
  }

  @override
  void dispose() {
    for (final StreamSubscription<Object> subscription in _subscriptions) {
      unawaited(subscription.cancel());
    }
    super.dispose();
  }
}
