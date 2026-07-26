# `core/pdl` — la bibliothèque de composants Pédalons

Point d'entrée unique : `import 'package:pedalons/core/pdl/pdl.dart';`.

Cette bibliothèque n'est pas un dossier de widgets partagés de plus. Elle a un
contrat propre, et ce contrat est vérifiable en revue. Un widget qui ne peut pas
le tenir ne vit pas ici : il vit dans son *feature*, sans préfixe `Pdl`.

## Le contrat

### 1. Jetons obligatoires — aucun littéral de couleur

Toute couleur vient de `context.pdl` (`PdlColors`), toute mesure de
`PdlSpacing` / `PdlRadii` / `PdlMetrics`, tout style de texte de
`context.pdlText` (`PdlTypography`), toute durée de `PdlMotion`, toute icône de
`PdlIcons`.

Aucun `Color(0xFF…)`, aucun `Colors.red`, aucun `TextStyle(fontSize: 15)` posé à
la main. La seule exception tolérée est `Colors.transparent` — et encore, on lui
préfère `MaterialType.transparency` ou une couleur nulle, ce que fait le code
actuel.

Corollaire : **les deux modes marchent par construction**. Un composant qui lit
ses jetons n'a rien de spécial à faire pour le mode sombre. Les rares endroits
où le mode change la *forme* et pas seulement la teinte — l'ombre de la pastille
du segmenté, qui devient une bordure — passent par `PdlShadows.*(brightness)` et
sont commentés sur place.

### 2. Cible tactile 44 px

Aucune action ne descend sous `PdlMetrics.tapTarget`. Cela vaut pour les tailles
compactes : `PdlButton(size: sm)` compacte la typographie et les gouttières,
**jamais la cible**. Quand la charte impose un visuel plus petit que 44 — la
pastille de 34 px de `PdlChip`, le rail de 28 px de `PdlSwitch` — le visuel est
centré dans une boîte de 44.

Un test de `test/core/pdl/` mesure `PdlChip`, `PdlButton(size: sm)`,
`PdlSettingRow` et `PdlSegmented`. Ajoutez-y tout nouveau composant actionnable.

### 3. Aucune importation de `api/generated`

`core/pdl` ne connaît aucun DTO. Un composant qui a besoin d'une teinte métier
prend un `PdlTone` en paramètre ; c'est l'appelant qui traduit son enum via
`tone(PdlColors)` (`core/theme/enum_colors.dart`). Un composant qui a besoin
d'un libellé le prend en `String` : **`core/pdl` ne traduit rien**, les clés de
localisation appartiennent aux écrans.

> `core/theme/enum_colors.dart` importe bien `api/generated`, mais il vit dans
> `core/theme`. La frontière passe par le répertoire, et elle tient.

### Les deux `grep` de revue

```bash
grep -rn --include='*.dart' "api/generated" mobile/lib/core/pdl   # doit rester vide
grep -rn --include='*.dart' '\bIcons\.'     mobile/lib/core/pdl   # doit rester vide
```

Deux précisions sur la forme de ces commandes, qui ne changent rien à la règle :

* `--include='*.dart'` écarte ce fichier-ci, qui cite forcément les deux motifs
  qu'il interdit ;
* `\bIcons\.` et non `Icons\.` : sans la limite de mot, le motif attrape
  `PdlIcons.` — c'est-à-dire exactement l'usage conforme.

Le second `grep` impose le passage par `PdlIcons`, seul fichier de l'app
autorisé à nommer `Icons.*` — basculer un jour de Material outline vers Tabler
ne doit toucher qu'un fichier.

## Noms

Les noms sont tranchés (plan v2 §1.0.3-3) et **les alias sont interdits** :

| Retenu | Interdits |
|---|---|
| `PdlButton(variant:)` | `PdlTextButton`, `PdlTextAction`, `PdlLoadingButton` |
| `PdlProgressBar` | `PdlSeatsBar` |
| `PdlColorTrack` | `PdlGroupTrack` |
| `PdlThumb` | `PdlThumbnail` |
| `PdlCardMedia` | `PdlMediaHeader` |
| `PdlPinnedToolbar` | `PdlStickyToolbar` |
| `PdlSettingRow` | `PdlNavRow` |
| `PdlBottomTabs` | `PdlTabBar` |

## Vague A — les 20 primitives livrées

| # | Widget | Fichier |
|---|---|---|
| A1 | `PdlSkeleton` | `pdl_skeleton.dart` |
| A2 | `PdlBadge` | `pdl_badge.dart` |
| A3 | `PdlButton` | `pdl_button.dart` |
| A4 | `PdlAvatar` | `pdl_avatar.dart` |
| A5 | `PdlCard` | `pdl_card.dart` |
| A6 | `PdlStat` | `pdl_stat.dart` |
| A7 | `PdlStatRow` | `pdl_stat_row.dart` |
| A8 | `PdlProgressBar` | `pdl_progress_bar.dart` |
| A9 | `PdlColorTrack` | `pdl_color_track.dart` |
| A10 | `PdlNumberPill` | `pdl_number_pill.dart` |
| A11 | `PdlChip` | `pdl_chip.dart` |
| A12 | `PdlSearchField` | `pdl_search_field.dart` |
| A13 | `PdlSectionHeader` | `pdl_section_header.dart` |
| A14 | `PdlSegmented` | `pdl_segmented.dart` |
| A15 | `PdlSwitch` | `pdl_switch.dart` |
| A16 | `PdlInfoLine` | `pdl_info_line.dart` |
| A17 | `PdlSettingRow` | `pdl_setting_row.dart` |
| A18 | `PdlScrim` | `pdl_scrim.dart` |
| A19 | `PdlBlurSurface` | `pdl_blur_surface.dart` |
| A20 | `PdlFilterButton` | `pdl_filter_button.dart` |

Un fichier par widget, une entrée par fichier dans `pdl.dart`.

## Voir le rendu

`lib/dev/pdl_gallery_page.dart` rend les 20 primitives dans leurs variantes,
avec une bascule clair / sombre. C'est une page de **debug**, volontairement
absente du `GoRouter` ; son en-tête explique comment l'ouvrir.
