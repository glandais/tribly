import 'package:flutter/material.dart';
import 'package:markdown_widget/markdown_widget.dart';

import '../../api/generated/models/asset_dto.dart';
import 'authenticated_image.dart';

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
  return data.replaceAllMapped(
    RegExp(r'::asset\{([^}]+)\}'),
    (match) {
      final attrs = _parseAttrs(match.group(1) ?? '');
      final id = attrs['id'];
      if (id == null || id.isEmpty) return '';

      final sizeWidth =
          _imageSizeWidths[attrs['size'] ?? 'medium'] ?? _defaultSizeWidth;
      final alt = attrs['alt'] ?? '';

      final asset = _findAsset(images, id);
      if (asset == null) return alt;

      final url =
          asset.imageUrl?.replaceAll('{size}', sizeWidth.toString()) ??
              asset.url;
      return '![$alt]($url)';
    },
  );
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

/// A widget that renders markdown content styled to match the app theme.
///
/// Uses [MarkdownBlock] with colors derived from the current [Theme]. It always
/// sizes itself to its content and never scrolls: the parent owns the scroll
/// view. That keeps the page's own scrollable attached to the route's
/// [PrimaryScrollController], which iOS needs for the status bar tap-to-top
/// gesture (handled by [Scaffold.handleStatusBarTap]).
///
/// Pass [images] to support `::asset{id="..." size="..." alt="..."}` directives
/// and authenticated image rendering (via [AuthenticatedImage]).
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
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    final processedData = images.isEmpty
        ? data
        : _resolveAssetDirectives(data, images);

    final config = isDark
        ? MarkdownConfig.darkConfig
        : MarkdownConfig.defaultConfig;

    final styledConfig = config.copy(configs: [
      PConfig(textStyle: textTheme.bodyMedium!),
      H1Config(
        style: textTheme.headlineSmall!.copyWith(
          color: colorScheme.onSurface,
        ),
      ),
      H2Config(
        style: textTheme.titleLarge!.copyWith(
          color: colorScheme.onSurface,
        ),
      ),
      H3Config(
        style: textTheme.titleMedium!.copyWith(
          color: colorScheme.onSurface,
        ),
      ),
      LinkConfig(
        style: textTheme.bodyMedium!.copyWith(
          color: colorScheme.primary,
          decoration: TextDecoration.underline,
          decorationColor: colorScheme.primary,
        ),
      ),
      CodeConfig(
        style: textTheme.bodySmall!.copyWith(
          fontFamily: 'monospace',
          backgroundColor: colorScheme.surfaceContainerHighest,
          color: colorScheme.onSurfaceVariant,
        ),
      ),
      BlockquoteConfig(
        sideColor: colorScheme.primary,
        textColor: colorScheme.onSurfaceVariant,
      ),
      HrConfig(color: colorScheme.outlineVariant),
      ImgConfig(
        builder: (url, attributes) => Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: AuthenticatedImage(
            imageUrl: url,
            fit: BoxFit.contain,
            width: double.maxFinite,
          ),
        ),
      ),
    ]);

    return MarkdownBlock(
      data: processedData,
      config: styledConfig,
    );
  }
}
