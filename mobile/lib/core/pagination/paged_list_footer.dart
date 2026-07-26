import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../pdl/pdl_paged_list_footer.dart';
import 'paged_list_state.dart';

/// Foot of an infinitely scrolled list.
///
/// Façade **traduite** au-dessus de [PdlPagedListFooter] (B9) : le rendu, lui,
/// vit dans `core/pdl`, qui ne traduit rien. Ce qui reste ici est la lecture du
/// [PagedListState] et les clés de localisation.
///
/// Le compteur porte **toujours le total** (« 8 participants sur 40 ») : un
/// « Chargement… » nu ne dit ni où on en est, ni combien il reste. Le total
/// vient de [PagedListState.total], renvoyé par le serveur avec la première
/// page ; tant qu'il manque, seul le nombre chargé est affiché.
class PagedListFooter extends StatelessWidget {
  final PagedListState<Object?> state;
  final VoidCallback onRetry;

  /// Optional placeholder shown above the spinner while the next page loads,
  /// so the list grows into the incoming content instead of jumping.
  final Widget? skeleton;

  /// Nom de ce qui est compté, déjà accordé au pluriel par l'appelant —
  /// « participants », « parcours ». Absent, le compteur reste « 8 sur 40 ».
  final String? itemNoun;

  const PagedListFooter({
    super.key,
    required this.state,
    required this.onRetry,
    this.skeleton,
    this.itemNoun,
  });

  String _progressLabel() {
    final int loaded = state.items.length;
    final int? total = state.total;
    if (total == null) {
      return 'pagination.loaded'.tr(
        namedArgs: <String, String>{'loaded': '$loaded'},
      );
    }
    if (itemNoun == null) {
      return 'pagination.progress'.tr(
        namedArgs: <String, String>{'loaded': '$loaded', 'total': '$total'},
      );
    }
    return 'pagination.progressNamed'.tr(
      namedArgs: <String, String>{
        'loaded': '$loaded',
        'noun': itemNoun!,
        'total': '$total',
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return PdlPagedListFooter(
      isLoadingNext: state.isLoadingNext,
      hasMore: state.hasMore,
      isEmpty: state.items.isEmpty,
      hasError: state.nextError != null,
      onRetry: onRetry,
      skeleton: skeleton,
      progressLabel: _progressLabel(),
      loadingLabel: 'pagination.loadingMore'.tr(),
      endLabel: 'pagination.endOfList'.tr(),
      errorLabel: 'pagination.nextPageError'.tr(),
      retryLabel: 'common.retry'.tr(),
    );
  }
}
