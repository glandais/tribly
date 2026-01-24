import 'package:flutter/material.dart';

/// A card widget with tap feedback animation.
///
/// Provides a subtle scale-down effect (0.98x) when pressed,
/// making the app feel more responsive.
class AnimatedCard extends StatefulWidget {
  /// The child widget to wrap.
  final Widget child;

  /// Called when the card is tapped.
  final VoidCallback? onTap;

  /// Called when the card is long-pressed.
  final VoidCallback? onLongPress;

  /// The border radius for the ink ripple effect.
  final BorderRadius borderRadius;

  /// The duration of the scale animation.
  final Duration duration;

  /// The scale factor when pressed (default: 0.98).
  final double pressedScale;

  const AnimatedCard({
    super.key,
    required this.child,
    this.onTap,
    this.onLongPress,
    this.borderRadius = const BorderRadius.all(Radius.circular(12)),
    this.duration = const Duration(milliseconds: 100),
    this.pressedScale = 0.98,
  });

  @override
  State<AnimatedCard> createState() => _AnimatedCardState();
}

class _AnimatedCardState extends State<AnimatedCard> {
  bool _isPressed = false;

  void _handleTapDown(TapDownDetails details) {
    setState(() => _isPressed = true);
  }

  void _handleTapUp(TapUpDetails details) {
    setState(() => _isPressed = false);
  }

  void _handleTapCancel() {
    setState(() => _isPressed = false);
  }

  @override
  Widget build(BuildContext context) {
    // Respect user's motion preferences
    final reduceMotion = MediaQuery.of(context).disableAnimations;
    final scale = _isPressed && !reduceMotion ? widget.pressedScale : 1.0;
    final duration = reduceMotion ? Duration.zero : widget.duration;

    return GestureDetector(
      onTapDown: _handleTapDown,
      onTapUp: _handleTapUp,
      onTapCancel: _handleTapCancel,
      child: AnimatedScale(
        scale: scale,
        duration: duration,
        curve: Curves.easeInOut,
        child: Card(
          child: InkWell(
            onTap: widget.onTap,
            onLongPress: widget.onLongPress,
            borderRadius: widget.borderRadius,
            child: widget.child,
          ),
        ),
      ),
    );
  }
}
