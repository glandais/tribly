import 'package:flutter/material.dart';

import '../../api/generated/models/asset_dto.dart';
import '../pdl/pdl_markdown_body.dart';

/// Image size widths in pixels — mirrors frontend assetMarkdown.ts.
const _imageSizeWidths = {
  'icon': 32,
  'thumbnail': 96,
  'medium': 448,
  'full': 1920,
};

const _defaultSizeWidth = 448; // medium

/// Pre-processes markdown by resolving `::asset{id="..." size="..." alt="..."}`
/// directives into standard `![alt](url)` markdown image syntax.
String _resolveAssetDirectives(String data, List<AssetDto> images) {
  return data.replaceAllMapped(RegExp(r'::asset\{([^}]+)\}'), (match) {
    final attrs = _parseAttrs(match.group(1) ?? '');
    final id = attrs['id'];
    if (id == null || id.isEmpty) return '';

    final sizeWidth =
        _imageSizeWidths[attrs['size'] ?? 'medium'] ?? _defaultSizeWidth;
    final alt = attrs['alt'] ?? '';

    final asset = _findAsset(images, id);
    if (asset == null) return alt;

    final url =
        asset.imageUrl?.replaceAll('{size}', sizeWidth.toString()) ?? asset.url;
    return '![$alt]($url)';
  });
}

Map<String, String> _parseAttrs(String input) {
  final result = <String, String>{};
  for (final m in RegExp(r'(\w+)="([^"]*)"').allMatches(input)) {
    result[m.group(1)!] = m.group(2)!;
  }
  return result;
}

AssetDto? _findAsset(List<AssetDto> images, String id) {
  for (final asset in images) {
    if (asset.id == id) return asset;
  }
  return null;
}

/// Rend un contenu markdown avec la feuille de style unique de la charte.
///
/// Ce n'est plus qu'une **façade** : le rendu appartient à [PdlMarkdownBody]
/// (B24). Ce qui reste ici est ce que `core/pdl` ne peut pas connaître — les
/// directives `::asset{id="..." size="..." alt="..."}`, qui se résolvent
/// contre des [AssetDto].
///
/// Comme [PdlMarkdownBody], il se dimensionne à son contenu et ne défile
/// jamais : le défilement appartient à la page, ce qui garde son
/// `ScrollController` attaché au `PrimaryScrollController` — iOS en a besoin
/// pour le tap sur la barre d'état (`Scaffold.handleStatusBarTap`).
class MarkdownContent extends StatelessWidget {
  final String data;
  final List<AssetDto> images;

  const MarkdownContent({
    super.key,
    required this.data,
    this.images = const [],
  });

  @override
  Widget build(BuildContext context) {
    final processedData = images.isEmpty
        ? data
        : _resolveAssetDirectives(data, images);

    return PdlMarkdownBody(data: processedData);
  }
}
