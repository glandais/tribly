import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:markdown_widget/markdown_widget.dart';

enum LegalPageType { privacy, terms }

class LegalPage extends StatefulWidget {
  final LegalPageType type;

  const LegalPage({super.key, required this.type});

  @override
  State<LegalPage> createState() => _LegalPageState();
}

class _LegalPageState extends State<LegalPage> {
  String? _markdown;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _loadMarkdown();
  }

  Future<void> _loadMarkdown() async {
    final lang = context.locale.languageCode == 'fr' ? 'fr' : 'en';
    final fileName = widget.type == LegalPageType.privacy
        ? 'privacy-policy'
        : 'terms-of-service';
    final content = await rootBundle.loadString('privacy/$fileName.$lang.md');
    if (mounted) {
      setState(() => _markdown = content);
    }
  }

  @override
  Widget build(BuildContext context) {
    final title = widget.type == LegalPageType.privacy
        ? 'profile.privacy'.tr()
        : 'profile.terms'.tr();

    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: _markdown == null
          ? const Center(child: CircularProgressIndicator())
          : MarkdownWidget(data: _markdown!),
    );
  }
}
