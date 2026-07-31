import 'dart:async';

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../../api/generated/export.dart';
import '../pdl/pdl.dart';
import '../theme/pdl_icons.dart';
import '../theme/pdl_tokens.dart';
import '../utils/link_launcher.dart';

/// Les fichiers joints à un contenu — facture, notice, fiche technique, road
/// book —, avec de quoi les ouvrir.
///
/// Un seul rendu pour les huit écrans qui portent un `MediaDto` : sortie,
/// publication, annonce, parcours, voyage, étape, page d'équipe et « à propos ».
/// Les trois copies qui existaient divergeaient déjà — celle des sorties
/// listait les fichiers **sans bouton**, donc sans moyen de les ouvrir.
///
/// Le bloc **n'existe pas** quand il n'y a aucune pièce jointe : ni titre, ni
/// espace réservé. Un écran peut donc le poser sans condition — sauf s'il
/// l'enveloppe lui-même dans un `Padding`, dont les marges, elles, se
/// dessineraient autour de rien.
///
/// `attachments` n'arrive qu'en vue complète : une liste chargée en `COMPACT`
/// ne le porte pas, ce qui est voulu — une carte ne montre pas de fichiers.
class MediaAttachments extends StatelessWidget {
  const MediaAttachments({
    super.key,
    required this.attachments,
    this.spacingBefore = PdlSpacing.section,
  });

  final List<AssetDto> attachments;

  /// L'espace posé au-dessus du titre, absent avec le bloc lui-même.
  final double spacingBefore;

  @override
  Widget build(BuildContext context) {
    if (attachments.isEmpty) return const SizedBox.shrink();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisSize: MainAxisSize.min,
      children: <Widget>[
        SizedBox(height: spacingBefore),
        PdlSectionHeader(title: 'media.attachments'.tr()),
        for (int i = 0; i < attachments.length; i++)
          _AttachmentRow(
            asset: attachments[i],
            showDivider: i < attachments.length - 1,
          ),
      ],
    );
  }
}

/// Une ligne de fichier, ouverte par le système.
///
/// `AssetDto` ne porte pas de taille : on affiche l'extension seule plutôt
/// qu'un poids inventé. Une pièce jointe qui est une image a sa vignette, ce
/// qui évite d'ouvrir un fichier pour savoir lequel c'est.
class _AttachmentRow extends StatelessWidget {
  const _AttachmentRow({required this.asset, required this.showDivider});

  final AssetDto asset;
  final bool showDivider;

  @override
  Widget build(BuildContext context) {
    return PdlAttachmentRow(
      name: asset.fileName,
      type: _extension(asset.fileName) ?? asset.contentType,
      thumbnailUrl: asset.imageUrl,
      icon: asset.imageUrl == null ? PdlIcons.attachment : PdlIcons.image,
      onAction: () => unawaited(openLink(context, asset.url)),
      // **Le libellé nomme le fichier** : « Ouvrir » répété douze fois dans un
      // lecteur d'écran ne permet pas de choisir.
      actionSemanticLabel: 'media.openAttachment'.tr(
        namedArgs: <String, String>{'file': asset.fileName},
      ),
      showDivider: showDivider,
    );
  }

  String? _extension(String fileName) {
    final int dot = fileName.lastIndexOf('.');
    if (dot < 0 || dot == fileName.length - 1) return null;
    return fileName.substring(dot + 1);
  }
}
