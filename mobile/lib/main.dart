import 'dart:async';
import 'dart:developer';

import 'package:app_links/app_links.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app.dart';
import 'config/router.dart';
import 'features/auth/providers/auth_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await EasyLocalization.ensureInitialized();

  // Handle deep links
  final appLinks = AppLinks();

  // Handle initial deep link (app opened via link)
  final initialLink = await appLinks.getInitialLink();
  if (initialLink != null) {
    log('Initial deep link: $initialLink', name: 'main');
  }

  String? initialPath;
  if (initialLink != null) {
    final path = initialLink.path.isEmpty ? '/' : initialLink.path;
    final query = initialLink.query.isNotEmpty ? '?${initialLink.query}' : '';
    initialPath = path + query;
  }

  runApp(
    EasyLocalization(
      supportedLocales: const [Locale('en'), Locale('fr')],
      path: 'assets/l10n',
      fallbackLocale: const Locale('fr'),
      child: ProviderScope(
        overrides: [
          if (initialPath != null)
            initialDeepLinkProvider.overrideWithValue(initialPath),
        ],
        child: _DeepLinkHandler(
          appLinks: appLinks,
          child: const PedalonsApp(),
        ),
      ),
    ),
  );
}

/// Widget that handles deep links (initial + runtime), expanding detail-page
/// targets into a full ancestor stack so back navigates through the logical
/// hierarchy instead of closing the app.
class _DeepLinkHandler extends ConsumerStatefulWidget {
  final AppLinks appLinks;
  final Widget child;

  const _DeepLinkHandler({
    required this.appLinks,
    required this.child,
  });

  @override
  ConsumerState<_DeepLinkHandler> createState() => _DeepLinkHandlerState();
}

/// Give up waiting for the router to mount after this many rendered frames.
const int _maxRouterMountFrames = 120;

class _DeepLinkHandlerState extends ConsumerState<_DeepLinkHandler> {
  StreamSubscription<Uri>? _linkSubscription;
  ProviderSubscription<bool>? _authSubscription;
  Completer<void>? _authInitialized;
  String? _pendingPath;
  bool _opening = false;

  @override
  void initState() {
    super.initState();

    // `uriLinkStream` also replays the launch link, so both sources funnel into
    // [_requestOpen], which keeps only the latest target.
    final initialPath = ref.read(initialDeepLinkProvider);
    if (initialPath != null) _requestOpen(initialPath);

    _linkSubscription = widget.appLinks.uriLinkStream.listen((Uri uri) {
      log('Deep link received: $uri', name: 'main');
      final path = uri.path + (uri.query.isNotEmpty ? '?${uri.query}' : '');
      _requestOpen(path);
    });
  }

  @override
  void dispose() {
    _linkSubscription?.cancel();
    _authSubscription?.close();
    super.dispose();
  }

  void _requestOpen(String path) {
    _pendingPath = path;
    if (_opening) return;
    _opening = true;
    _openWhenReady();
  }

  /// Waits until the app is ready to be navigated, then opens the pending link.
  ///
  /// Two conditions must hold. Auth must be initialized, since `app.dart` shows
  /// its loading scaffold until then — with no timeout, as restoring a session
  /// can take a network round trip. And the router must have parsed its first
  /// route: [GoRouter.push] stacks onto `routerDelegate.currentConfiguration`,
  /// which stays empty until then — pushing before that silently collapses the
  /// ancestors into a single-entry stack, leaving the page with no way back.
  Future<void> _openWhenReady() async {
    await _whenAuthInitialized();
    if (!mounted) return;

    var mountedRouter = false;
    for (var i = 0; i < _maxRouterMountFrames && !mountedRouter; i++) {
      await WidgetsBinding.instance.endOfFrame;
      if (!mounted) return;
      mountedRouter = ref
          .read(routerProvider)
          .routerDelegate
          .currentConfiguration
          .isNotEmpty;
    }
    if (!mountedRouter) {
      log('Router did not mount in time, opening deep link anyway',
          name: 'main');
    }

    final path = _pendingPath;
    _pendingPath = null;
    _opening = false;
    if (path != null) _openWithHierarchy(path);
  }

  Future<void> _whenAuthInitialized() {
    if (ref.read(authProvider).isInitialized) return Future<void>.value();
    final completer = _authInitialized ??= Completer<void>();
    _authSubscription ??= ref.listenManual(
      authProvider.select((s) => s.isInitialized),
      (_, isInitialized) {
        if (isInitialized && !completer.isCompleted) completer.complete();
      },
    );
    return completer.future;
  }

  void _openWithHierarchy(String path) {
    final router = ref.read(routerProvider);
    final ancestors = ancestorsForDeepLink(path);
    if (ancestors.isEmpty) {
      router.go(path);
      return;
    }
    router.go(ancestors.first);
    for (final p in ancestors.skip(1)) {
      router.push(p);
    }
    router.push(path);
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}
