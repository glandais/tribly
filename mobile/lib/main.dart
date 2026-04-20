import 'dart:developer';

import 'package:app_links/app_links.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app.dart';
import 'config/router.dart';

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

class _DeepLinkHandlerState extends ConsumerState<_DeepLinkHandler> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final initialPath = ref.read(initialDeepLinkProvider);
      if (initialPath != null) _openWithHierarchy(initialPath);
    });

    widget.appLinks.uriLinkStream.listen((Uri uri) {
      log('Deep link received: $uri', name: 'main');
      final path = uri.path + (uri.query.isNotEmpty ? '?${uri.query}' : '');
      _openWithHierarchy(path);
    });
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
