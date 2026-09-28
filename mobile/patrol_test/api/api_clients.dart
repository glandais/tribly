import 'backend_client.dart';
import 'mailpit_client.dart';

final class ApiClients {
  ApiClients() : mailpit = MailpitClient() {
    backend = BackendClient(mailpit);
  }

  final MailpitClient mailpit;
  late final BackendClient backend;
}
