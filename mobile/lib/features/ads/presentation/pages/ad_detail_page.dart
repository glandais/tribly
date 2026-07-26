import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
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
import '../../../../core/widgets/team_banner.dart';
import '../../../auth/domain/auth_state.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/ad_repository.dart';
import '../widgets/ad_contact_sheet.dart';
import '../widgets/ad_location_map.dart';

/// Hauteur de la galerie du détail.
const double _kGalleryHeight = 260;

final adDetailProvider =
    FutureProvider.family<AdDto, ({String teamSlug, String adSlug})>((
      ref,
      params,
    ) async {
      final repository = ref.watch(adRepositoryProvider);
      return repository.getAd(params.teamSlug, params.adSlug);
    });

class AdDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String adSlug;

  const AdDetailPage({super.key, required this.teamSlug, required this.adSlug});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, adSlug: adSlug);
    final AsyncValue<AdDto> adAsync = ref.watch(adDetailProvider(params));

    return adAsync.when(
      data: (AdDto ad) => _AdDetailContent(ad: ad),
      loading: () => Scaffold(
        appBar: PdlAppBar(onBack: () => Navigator.of(context).maybePop()),
        // Défilable : deux gabarits à bandeau dépassent la hauteur d'un petit
        // écran, et un gabarit qui déborde rend une bande d'erreur rayée.
        body: const SingleChildScrollView(
          padding: EdgeInsets.all(PdlSpacing.section),
          child: PdlSkeletonCardList(
            variant: PdlSkeletonCardVariant.media,
            count: 2,
          ),
        ),
      ),
      error: (Object error, StackTrace stack) => Scaffold(
        appBar: PdlAppBar(onBack: () => Navigator.of(context).maybePop()),
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
                  onPressed: () => ref.invalidate(adDetailProvider(params)),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _AdDetailContent extends ConsumerStatefulWidget {
  const _AdDetailContent({required this.ad});

  final AdDto ad;

  @override
  ConsumerState<_AdDetailContent> createState() => _AdDetailContentState();
}

class _AdDetailContentState extends ConsumerState<_AdDetailContent> {
  /// Vrai une fois le message parti : la confirmation est un **état** de
  /// l'écran, pas un `SnackBar` qui disparaît avant d'être lu.
  bool _messageSent = false;

  /// Vrai quand le serveur a répondu `AD_CONTACT_OPTED_OUT` : l'auteur s'est
  /// rendu injoignable, et réessayer n'y changerait rien.
  bool _optedOut = false;

  AdDto get ad => widget.ad;

  Future<void> _contact() async {
    final AdContactOutcome? outcome = await showAdContactSheet(
      context,
      teamSlug: ad.team.slug,
      adSlug: ad.slug,
      sellerName: ad.createdByDisplayName,
    );
    if (!mounted || outcome == null) return;
    setState(() {
      _messageSent = outcome == AdContactOutcome.sent;
      _optedOut = outcome == AdContactOutcome.optedOut;
    });
  }

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final AdType type = AdType.fromJson(ad.adType);
    final DateTime? at = DateTime.tryParse(ad.createdAt)?.toLocal();
    final FormattedPrice price = AppFormatters.formatPrice(
      ad.price,
      rentalPeriod: ad.rentalPeriod,
    );
    final String? currentUserId = ref.watch(
      authProvider.select((AuthState s) => s.user?.id),
    );

    return Scaffold(
      backgroundColor: c.bg,
      body: CustomScrollView(
        slivers: <Widget>[
          SliverToBoxAdapter(child: _Gallery(ad: ad)),
          SliverPadding(
            padding: const EdgeInsets.all(PdlSpacing.section),
            sliver: SliverList.list(
              children: <Widget>[
                TeamBanner(team: ad.team),
                const SizedBox(height: PdlSpacing.cardTight),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Expanded(child: Text(ad.name, style: t.screenTitle)),
                    const SizedBox(width: PdlSpacing.chipGap),
                    PdlBadgeStack(
                      badges: <Widget>[
                        PdlBadge(
                          label: 'ads.adType.${ad.adType}'.tr(),
                          tone: type.tone(c),
                          size: PdlBadgeSize.lg,
                        ),
                        if (ad.status == 'DRAFT')
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
                  Text(AppFormatters.formatFullDate(at), style: t.sub),
                ],
                const SizedBox(height: PdlSpacing.section),
                // `note` porte la période d'une location et rien d'autre : le
                // lieu a sa propre section, et l'absence de prix est déjà dite
                // par `amount` — « Prix à négocier », jamais un tiret.
                PdlPriceBlock(price: price.amount, note: price.period),
                if (ad.media.markdown.isNotEmpty) ...<Widget>[
                  const SizedBox(height: PdlSpacing.section),
                  PdlSectionHeader(title: 'ads.detail.description'.tr()),
                  MarkdownContent(
                    data: ad.media.markdown,
                    images: ad.media.assets.images,
                  ),
                ],
                if (ad.locationGeometry != null ||
                    ad.locationDescription != null) ...<Widget>[
                  const SizedBox(height: PdlSpacing.section),
                  PdlSectionHeader(title: 'ads.detail.location'.tr()),
                  if (ad.locationDescription != null)
                    Padding(
                      padding: const EdgeInsets.only(
                        bottom: PdlSpacing.chipGap,
                      ),
                      child: PdlStat(
                        value: ad.locationDescription!,
                        icon: PdlIcons.place,
                      ),
                    ),
                  // Aucune carte quand la géométrie manque : le lieu écrit se
                  // suffit, et une carte centrée sur un repli mentirait.
                  if (ad.locationGeometry != null)
                    AdLocationMap(geometry: ad.locationGeometry!),
                ],
                const SizedBox(height: PdlSpacing.section),
                PdlSectionHeader(title: 'ads.detail.seller'.tr()),
                PdlPersonRow(name: ad.createdByDisplayName),
                if (_messageSent) ...<Widget>[
                  const SizedBox(height: PdlSpacing.cardTight),
                  // `PdlBannerTone` n'a pas de teinte « succès » et n'en
                  // gagne pas une pour l'occasion : `info` porte la même
                  // paire primaire, et l'icône dit le reste.
                  PdlBanner(
                    tone: PdlBannerTone.info,
                    icon: PdlIcons.checkCircle,
                    message: 'ads.contact.sent'.tr(),
                  ),
                ] else if (_optedOut) ...<Widget>[
                  const SizedBox(height: PdlSpacing.cardTight),
                  PdlBanner(
                    tone: PdlBannerTone.warn,
                    message: 'ads.contact.optedOut'.tr(),
                  ),
                ] else if (currentUserId != ad.createdById) ...<Widget>[
                  // Le bouton **n'existe pas** sur sa propre annonce : se
                  // relayer un message à soi-même n'est pas une action.
                  const SizedBox(height: PdlSpacing.cardTight),
                  PdlButton(
                    label: 'ads.contact.cta'.tr(),
                    icon: PdlIcons.email,
                    fullWidth: true,
                    onPressed: _contact,
                  ),
                ],
                const SizedBox(height: 32),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// La galerie : balayage horizontal sur `images[]`, points de position, et
/// l'appui ouvre le plein écran zoomable.
///
/// L'app bar est posée **par-dessus** en variante `overlay` : la première
/// image d'une annonce est ce qui la fait ouvrir, elle a droit à toute la
/// largeur.
class _Gallery extends ConsumerStatefulWidget {
  const _Gallery({required this.ad});

  final AdDto ad;

  @override
  ConsumerState<_Gallery> createState() => _GalleryState();
}

class _GalleryState extends ConsumerState<_Gallery> {
  final PageController _controller = PageController();
  int _index = 0;

  List<String> get _images => widget.ad.images;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _openFullScreen(int index) {
    final String? token = ref.read(accessTokenHolderProvider);
    final ImageProvider? provider = AuthenticatedDecorationImage.fromUrl(
      _images[index],
      token,
      // La plus grande variante, pas la vignette de la galerie étirée sur
      // toute la dalle.
      size: 1920,
    );
    if (provider == null) return;
    PdlImageViewer.show(
      context,
      imageProvider: provider,
      closeSemanticLabel: 'links.imageClose'.tr(),
    );
  }

  @override
  Widget build(BuildContext context) {
    final Widget media = _images.isEmpty
        ? PdlCardMedia(
            tone: PdlMediaTone.ad,
            icon: PdlIcons.ad,
            height: _kGalleryHeight,
          )
        : PageView.builder(
            controller: _controller,
            itemCount: _images.length,
            onPageChanged: (int index) => setState(() => _index = index),
            itemBuilder: (BuildContext context, int index) => GestureDetector(
              onTap: () => _openFullScreen(index),
              child: Semantics(
                button: true,
                label: 'ads.detail.openImage'.tr(
                  namedArgs: <String, String>{
                    'index': '${index + 1}',
                    'count': '${_images.length}',
                  },
                ),
                child: AuthenticatedImage(
                  imageUrl: _images[index],
                  size: 1080,
                  width: double.infinity,
                  height: _kGalleryHeight,
                  fit: BoxFit.cover,
                  placeholder: const PdlSkeleton(
                    width: double.infinity,
                    height: _kGalleryHeight,
                  ),
                ),
              ),
            ),
          );

    return SizedBox(
      height: _kGalleryHeight,
      child: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          media,
          // Le voile n'est pas décoratif : sans lui, la flèche de retour
          // blanche disparaît sur une photo claire.
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
                onBack: () => Navigator.of(context).maybePop(),
                backSemanticLabel: 'ads.title'.tr(),
              ),
            ),
          ),
          if (_images.length > 1)
            Positioned(
              bottom: 12,
              left: 0,
              right: 0,
              child: Center(
                child: PdlGalleryDots(
                  count: _images.length,
                  index: _index,
                  semanticLabel: 'ads.detail.gallery'.tr(),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
