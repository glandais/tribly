import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/team_banner.dart';
import '../../../../core/widgets/widgets.dart';
import '../../data/post_repository.dart';

final postDetailProvider =
    FutureProvider.family<PostDto, ({String teamSlug, String postSlug})>((
      ref,
      params,
    ) async {
      final repository = ref.watch(postRepositoryProvider);
      return repository.getPost(params.teamSlug, params.postSlug);
    });

class PostDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String postSlug;

  const PostDetailPage({
    super.key,
    required this.teamSlug,
    required this.postSlug,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, postSlug: postSlug);
    final postAsync = ref.watch(postDetailProvider(params));

    return postAsync.when(
      data: (post) => _PostDetailContent(post: post),
      loading: () => Scaffold(
        appBar: AppBar(),
        body: const Center(child: CircularProgressIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.error_outline,
                size: 48,
                color: Theme.of(context).colorScheme.error,
              ),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(postDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _PostDetailContent extends StatelessWidget {
  final PostDto post;

  const _PostDetailContent({required this.post});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(pinned: true, title: Text(post.name)),

          // Team
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: TeamBanner(team: post.team),
            ),
          ),

          // Date
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Icon(
                    Icons.calendar_today,
                    size: 16,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    AppFormatters.formatFullDate(DateTime.parse(post.dateTime)),
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: Theme.of(context).colorScheme.outline,
                    ),
                  ),
                ],
              ),
            ),
          ),

          // Content
          if (post.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: MarkdownContent(
                  data: post.media.markdown,
                  images: post.media.assets.images,
                ),
              ),
            ),

          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }
}
