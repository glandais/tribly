import 'package:flutter/material.dart';

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
        child: Text(
          fallbackText,
          style: TextStyle(fontSize: fontSize),
        ),
      );
    }

    return CircleAvatar(
      radius: radius,
      backgroundColor: backgroundColor,
      backgroundImage: NetworkImage(imageUrl!),
      onBackgroundImageError: (exception, stackTrace) {},
      child: ClipOval(
        child: Image.network(
          imageUrl!,
          width: radius * 2,
          height: radius * 2,
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) => Center(
            child: Text(
              fallbackText,
              style: TextStyle(fontSize: fontSize),
            ),
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
  static DecorationImage? fromUrl(
    String? url, {
    BoxFit fit = BoxFit.cover,
  }) {
    if (url == null) return null;
    return DecorationImage(
      image: NetworkImage(url),
      fit: fit,
      onError: (exception, stackTrace) {},
    );
  }
}
