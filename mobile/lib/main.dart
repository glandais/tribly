import 'package:app_links/app_links.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app.dart';
import 'config/router.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Handle deep links
  final appLinks = AppLinks();

  // Handle initial deep link (app opened via link)
  final initialLink = await appLinks.getInitialLink();
  if (initialLink != null) {
    debugPrint('Initial deep link: $initialLink');
  }

  runApp(
    ProviderScope(
      overrides: [
        // If there's an initial deep link, we could pass it here
        // For now, GoRouter handles it via the URL
      ],
      child: _DeepLinkHandler(
        appLinks: appLinks,
        child: const TriblyApp(),
      ),
    ),
  );
}

/// Widget that handles deep links while the app is running
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
    // Listen for deep links while app is running
    widget.appLinks.uriLinkStream.listen((Uri uri) {
      debugPrint('Deep link received: $uri');
      // GoRouter handles the navigation via routerConfig
      final router = ref.read(routerProvider);
      router.go(uri.path + (uri.query.isNotEmpty ? '?${uri.query}' : ''));
    });
  }

  @override
  Widget build(BuildContext context) {
    return widget.child;
  }
}
