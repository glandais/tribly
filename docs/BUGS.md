# Site web

## Tous

[x] Dans le burger, il faut deviner qu'il faut cliquer sur sa pastille/libellé pour ouvrir son profil — `WEB-46`
[x] Sur l'accueil, ma prochaine sortie : le nom n'est pas cliquable — `WEB-47`
[x] Cette semaine : la semaine commence le dimanche, même en français — `WEB-48`
[x] Sur la carte d'une sortie — `WEB-49`
    - on ne sait pas dans quel sens est chaque parcours
    - les boutons de carte sont cachés par le profil
    - en mode plein écran, les pastilles des participants sont au dessus de la carte
    - dans la liste des calques, on ne peut pas choisir les traces à afficher
[x] Quand on est connecté, ne pas afficher Fonctionnalités en haut — `WEB-50`
[x] Quand on est connecté, sur l'accueil, envoyez vos parcours vers votre compteur — `WEB-51`
    - ne s'affiche que si l'utilisateur n'a ni compteur ni service connecté
    - renvoie à la connexion de GPS sur son profil et non sur fonctionnalités

## Mobile

Recette `MOB-1` à `MOB-20` du 4 octobre 2026, sur prod, équipe `gaby-test1` (jeu de données « Recette — … »).

[x] Toucher l'équipe depuis le détail d'un parcours, d'une sortie… ramène parfois sur une page vide — `MOB-44`
    - toutes ces entrées font `context.push(Paths.team(slug))` (`ride_detail_page.dart`, `trip_detail_page.dart`,
      `post_detail_page.dart`, `route_card.dart`, `team_banner.dart`…) ; la page d'équipe est une `NoTransitionPage`
      de la branche Équipes du `StatefulShellRoute` (`_teamTree`, `config/router.dart`)
    - cause : depuis une page plein écran (navigateur racine), un `push` vers **n'importe quelle** page d'onglet
      recrée la branche avec des clés de page déjà prises — assertion en debug, écran vide en release. Corrigé par
      `pushLocation` (`core/utils/push_location.dart`)
[x] « Cols et montées (1) » : le titre reste au pluriel avec une seule montée — `MOB-45`
    - `routes.climbs` est devenu un pluriel lu par `.plural()` : « Col ou montée (1) », « Climb (1) » en anglais
    - le web n'affiche pas de compte (`routes.detail.climbs.title`), pas de défaut côté site
[x] Lien relatif d'une publication (`/equipes/gaby-test1/annonces`, lien 6 de « Recette — Liens et tableau ») :
    page vide, sans retour — `MOB-46`
    - même cause que `MOB-44` : `openLink` (`core/utils/link_launcher.dart`) poussait la section depuis la
      publication plein écran ; les liens vers une sortie ou un parcours (pages plein écran) n'étaient pas touchés
