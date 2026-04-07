import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

final postRepositoryProvider = Provider<PostRepository>((ref) {
  return PostRepository(ref.watch(postsClientProvider));
});

class PostRepository {
  final PostsClient _postsClient;

  PostRepository(this._postsClient);

  Future<PostDto> getPost(String teamSlug, String postSlug) {
    return _postsClient.getPost(teamSlug: teamSlug, postSlug: postSlug);
  }
}
