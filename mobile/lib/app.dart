import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'config/locale_context.dart';
import 'config/router.dart';
import 'core/theme/theme.dart';
import 'features/auth/providers/auth_provider.dart';

class PedalonsApp extends ConsumerStatefulWidget {
  const PedalonsApp({super.key});

  @override
  ConsumerState<PedalonsApp> createState() => _PedalonsAppState();
}

class _PedalonsAppState extends ConsumerState<PedalonsApp> {
  @override
  void initState() {
    super.initState();
    // Initialize auth state
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(authProvider.notifier).initialize();
    });
  }

  @override
  Widget build(BuildContext context) {
    setCurrentLocale(context.locale.languageCode);
    final authState = ref.watch(authProvider);
    final router = ref.watch(routerProvider);

    // Show loading while initializing
    if (!authState.isInitialized) {
      return MaterialApp(
        title: 'Pédalons',
        theme: PedalonsTheme.build(Brightness.light),
        darkTheme: PedalonsTheme.build(Brightness.dark),
        localizationsDelegates: context.localizationDelegates,
        supportedLocales: context.supportedLocales,
        locale: context.locale,
        home: const Scaffold(body: Center(child: CircularProgressIndicator())),
      );
    }

    return MaterialApp.router(
      title: 'Pédalons',
      theme: PedalonsTheme.build(Brightness.light),
      darkTheme: PedalonsTheme.build(Brightness.dark),
      localizationsDelegates: context.localizationDelegates,
      supportedLocales: context.supportedLocales,
      locale: context.locale,
      routerConfig: router,
      debugShowCheckedModeBanner: false,
    );
  }
}
