import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final invitationsRepositoryProvider = Provider<InvitationsRepository>((ref) {
  return InvitationsRepository(ref.watch(invitationsClientProvider));
});

/// Les invitations **reçues** par l'utilisateur connecté.
///
/// Celles qu'une équipe envoie (`TeamInvitationsClient`) sont une affaire
/// d'administrateur et restent sur le web.
class InvitationsRepository {
  InvitationsRepository(this._client);

  final InvitationsClient _client;

  /// Les invitations en attente adressées à l'utilisateur.
  Future<List<MyInvitationDto>> listMine() async {
    final MyInvitationListResponse response = await _client.listMyInvitations();
    return response.invitations;
  }

  /// Accepte une invitation : l'utilisateur devient membre de l'équipe.
  Future<void> accept(String invitationId) async {
    await _client.acceptMyInvitation(invitationId: invitationId);
  }
}
