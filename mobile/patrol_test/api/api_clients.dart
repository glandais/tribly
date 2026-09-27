import 'backend_client.dart';
import 'mailhog_client.dart';

final class ApiClients {
  ApiClients() : mailhog = MailhogClient() {
    backend = BackendClient(mailhog);
  }

  final MailhogClient mailhog;
  late final BackendClient backend;
}
