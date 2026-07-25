import 'package:flutter/material.dart';

import 'breakpoints.dart';

/// Centers and constrains content width on larger screens for better readability.
///
/// On compact screens, content fills the full width.
/// On medium screens, content is constrained to 720px.
/// On expanded screens, content is constrained to 960px.
class ContentWidthConstraint extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;

  const ContentWidthConstraint({super.key, required this.child, this.padding});

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);
    final maxWidth = Breakpoints.contentMaxWidth(sizeClass);

    Widget content = child;

    if (maxWidth != null) {
      content = Center(
        child: ConstrainedBox(
          constraints: BoxConstraints(maxWidth: maxWidth),
          child: child,
        ),
      );
    }

    if (padding != null) {
      content = Padding(padding: padding!, child: content);
    }

    return content;
  }
}

/// A sliver version of ContentWidthConstraint for use in CustomScrollView.
class SliverContentWidthConstraint extends StatelessWidget {
  final Widget sliver;

  const SliverContentWidthConstraint({super.key, required this.sliver});

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);
    final maxWidth = Breakpoints.contentMaxWidth(sizeClass);

    if (maxWidth == null) {
      return sliver;
    }

    return SliverToBoxAdapter(
      child: Center(
        child: ConstrainedBox(
          constraints: BoxConstraints(maxWidth: maxWidth),
          child: _SliverToBox(sliver: sliver),
        ),
      ),
    );
  }
}

/// Helper widget to convert sliver back to box for constraint.
/// This is a simplified approach - for complex slivers, consider using
/// SliverPadding with calculated horizontal padding instead.
class _SliverToBox extends StatelessWidget {
  final Widget sliver;

  const _SliverToBox({required this.sliver});

  @override
  Widget build(BuildContext context) {
    // This approach works for SliverToBoxAdapter-wrapped content
    // For complex sliver trees, you may need a different approach
    return sliver;
  }
}
