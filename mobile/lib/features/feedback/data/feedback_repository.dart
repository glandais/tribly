import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final Provider<FeedbackRepository> feedbackRepositoryProvider =
    Provider<FeedbackRepository>(
      (Ref ref) => FeedbackRepository(ref.watch(feedbackClientProvider)),
    );

/// Les deux envois de l'API de retours : le signalement écrit par le membre,
/// le rapport d'erreur envoyé sans lui.
class FeedbackRepository {
  FeedbackRepository(this._client);

  final FeedbackClient _client;

  Future<void> send(FeedbackRequest request) =>
      _client.sendFeedback(body: request);

  Future<void> reportError(ErrorReportRequest request) =>
      _client.reportClientError(body: request);
}
