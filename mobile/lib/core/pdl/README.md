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

## Vague B — les 26 composés livrés

| # | Widget | Fichier |
|---|---|---|
| B1 | `PdlChipRow` | `pdl_chip_row.dart` |
| B2 | `PdlAvatarStack` | `pdl_avatar_stack.dart` |
| B3 | `PdlTeamLine` | `pdl_team_line.dart` |
| B4 | `PdlPlaceRow` | `pdl_place_row.dart` |
| B5 | `PdlThumb` | `pdl_thumb.dart` |
| B6 | `PdlCardMedia` | `pdl_card_media.dart` |
| B7 | `PdlEmptyState` | `pdl_empty_state.dart` |
| B8 | `PdlBanner` | `pdl_banner.dart` |
| B9 | `PdlPagedListFooter` | `pdl_paged_list_footer.dart` |
| B10 | `PdlPersonRow` | `pdl_person_row.dart` |
| B11 | `PdlAttachmentRow` | `pdl_attachment_row.dart` |
| B12 | `PdlLegendRow` | `pdl_legend_row.dart` |
| B13 | `PdlClimbRow` | `pdl_climb_row.dart` |
| B14 | `PdlStatCellRow` | `pdl_stat_cell_row.dart` |
| B15 | `PdlAvatarEditor` | `pdl_avatar_editor.dart` |
| B16 | `PdlDangerZone` | `pdl_danger_zone.dart` |
| B17 | `PdlRangeFilter` | `pdl_range_filter.dart` |
| B18 | `PdlGalleryDots` | `pdl_gallery_dots.dart` |
| B19 | `PdlPriceBlock` | `pdl_price_block.dart` |
| B20 | `PdlBadgeStack` | `pdl_badge_stack.dart` |
| B21 | `PdlSkeletonCard` | `pdl_skeleton_card.dart` |
| B22 | `PdlDeadEndEmpty` | `pdl_dead_end_empty.dart` |
| B23 | `PdlScopeSelector` | `pdl_scope_selector.dart` |
| B24 | `PdlMarkdownBody` | `pdl_markdown_body.dart` |
| B25 | `PdlImageViewer` | `pdl_image_viewer.dart` |
| B26 | `PdlMonthGrid` + `PdlDayHeader` | `pdl_month_grid.dart` |

Quatre d'entre eux **remplacent** un widget existant, qui n'est plus qu'une
façade portant ce que `core/pdl` ne peut pas connaître — un DTO, une route, une
clé de traduction : `core/widgets/team_banner.dart` (B3),
`core/pagination/paged_list_footer.dart` (B9),
`core/widgets/markdown_content.dart` (B24) et `RoutesEmptyState` de
`features/routes/.../routes_page.dart` (B22). `PdlEmptyState` et
`PdlDeadEndEmpty` composent, eux, `core/animations/animated_empty_state.dart`.

Trois écarts assumés à la charte, documentés sur place :

* **`PdlTeamLine` mesure 24 px** et non 44 (`.teamline { min-height: 24px }`) :
  c'est un lien inline dans une carte déjà tappable en entier. `minHeight` le
  remonte là où il est seul.
* **`PdlBanner(warn)` emploie la paire `warning`** et non l'orange `#fff4e6` de
  la planche : seule la paire sémantique est définie dans les deux modes.
* **`PdlAvatarEditor` ne sélectionne pas l'image** : `image_picker` demande une
  permission et produit des erreurs à traduire, deux choses qui appartiennent à
  l'écran. Le widget expose `onPick`.

## Voir le rendu

`lib/dev/pdl_gallery_page.dart` rend les 20 primitives et les 26 composés dans
leurs variantes, avec une bascule clair / sombre. C'est une page de **debug**,
volontairement absente du `GoRouter` ; son en-tête explique comment l'ouvrir.
