import 'package:flutter/material.dart';

import '../../config/app_config.dart';

/// Resolves a URL to an absolute URL if it's relative.
String _resolveUrl(String url) {
  if (url.startsWith('/')) {
    return '${AppConfig.apiBaseUrl}$url';
  }
  return url;
}

/// Resolves an image URL, replacing {size} placeholder with pixel size.
String _resolveImageUrl(String url, {int size = 400}) {
  final resolvedSize = url.replaceAll('{size}', size.toString());
  return _resolveUrl(resolvedSize);
}

/// A CircleAvatar that safely handles network images with error fallbacks.
///
/// When the image fails to load, it displays the [fallbackText] (typically
/// the first letter of a name) instead of breaking.
class SafeCircleAvatar extends StatelessWidget {
  /// The URL of the image to display.
  final String? imageUrl;

  /// The text to display when no image is available or the image fails to load.
  /// Typically the first letter of a name.
  final String fallbackText;

  /// The radius of the avatar.
  final double radius;

  /// The font size of the fallback text.
  final double? fontSize;

  /// The background color when showing the fallback text.
  final Color? backgroundColor;

  const SafeCircleAvatar({
    super.key,
    this.imageUrl,
    required this.fallbackText,
    this.radius = 20,
    this.fontSize,
    this.backgroundColor,
  });

  @override
  Widget build(BuildContext context) {
    if (imageUrl == null) {
      return CircleAvatar(
        radius: radius,
        backgroundColor: backgroundColor,
        child: Text(fallbackText, style: TextStyle(fontSize: fontSize)),
      );
    }

    final resolvedUrl = _resolveImageUrl(imageUrl!, size: (radius * 2).toInt());
    return CircleAvatar(
      radius: radius,
      backgroundColor: backgroundColor,
      backgroundImage: NetworkImage(resolvedUrl),
      onBackgroundImageError: (exception, stackTrace) {},
      child: ClipOval(
        child: Image.network(
          resolvedUrl,
          width: radius * 2,
          height: radius * 2,
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) => Center(
            child: Text(fallbackText, style: TextStyle(fontSize: fontSize)),
          ),
        ),
      ),
    );
  }
}

/// A DecorationImage that safely handles network images with a placeholder.
class SafeDecorationImage {
  /// Creates a DecorationImage from a network URL with error handling.
  ///
  /// If the image fails to load, the DecorationImage will show nothing,
  /// allowing any underlying widget to be visible.
  static DecorationImage? fromUrl(String? url, {BoxFit fit = BoxFit.cover}) {
    if (url == null) return null;
    return DecorationImage(
      image: NetworkImage(_resolveImageUrl(url)),
      fit: fit,
      onError: (exception, stackTrace) {},
    );
  }
}
