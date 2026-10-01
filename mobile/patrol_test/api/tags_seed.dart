import 'backend_client.dart';

/// Seeding of a team's tags (ledger `MOB-39`).
extension TagsSeed on BackendClient {
  /// A tag of [teamSlug] for one kind of content ([type]: `ROUTE`, `RIDE`, `AD`…), created by a
  /// team admin as the web's tag manager does. Its `id` goes into a content's `tagIds`.
  Future<Json> newTag(
    TestUser by,
    String teamSlug, {
    required String type,
    required String label,
    String color = 'BLUE',
  }) => post(by, '/api/teams/$teamSlug/tags', {
    'type': type,
    'label': label,
    'color': color,
  });
}
