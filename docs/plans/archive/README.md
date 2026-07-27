# Plans archivés

Ces plans ont été exécutés. Ils sont conservés parce qu'ils portent le **pourquoi** de décisions
qui contraignent encore le code — pas comme feuille de route. Ce qui reste à faire a été extrait
dans [`docs/NEXT.md`](../../NEXT.md) : c'est là qu'il faut chercher la suite, pas ici.

Ne pas rouvrir un arbitrage listé ici sans lire sa justification. Plusieurs sont des invariants que
des tests gardent (`groupLeader_isNotTheRideCreator`, `…QueryCountTest`, les deux `grep` de revue de
`core/pdl`).

| Plan | Objet | État vérifié le 27 juillet 2026 |
|---|---|---|
| [`2026-07-26-mobile-v2-implementation.md`](2026-07-26-mobile-v2-implementation.md) | Refonte de l'app Flutter : thème, bibliothèque `core/pdl`, coquille à 5 onglets, 12 écrans | **Terminé** — 116 tâches ☑, aucune ☐. `flutter analyze` propre, 480 tests verts, les 5 invariants de revue tiennent (§ci-dessous) |
| [`2026-07-26-web-portage-mobile-v2.md`](2026-07-26-web-portage-mobile-v2.md) | Portage vers React des seules idées de la v2 mobile qui corrigent une faiblesse du site | **Terminé sauf 3 tâches** — T3.5 abandonnée (prémisse fausse, argumentée sur place), T5.4 partielle (trombinoscope public bloqué par une décision de sécurité), T5.5 optionnelle jamais ouverte. Les trois sont reportées dans `NEXT.md` |
| [`2026-07-26-api-v2-livraison-et-suites.md`](2026-07-26-api-v2-livraison-et-suites.md) | Ce que les contrats 1.3.0 → 1.5.0 ont apporté, et les 4 chantiers d'infrastructure non livrés | **Document de référence** — la partie « livré » fait toujours foi ; son §4 (push, curseur, cache/images, carte multi-entités) est repris dans `NEXT.md`. **Stale sur un point** : le contrat est en **1.6.0** (1.5.1 = mode compact qui rogne les assets ; 1.6.0 = `teamSlug` sur `GET /api/users/search`, pour le sélecteur de meneur), le document s'arrête à 1.5.0 |

## Les invariants vérifiés du plan mobile

Ce sont les `grep` que le plan lui-même impose en revue. Ils passaient tous le 27 juillet 2026 ;
les relancer avant de conclure qu'un écran a le droit de faire autrement.

```bash
cd mobile
grep -rn --include='*.dart' "api/generated" lib/core/pdl   # vide — la bibliothèque ignore les DTO
grep -rn --include='*.dart' '\bIcons\.'     lib/core/pdl   # vide — tout passe par PdlIcons
grep -rln "showModalBottomSheet" lib                       # seul pdl_sheet.dart (le reste = commentaires)
grep -rn "TODO\|FIXME" --include='*.dart' lib | grep -v generated   # vide
grep -rnE '0xFF[0-9A-Fa-f]{6}' lib/features                # vide — aucun littéral de couleur
grep -rn "TeamShell" lib                                   # seuls des commentaires historiques
```

## Ce qui n'est *pas* archivé

[`../2026-02-14-project-audit.md`](../2026-02-14-project-audit.md) reste dans `docs/plans/` : son
plan d'action a encore des lignes ouvertes (backups PostgreSQL/MinIO, rate limiting sur
`/api/device/oauth/complete`, pipeline CD, `maximum-scale=1.0` du viewport). Il n'a pas été relu
dans cette passe — son dernier contrôle datait du 1er avril 2026.

[`../../audit-ux/`](../../audit-ux/) reste également en place : c'est l'**entrant** de design (brief,
analyse page par page, charte, 65 captures) et il documente l'état d'avant la v2. Il ne décrit pas
le code actuel.
