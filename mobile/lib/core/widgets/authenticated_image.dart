import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../api/tribly_api_client.dart';

/// A general-purpose image widget that includes the Authorization header.
///
/// Uses [CachedNetworkImage] for caching and the [accessTokenHolderProvider]
/// to add the Bearer token to requests.
class AuthenticatedImage extends ConsumerWidget {
  /// The URL of the image to display.
  final String imageUrl;

  /// How to inscribe the image into the space allocated.
  final BoxFit? fit;

  /// The target width of the image.
  final double? width;

  /// The target height of the image.
  final double? height;

  /// Widget to display while the image is loading.
  final Widget? placeholder;

  /// Widget to display when the image fails to load.
  final Widget? errorWidget;

  const AuthenticatedImage({
    super.key,
    required this.imageUrl,
    this.fit,
    this.width,
    this.height,
    this.placeholder,
    this.errorWidget,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final token = ref.watch(accessTokenHolderProvider);

    return CachedNetworkImage(
      imageUrl: imageUrl,
      httpHeaders: token != null ? {'Authorization': 'Bearer $token'} : null,
      fit: fit,
      width: width,
      height: height,
      placeholder: placeholder != null
          ? (context, url) => placeholder!
          : (context, url) => const Center(
                child: CircularProgressIndicator(strokeWidth: 2),
              ),
      errorWidget: errorWidget != null
          ? (context, url, error) => errorWidget!
          : (context, url, error) => const Icon(Icons.broken_image_outlined),
    );
  }
}

/// A CircleAvatar that includes the Authorization header and falls back
/// to initials when the image fails to load.
///
/// Similar to [SafeCircleAvatar] but with authenticated image loading.
class AuthenticatedCircleAvatar extends ConsumerWidget {
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

  const AuthenticatedCircleAvatar({
    super.key,
    this.imageUrl,
    required this.fallbackText,
    this.radius = 20,
    this.fontSize,
    this.backgroundColor,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (imageUrl == null) {
      return _buildFallbackAvatar(context);
    }

    final token = ref.watch(accessTokenHolderProvider);

    return CachedNetworkImage(
      imageUrl: imageUrl!,
      httpHeaders: token != null ? {'Authorization': 'Bearer $token'} : null,
      imageBuilder: (context, imageProvider) => CircleAvatar(
        radius: radius,
        backgroundColor: backgroundColor,
        backgroundImage: imageProvider,
      ),
      placeholder: (context, url) => _buildFallbackAvatar(context),
      errorWidget: (context, url, error) => _buildFallbackAvatar(context),
    );
  }

  Widget _buildFallbackAvatar(BuildContext context) {
    return CircleAvatar(
      radius: radius,
      backgroundColor:
          backgroundColor ?? Theme.of(context).colorScheme.primaryContainer,
      child: Text(
        fallbackText,
        style: TextStyle(
          fontSize: fontSize ?? radius * 0.8,
          fontWeight: FontWeight.w500,
          color: Theme.of(context).colorScheme.onPrimaryContainer,
        ),
      ),
    );
  }
}

/// A DecorationImage provider that includes the Authorization header.
///
/// Returns a [CachedNetworkImageProvider] configured with the Bearer token.
class AuthenticatedDecorationImage {
  /// Creates a [CachedNetworkImageProvider] from a network URL with auth headers.
  ///
  /// Returns null if [url] is null.
  static ImageProvider? fromUrl(String? url, String? token) {
    if (url == null) return null;
    return CachedNetworkImageProvider(
      url,
      headers: token != null ? {'Authorization': 'Bearer $token'} : null,
    );
  }
}
