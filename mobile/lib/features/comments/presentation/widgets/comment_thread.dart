import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/comment_repository.dart';
import '../../providers/comment_thread_provider.dart';

/// Le fil de commentaires — un niveau de réponse, pas deux.
///
/// Un fil de discussion imbriqué sans limite finit en escalier de 20 px de
/// large sur un téléphone. La maquette n'en montre qu'un niveau, et l'API le
/// permet sans le forcer : répondre à une réponse rattache au même parent.
class CommentThread extends ConsumerStatefulWidget {
  const CommentThread({
    super.key,
    required this.target,
    this.canComment = true,
  });

  final CommentTarget target;

  /// Les commentaires sont réservés aux membres : un non-membre lit le fil
  /// mais ne voit pas le composeur.
  final bool canComment;

  @override
  ConsumerState<CommentThread> createState() => _CommentThreadState();
}

class _CommentThreadState extends ConsumerState<CommentThread> {
  final TextEditingController _composer = TextEditingController();
  final FocusNode _focus = FocusNode();

  /// À qui l'on répond, s'il y a lieu. Un seul composeur pour tout le fil :
  /// la maquette en montrait deux formes, elles sont unifiées sur celle-ci.
  CommentDto? _replyTo;
  bool _posting = false;
  Object? _error;

  @override
  void dispose() {
    _composer.dispose();
    _focus.dispose();
    super.dispose();
  }

  CommentThreadNotifier get _notifier =>
      ref.read(commentThreadProvider(widget.target).notifier);

  @override
  Widget build(BuildContext context) {
    final PagedListState<CommentDto> state = ref.watch(
      commentThreadProvider(widget.target),
    );
    final String? currentUserId = ref.watch(
      authProvider.select((AuthState s) => s.user?.id),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(
          title: 'rides.comments'.tr(),
          count: state.total == null
              ? null
              : 'comments.count'.plural(state.total!),
          padding: const EdgeInsets.only(bottom: 10),
        ),
        if (widget.canComment) ...<Widget>[
          _composerRow(context),
          const SizedBox(height: 14),
        ],
        if (_error != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.danger,
            message: getErrorMessage(_error!),
            onDismiss: () => setState(() => _error = null),
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: 14),
        ],
        if (state.isLoadingInitial)
          const PdlSkeletonCard(variant: PdlSkeletonCardVariant.person)
        else if (state.initialError != null)
          PdlEmptyState(
            variant: PdlEmptyVariant.error,
            title: 'common.loadError'.tr(),
            message: getErrorMessage(state.initialError!),
            actions: <Widget>[
              PdlButton(
                label: 'common.retry'.tr(),
                variant: PdlButtonVariant.outline,
                onPressed: _notifier.loadFirstPage,
              ),
            ],
          )
        else if (state.items.isEmpty)
          PdlEmptyState(
            variant: PdlEmptyVariant.empty,
            title: 'comments.emptyTitle'.tr(),
            message: 'comments.emptyMessage'.tr(),
          )
        else
          for (final CommentDto comment in state.items)
            Padding(
              padding: const EdgeInsets.only(bottom: 14),
              child: _thread(comment, currentUserId),
            ),
        if (state.hasMore || state.nextError != null)
          PdlPagedListFooter(
            isLoadingNext: state.isLoadingNext,
            hasMore: state.hasMore,
            isEmpty: state.items.isEmpty,
            hasError: state.nextError != null,
            progressLabel: 'comments.progress'.tr(
              namedArgs: <String, String>{
                'loaded': '${state.items.length}',
                'total': '${state.total ?? state.items.length}',
              },
            ),
            loadingLabel: 'pagination.loadingMore'.tr(),
            endLabel: 'pagination.endOfList'.tr(),
            errorLabel: 'pagination.nextPageError'.tr(),
            retryLabel: 'common.retry'.tr(),
            onRetry: _notifier.loadNextPage,
          ),
      ],
    );
  }

  // ── Un fil : le commentaire racine et ses réponses ──────────────────────
  Widget _thread(CommentDto comment, String? currentUserId) {
    final PdlColors c = context.pdl;
    final List<CommentDto> replies = _notifier.repliesOf(comment);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        _comment(comment, currentUserId),
        for (final CommentDto reply in replies)
          Padding(
            padding: const EdgeInsets.only(top: 14, left: 14),
            child: Container(
              padding: const EdgeInsets.only(left: 14),
              decoration: BoxDecoration(
                border: Border(
                  left: BorderSide(color: c.borderSubtle, width: 2),
                ),
              ),
              child: _comment(reply, currentUserId, isReply: true),
            ),
          ),
        if (_notifier.hasMoreReplies(comment))
          Align(
            alignment: Alignment.centerLeft,
            child: PdlButton(
              label: 'comments.showReplies'.plural(
                comment.replyCount - replies.length,
              ),
              variant: PdlButtonVariant.text,
              size: PdlButtonSize.sm,
              onPressed: () => _notifier.loadReplies(comment),
            ),
          ),
      ],
    );
  }

  Widget _comment(
    CommentDto comment,
    String? currentUserId, {
    bool isReply = false,
  }) {
    final PdlTypography t = context.pdlText;
    final bool mine = comment.author.id == currentUserId;
    final DateTime? createdAt = DateTime.tryParse(comment.createdAt);

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        PdlAvatar(
          name: comment.author.displayName,
          imageUrl: comment.author.avatarUrl,
          size: 40,
          isCurrentUser: mine,
        ),
        const SizedBox(width: 10),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Row(
                children: <Widget>[
                  Flexible(
                    child: Text(
                      comment.author.displayName,
                      style: t.bodyStrong.copyWith(fontSize: 14),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  const SizedBox(width: PdlSpacing.chipGap),
                  if (createdAt != null)
                    Text(AppFormatters.formatRelative(createdAt), style: t.xs),
                  // « Supprimer » n'apparaît que pour l'auteur : c'est la seule
                  // action que l'API autorise, et la montrer à d'autres ferait
                  // promettre un 403.
                  if (mine) ...<Widget>[
                    const SizedBox(width: PdlSpacing.chipGap),
                    PdlButton(
                      label: 'common.delete'.tr(),
                      variant: PdlButtonVariant.danger,
                      size: PdlButtonSize.sm,
                      onPressed: () => _remove(comment),
                    ),
                  ],
                ],
              ),
              const SizedBox(height: 2),
              PdlMarkdownBody(data: comment.content),
              if (widget.canComment && !isReply)
                Align(
                  alignment: Alignment.centerLeft,
                  child: PdlButton(
                    label: 'comments.reply'.tr(),
                    variant: PdlButtonVariant.text,
                    size: PdlButtonSize.sm,
                    onPressed: () {
                      setState(() => _replyTo = comment);
                      _focus.requestFocus();
                    },
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }

  // ── Le composeur, unique ────────────────────────────────────────────────
  Widget _composerRow(BuildContext context) {
    final CommentDto? replyTo = _replyTo;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        if (replyTo != null)
          Padding(
            padding: const EdgeInsets.only(bottom: 6),
            child: Row(
              children: <Widget>[
                Expanded(
                  child: Text(
                    'comments.replyingTo'.tr(
                      namedArgs: <String, String>{
                        'name': replyTo.author.displayName,
                      },
                    ),
                    style: context.pdlText.xs,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                PdlButton(
                  label: 'common.cancel'.tr(),
                  variant: PdlButtonVariant.text,
                  size: PdlButtonSize.sm,
                  onPressed: () => setState(() => _replyTo = null),
                ),
              ],
            ),
          ),
        Row(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: <Widget>[
            Expanded(
              // Un `TextField` nu, habillé par `inputDecorationTheme` :
              // `PdlSearchField` est un champ de recherche à anti-rebond, pas
              // une zone de saisie multiligne, et le tordre en composeur
              // coûterait plus cher que ces six lignes.
              child: TextField(
                controller: _composer,
                focusNode: _focus,
                minLines: 2,
                maxLines: 5,
                textCapitalization: TextCapitalization.sentences,
                keyboardType: TextInputType.multiline,
                decoration: InputDecoration(
                  hintText: 'comments.placeholder'.tr(),
                ),
              ),
            ),
            const SizedBox(width: PdlSpacing.chipGap),
            PdlButton(
              label: 'comments.send'.tr(),
              loadingLabel: 'comments.sending'.tr(),
              loading: _posting,
              onPressed: _post,
            ),
          ],
        ),
      ],
    );
  }

  Future<void> _post() async {
    final String content = _composer.text.trim();
    if (content.isEmpty || _posting) return;
    setState(() {
      _posting = true;
      _error = null;
    });
    try {
      await _notifier.post(content: content, parentId: _replyTo?.id);
      if (!mounted) return;
      _composer.clear();
      setState(() => _replyTo = null);
    } catch (error) {
      if (mounted) setState(() => _error = error);
    } finally {
      if (mounted) setState(() => _posting = false);
    }
  }

  Future<void> _remove(CommentDto comment) async {
    try {
      await _notifier.remove(comment);
    } catch (error) {
      if (mounted) setState(() => _error = error);
    }
  }
}
