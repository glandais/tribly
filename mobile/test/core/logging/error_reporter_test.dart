import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/logging/app_log.dart';
import 'package:pedalons/core/logging/client_context.dart';
import 'package:pedalons/core/logging/error_reporter.dart';
import 'package:pedalons/core/preferences/error_reports_preference.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Un serveur de rapports : il note ce qu'il reçoit, ou échoue comme on lui
/// dit.
class _Server {
  final List<ErrorReportRequest> received = <ErrorReportRequest>[];
  Object? failWith;

  Future<void> send(ErrorReportRequest request) async {
    final Object? failure = failWith;
    if (failure != null) throw failure;
    received.add(request);
  }
}

DioException _offline() => DioException(
  requestOptions: RequestOptions(path: '/api/feedback/errors'),
  type: DioExceptionType.connectionError,
);

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late SharedPreferences prefs;
  late AppLog log;
  late _Server server;

  Future<ErrorReporter> newReporter({
    Map<String, Object> values = const <String, Object>{},
    bool reuse = false,
  }) async {
    if (!reuse) {
      SharedPreferences.setMockInitialValues(values);
      prefs = await SharedPreferences.getInstance();
    }
    return ErrorReporter(
      preferences: prefs,
      log: log,
      context: ClientContextBuilder(platform: 'ANDROID', locale: () => 'fr'),
    );
  }

  setUp(() {
    log = AppLog();
    server = _Server();
  });

  test('connecté : le rapport part tout de suite, journal joint', () async {
    final ErrorReporter reporter = await newReporter();
    await reporter.attach(server.send);
    log.info('navigation', '/equipes/gaby');

    await reporter.report(StateError('boom'), StackTrace.current);

    final ErrorReportRequest sent = server.received.single;
    expect(sent.error.type, 'StateError');
    expect(sent.error.message, 'Bad state: boom');
    expect(sent.error.stack, contains('error_reporter_test.dart'));
    expect(sent.context.platform, 'ANDROID');
    expect(
      sent.logs!.map((ClientLogEntryDto e) => e.message),
      contains('/equipes/gaby'),
    );
    // L'erreur elle-même entre au journal.
    expect(log.entries.last.level, AppLogLevel.error);
  });

  test('une même erreur ne part qu\'une fois par session', () async {
    final ErrorReporter reporter = await newReporter();
    await reporter.attach(server.send);
    for (int i = 0; i < 3; i++) {
      await reporter.report(StateError('same'), StackTrace.current);
    }
    await reporter.report(StateError('other'), StackTrace.current);
    expect(
      server.received.map((ErrorReportRequest r) => r.error.message),
      <String>['Bad state: same', 'Bad state: other'],
    );
  });

  test('une erreur pendant un envoi en cours n\'est pas perdue', () async {
    final ErrorReporter reporter = await newReporter();
    final List<ErrorReportRequest> received = <ErrorReportRequest>[];
    await reporter.attach((ErrorReportRequest r) async {
      await Future<void>.delayed(const Duration(milliseconds: 5));
      received.add(r);
    });
    await Future.wait(<Future<void>>[
      reporter.report(StateError('first'), StackTrace.current),
      reporter.report(StateError('second'), StackTrace.current),
    ]);
    expect(received, hasLength(2));
  });

  test('une erreur répétée ne s\'écrit qu\'une fois au journal', () async {
    final ErrorReporter reporter = await newReporter();
    for (int i = 0; i < 10; i++) {
      await reporter.report(StateError('overflow'), StackTrace.current);
    }
    expect(log.entries, hasLength(1));
  });

  test('pas plus de cinq rapports par session', () async {
    final ErrorReporter reporter = await newReporter();
    await reporter.attach(server.send);
    for (int i = 0; i < 8; i++) {
      await reporter.report(StateError('e$i'), StackTrace.current);
    }
    expect(server.received, hasLength(5));
  });

  test('sans session, le rapport attend et part à la connexion', () async {
    final ErrorReporter reporter = await newReporter();
    await reporter.report(StateError('before login'), StackTrace.current);
    expect(server.received, isEmpty);
    expect(reporter.queued, hasLength(1));

    await reporter.attach(server.send);
    expect(server.received.single.error.message, 'Bad state: before login');
    expect(reporter.queued, isEmpty);
  });

  test('la file survit au redémarrage de l\'app', () async {
    final ErrorReporter first = await newReporter();
    await first.report(StateError('crash'), StackTrace.current);

    final ErrorReporter second = await newReporter(reuse: true);
    await second.attach(server.send);
    expect(server.received.single.error.message, 'Bad state: crash');
  });

  test('hors ligne, le rapport retourne en file pour la connexion suivante, '
      'sans nouvelle tentative d\'ici là', () async {
    final ErrorReporter reporter = await newReporter();
    server.failWith = _offline();
    await reporter.attach(server.send);
    await reporter.report(StateError('offline'), StackTrace.current);
    expect(server.received, isEmpty);
    expect(reporter.queued, hasLength(1));

    server.failWith = null;
    // Rien ne réessaie tout seul…
    await Future<void>.delayed(const Duration(milliseconds: 10));
    expect(server.received, isEmpty);
    // …la connexion suivante vide la file.
    await reporter.attach(server.send);
    expect(server.received.single.error.message, 'Bad state: offline');
  });

  test('une autre erreur d\'envoi perd le rapport, sans lever', () async {
    final ErrorReporter reporter = await newReporter();
    server.failWith = StateError('500');
    await reporter.attach(server.send);
    await reporter.report(StateError('lost'), StackTrace.current);
    expect(reporter.queued, isEmpty);
  });

  test('réglage coupé : rien ne part, rien n\'attend', () async {
    final ErrorReporter reporter = await newReporter(
      values: <String, Object>{kAutoErrorReportsKey: false},
    );
    await reporter.report(StateError('quiet'), StackTrace.current);
    await reporter.attach(server.send);
    await reporter.report(StateError('quiet 2'), StackTrace.current);
    expect(server.received, isEmpty);
    expect(reporter.queued, isEmpty);
  });

  test('réglage coupé après coup : la file est vidée sans envoi', () async {
    final ErrorReporter reporter = await newReporter();
    await reporter.report(StateError('queued'), StackTrace.current);
    await prefs.setBool(kAutoErrorReportsKey, false);
    await reporter.attach(server.send);
    expect(server.received, isEmpty);
    expect(reporter.queued, isEmpty);
  });

  test('build de développement : rien ne part', () async {
    SharedPreferences.setMockInitialValues(<String, Object>{});
    prefs = await SharedPreferences.getInstance();
    final ErrorReporter reporter = ErrorReporter(
      preferences: prefs,
      log: log,
      context: ClientContextBuilder(platform: 'ANDROID'),
      autoSendAllowed: false,
    );
    await reporter.attach(server.send);
    await reporter.report(StateError('dev'), StackTrace.current);
    expect(server.received, isEmpty);
  });

  group('plantage non signalé', () {
    test(
      'se propose à la session suivante, pas à la session en cours',
      () async {
        final ErrorReporter first = await newReporter();
        await first.report(
          StateError('fatal'),
          StackTrace.current,
          fatal: true,
        );
        expect(first.unreportedFatal, isNull);

        final ErrorReporter second = await newReporter(reuse: true);
        expect(second.unreportedFatal!.message, 'Bad state: fatal');
        expect(second.unreportedFatal!.stack, contains('error_reporter_test'));

        await second.clearUnreportedFatal();
        expect(second.unreportedFatal, isNull);
        final ErrorReporter third = await newReporter(reuse: true);
        expect(third.unreportedFatal, isNull);
      },
    );

    test('est retenu même réglage coupé', () async {
      final ErrorReporter first = await newReporter(
        values: <String, Object>{kAutoErrorReportsKey: false},
      );
      await first.report(StateError('fatal'), StackTrace.current, fatal: true);
      final ErrorReporter second = await newReporter(reuse: true);
      expect(second.unreportedFatal, isNotNull);
    });

    test('une erreur ordinaire ne l\'est pas', () async {
      final ErrorReporter first = await newReporter();
      await first.report(StateError('minor'), StackTrace.current);
      final ErrorReporter second = await newReporter(reuse: true);
      expect(second.unreportedFatal, isNull);
    });
  });
}
