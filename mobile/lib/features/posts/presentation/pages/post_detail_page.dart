import 'dart:async';

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:share_plus/share_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/markdown_content.dart';
import '../../../../core/widgets/media_attachments.dart';
import '../../../comments/data/comment_repository.dart';
import '../../../comments/presentation/widgets/comment_thread.dart';
import '../../../teams/providers/team_providers.dart';
import '../../data/post_repository.dart';
import '../../domain/post_neighbours.dart';

final postDetailProvider =
    FutureProvider.family<PostDto, ({String teamSlug, String postSlug})>((
      ref,
      params,
    ) async {
      final repository = ref.watch(postRepositoryProvider);
      return repository.getPost(params.teamSlug, params.postSlug);
    });

/// L'écran d'une publication.
///
/// **Aucun bloc auteur.** La maquette en dessine un — avatar, nom, date — et
/// `PostDto` n'expose ni `createdBy` ni `createdById` : il n'est pas
/// alimentable, et le fabriquer reviendrait à attribuer un texte à quelqu'un.
/// Seule la date longue est conservée (§5.1).
class PostDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String postSlug;

  /// Voisins du fil d'où la publication a été ouverte. Nuls en ouverture par
  /// deeplink — voir [PostNeighbours].
  final PostNeighbours? neighbours;

  const PostDetailPage({
    super.key,
    required this.teamSlug,
    required this.postSlug,
    this.neighbours,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, postSlug: postSlug);
    final AsyncValue<PostDto> postAsync = ref.watch(postDetailProvider(params));

    return postAsync.when(
      data: (PostDto post) =>
          _PostDetailContent(post: post, neighbours: neighbours),
      loading: () => Scaffold(
        appBar: PdlAppBar(
          onBack: () => context.pop(),
          backSemanticLabel: 'common.back'.tr(),
        ),
        body: const SingleChildScrollView(
          padding: EdgeInsets.all(PdlSpacing.section),
          child: PdlSkeletonCardList(
            variant: PdlSkeletonCardVariant.media,
            count: 2,
          ),
        ),
      ),
      error: (Object error, StackTrace stack) => Scaffold(
        appBar: PdlAppBar(
          onBack: () => context.pop(),
          backSemanticLabel: 'common.back'.tr(),
        ),
        body: Center(
          child: SingleChildScrollView(
            child: PdlEmptyState(
              variant: PdlEmptyVariant.error,
              title: 'common.loadError'.tr(),
              message: getErrorMessage(error, stack),
              actions: <Widget>[
                PdlButton(
                  label: 'common.retry'.tr(),
                  variant: PdlButtonVariant.outline,
                  onPressed: () => ref.invalidate(postDetailProvider(params)),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PostDetailContent extends ConsumerWidget {
  const _PostDetailContent({required this.post, this.neighbours});

  final PostDto post;
  final PostNeighbours? neighbours;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final bool? isMember = ref.watch(teamMembershipProvider(post.team.slug));
    final List<AssetDto> attachments = post.media.assets.attachments;

    return Scaffold(
      backgroundColor: c.bg,
      body: CustomScrollView(
        slivers: <Widget>[
          SliverToBoxAdapter(child: _Cover(post: post)),
          SliverPadding(
            padding: const EdgeInsets.all(PdlSpacing.section),
            sliver: SliverList.list(
              children: <Widget>[
                _identity(context),
                const SizedBox(height: PdlSpacing.section),
                if (post.media.markdown.trim().isNotEmpty)
                  MarkdownContent(
                    data: post.media.markdown,
                    images: post.media.assets.images,
                  ),
                MediaAttachments(attachments: attachments),
                const SizedBox(height: PdlSpacing.section),
                // `commentCount` absent signifie « vous n'avez pas le droit de
                // lire les commentaires » : un non-membre n'est même pas
                // informé qu'il y en a zéro, donc le fil ne se monte pas.
                if (post.commentCount != null)
                  CommentThread(
                    target: CommentTarget(
                      entity: CommentEntity.post,
                      teamSlug: post.team.slug,
                      slug: post.slug,
                    ),
                    canComment: isMember ?? false,
                  )
                else
                  PdlBanner(
                    tone: PdlBannerTone.info,
                    message: 'posts.commentsMembersOnly'.tr(),
                    action: PdlButton(
                      label: 'teams.viewTeam'.tr(),
                      variant: PdlButtonVariant.outline,
                      size: PdlButtonSize.sm,
                      onPressed: () => context.push(Paths.team(post.team.slug)),
                    ),
                  ),
              ],
            ),
          ),
          // Absent en ouverture froide, et sans espace résiduel : le composant
          // rend `SizedBox.shrink()` quand il n'a aucun voisin.
          SliverToBoxAdapter(
            child: PdlPrevNextNav(
              previous: _target(context, neighbours?.previous, forward: false),
              next: _target(context, neighbours?.next, forward: true),
            ),
          ),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }

  PdlPrevNextTarget? _target(
    BuildContext context,
    PostNeighbour? neighbour, {
    required bool forward,
  }) {
    if (neighbour == null) return null;
    return PdlPrevNextTarget(
      caption: forward ? 'posts.next'.tr() : 'posts.previous'.tr(),
      title: neighbour.name,
      // `pushReplacement` : enchaîner dix publications ne doit pas empiler dix
      // écrans à remonter un à un pour revenir au fil.
      onTap: () => context.pushReplacement(
        Paths.post(post.team.slug, neighbour.slug),
        // Recentrée sur le voisin : sans cela, il hériterait des voisins de
        // la publication qu'on vient de quitter.
        extra: neighbours?.movedTo(neighbour.slug),
      ),
    );
  }

  /// Ligne d'équipe, titre multi-ligne, badges, date longue.
  Widget _identity(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final DateTime? at = DateTime.tryParse(post.dateTime)?.toLocal();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        PdlTeamLine(
          label: post.team.name,
          onTap: () => context.push(Paths.team(post.team.slug)),
        ),
        const SizedBox(height: PdlSpacing.chipGap),
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            // Le titre n'est **pas** tronqué ici : sur un détail, il est le
            // sujet de l'écran, pas une ligne de liste.
            Expanded(child: Text(post.name, style: t.screenTitle)),
            const SizedBox(width: PdlSpacing.chipGap),
            PdlBadgeStack(
              badges: <Widget>[
                PdlBadge(
                  label: 'publicationType.post'.tr(),
                  tone: PublicationType.post.tone(c),
                ),
                if (post.status == 'DRAFT')
                  PdlBadge(
                    label: 'status.draft'.tr(),
                    tone: Status.draft.tone(c),
                  ),
              ],
            ),
          ],
        ),
        if (at != null) ...<Widget>[
          const SizedBox(height: 4),
          // La seule chose que la maquette mettait dans son bloc auteur et que
          // le contrat porte réellement.
          Text(AppFormatters.formatFullDate(at), style: t.sub),
        ],
      ],
    );
  }
}

/// La couverture : 208 px de 16:9, zoomable en plein écran, app bar en
/// surimpression avec le partage.
class _Cover extends ConsumerWidget {
  const _Cover({required this.post});

  final PostDto post;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final String? url = post.thumbnailUrl;

    final Widget media = url == null
        ? PdlCardMedia(
            tone: PdlMediaTone.post,
            icon: PdlIcons.post,
            height: PdlMetrics.media16x9,
          )
        : GestureDetector(
            onTap: () => _openFullScreen(context, ref, url),
            child: Semantics(
              button: true,
              label: 'posts.openCover'.tr(),
              child: AuthenticatedImage(
                imageUrl: url,
                size: 1080,
                width: double.infinity,
                height: PdlMetrics.media16x9,
                fit: BoxFit.cover,
                placeholder: const PdlSkeleton(
                  width: double.infinity,
                  height: PdlMetrics.media16x9,
                ),
              ),
            ),
          );

    return SizedBox(
      height: PdlMetrics.media16x9,
      child: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          media,
          const Positioned(
            top: 0,
            left: 0,
            right: 0,
            child: PdlScrim.top(height: 96),
          ),
          Positioned(
            top: 0,
            left: 0,
            right: 0,
            child: SafeArea(
              bottom: false,
              child: PdlAppBar(
                variant: PdlAppBarVariant.overlay,
                onBack: () => context.pop(),
                backSemanticLabel: 'common.back'.tr(),
                actions: <Widget>[
                  PdlAppBarAction(
                    icon: PdlIcons.share,
                    semanticLabel: 'routes.share'.tr(),
                    onPressed: () => unawaited(_share(context)),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _openFullScreen(BuildContext context, WidgetRef ref, String url) {
    final ImageProvider? provider = AuthenticatedDecorationImage.fromUrl(
      url,
      ref.read(accessTokenHolderProvider),
      size: 1920,
    );
    if (provider == null) return;
    PdlImageViewer.show(
      context,
      imageProvider: provider,
      closeSemanticLabel: 'links.imageClose'.tr(),
    );
  }

  Future<void> _share(BuildContext context) async {
    final RenderBox? box = context.findRenderObject() as RenderBox?;
    await SharePlus.instance.share(
      ShareParams(
        text: post.name,
        sharePositionOrigin: box == null
            ? Rect.zero
            : box.localToGlobal(Offset.zero) & box.size,
      ),
    );
  }
}

/// Une pièce jointe.
///
/// Vignette de 56 px quand l'asset est une image — `imageUrl` porte le gabarit
/// `{size}` d'imgproxy —, icône sinon. **Pas de poids affiché** : `AssetDto`
/// porte `contentType`, pas d'octets, et « PDF · 240 Ko » serait une invention.
