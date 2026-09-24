# Signalement et blocage — directive App Store 1.2

Pedalons contient du contenu créé par les membres ; Apple exige (1.2) un moyen de **signaler**, un
moyen de **bloquer**, un **filtrage avant publication** et des coordonnées publiées. L'examinateur
se connecte avec `marketplace-tester@pedalons.fr`, simple `MEMBER` : tout doit marcher depuis ce
compte, dans l'app mobile, en deux ou trois taps.

Ce document est la spécification. Les arbitrages sont en fin de document.

## Principes

- **Tout est signalable** : commentaire, post, annonce, sortie, voyage, parcours, membre. Une pièce
  jointe ou un avatar ne sont **pas** des cibles : on signale le contenu qui les porte (motif
  « Image inappropriée ») ou le membre. L'interface ne gagne aucune entrée de plus pour eux.
- **Signaler** exige d'être connecté et de pouvoir lire la cible. Un signalement se fait toujours
  **dans une équipe** (`teamSlug`) : c'est elle qui décide qui modère.
- **Qui modère** : les ORGANIZER et ADMIN de l'équipe (file d'équipe) **et** `PLATFORM_ADMIN` (file
  plateforme, tout le domaine). Un membre visé par un signalement (auteur du contenu ou membre
  signalé) n'est jamais destinataire et ne voit pas ce signalement dans sa file ; s'il est
  ORGANIZER/ADMIN, seuls les **ADMIN** (hors lui) sont prévenus ; s'il n'en reste aucun, seul
  `PLATFORM_ADMIN` l'est.
- **Identité du signaleur** : visible **uniquement** par `PLATFORM_ADMIN`. Jamais dans la file
  d'équipe, jamais dans la notification (acteur nul).
- **Masquage** : le contenu signalé disparaît **aussitôt des listes du signaleur** ; au **3ᵉ
  signaleur distinct** (signalements OPEN), il est masqué pour tous sauf les modérateurs de
  l'équipe et `PLATFORM_ADMIN`, jusqu'à décision. « Rejeter » le rétablit, « Supprimer » l'efface.
- **Blocage** : à l'échelle du domaine, à sens unique, silencieux. Le bloqueur ne voit plus les
  **commentaires, posts et annonces** du bloqué, ni les notifications de commentaire qu'il cause.
  Sorties, voyages, parcours et listes de participants ne changent pas (c'est de l'organisation).
  Le bloqué ne voit rien changer, et aucun endpoint ne lui révèle qu'il est bloqué. On ne bloque
  que là où l'auteur est déjà affiché (commentaire, annonce, ligne de membre/participant) : un post
  n'expose pas son auteur et ne doit pas commencer à le faire.
- **Filtrage à la publication** : une courte liste de termes sans ambiguïté (insultes lourdes,
  termes haineux, fr/en), mot entier après normalisation. Refus `400` avec le code
  `CONTENT_REJECTED`. Les mots ambigus en français (« con », « retard », « crève », « tapette »,
  « bite », « pd », « chienne »…) n'y sont **pas** : le signalement couvre le reste.
- **CGU** : case à cocher obligatoire à l'inscription (web et mobile), `acceptTerms` dans
  `RegisterRequest`, horodatage `users.terms_accepted_at`. Clause de tolérance zéro et section
  modération dans `privacy/terms-of-service.{fr,en}.md`.
- **Notification** : nouveau `NotificationType.CONTENT_REPORTED` par le pipeline existant, push et
  boîte de réception (l'e-mail des notifications reste coupé en prod ; le push suffit).

## Modèle — migration `V41__moderation.sql` (écrite)

- `content_reports` : `domain_id`, `team_id`, `reporter_id` (nullable, `set null` à l'effacement),
  `target_type`, `target_id` (sans FK : un commentaire supprimé est hard-deleted, le signalement
  lui survit), `target_user_id` (auteur ou membre visé), `reason`, `message` (≤ 500), `excerpt`
  (copie du texte signalé, ≤ 1000), `status` (OPEN/REMOVED/DISMISSED), `resolved_by_id`,
  `resolved_at`, `created_at`. Unique `(reporter_id, target_type, target_id)`.
- `user_blocks` : `blocker_id`, `blocked_id`, `created_at`, unique `(blocker_id, blocked_id)`.
- `team_entities.moderation_hidden_at`, `comments.moderation_hidden_at` : masquage au 3ᵉ signaleur.
- `users.terms_accepted_at`, et `auth_tokens.pending_terms_accepted_at` qui la porte de l'inscription
  à la vérification de l'e-mail (nulle sur un jeton émis avant la case : aucun consentement inventé).

Les tests utilisent `drop-and-create` : les annotations JPA doivent correspondre exactement.

Enums (`fr.pedalons.enums`) : `ReportTargetType {COMMENT, POST, AD, RIDE, TRIP, ROUTE, MEMBER}`,
`ReportReason {SPAM, HARASSMENT, HATE, SEXUAL, VIOLENCE, ILLEGAL, INAPPROPRIATE_IMAGE, OTHER}`,
`ReportStatus {OPEN, REMOVED, DISMISSED}`, `ModerationAction {REMOVE_CONTENT, DISMISS}`.

## API (4.0.0 → 4.1.0)

| Endpoint | Accès | Effet |
|---|---|---|
| `POST /api/reports` `{teamSlug, targetType, targetId, reason, message?}` | connecté, cible lisible | 204. Idempotent (même signaleur + cible → rien de neuf). `REPORT_SELF` si l'on se signale soi-même ou son propre contenu. 404 si la cible n'est pas lisible par l'appelant. |
| `GET /api/teams/{teamSlug}/reports?status=OPEN\|RESOLVED` | ORGANIZER/ADMIN de l'équipe, `PLATFORM_ADMIN` | Signalements **groupés par cible**, sans signaleurs. Exclut les cibles visant l'appelant. |
| `POST /api/teams/{teamSlug}/reports/resolve` `{targetType, targetId, action}` | idem | Applique la décision à **tous** les signalements OPEN de la cible. |
| `GET /api/admin/reports?status=` | `@Admin` | Idem, tout le domaine, **avec** signaleurs. |
| `POST /api/admin/reports/resolve` `{targetType, targetId, action}` | `@Admin` | Idem. |
| `GET /api/users/me/blocks` | connecté | `{users: PublicUserDto[]}` |
| `PUT /api/users/me/blocks/{userId}` · `DELETE …` | connecté | 204. `BLOCK_SELF` si soi-même ; 404 si l'utilisateur n'est pas du domaine ou est supprimé. Idempotents. |

`ModerationItemDto` (un par cible) : `targetType`, `targetId`, `teamSlug`, `teamName`,
`targetUser: PublicUserDto`, `contentName` (nom de la publication, ou du parent pour un
commentaire ; nul pour un membre), `contentType` + `contentSlug` (pour ouvrir le contenu : le
parent d'un commentaire, `POST|RIDE|TRIP|ROUTE|AD`), `excerpt`, `reportCount`, `reasons` (distincts),
`messages` (textes libres non vides), `firstReportedAt`, `lastReportedAt`, `hidden`, `status`,
`reporters: PublicUserDto[]` — **nul** dans la file d'équipe, rempli pour `PLATFORM_ADMIN`.
Réponse : `{items, total}` ; OPEN non paginé (une file d'équipe est courte), RESOLVED limité aux 100
dernières cibles résolues.

Décisions :
- `REMOVE_CONTENT` : `TeamEntity.setDeleted(true)` (même geste que les `delete*` des services —
  un organisateur peut donc retirer l'annonce d'un autre **par la modération**, sans toucher
  `AdAccessChecker`) ; commentaire : même suppression que `CommentService.deleteComment`
  (réponses comprises, nettoyage des tombstones). Interdit (`BAD_REQUEST`) sur une cible `MEMBER` :
  un membre se retire depuis la page Membres (ADMIN).
- `DISMISS` : statut DISMISSED et `moderation_hidden_at` remis à nul.
- Statut des signalements de la cible : REMOVED ou DISMISSED, `resolved_by`, `resolved_at`.

## Filtrage des listes (invariant « par page »)

Dans `TeamEntityRepository.getPedalonsQuery`, branche utilisateur connecté :
- **listes seulement** (`list == true`) : `te.id not in (select r.targetId from ContentReport r
  where r.reporter.id = :userId)` et `(TYPE(te) not in (Post, Ad) or te.createdBy.id not in
  (select b.blocked.id from UserBlock b where b.blocker.id = :userId))` — y compris pour
  `PLATFORM_ADMIN` (c'est son choix personnel). Le détail reste accessible par lien.
- **listes et détail** : `te.moderationHiddenAt is null` pour un anonyme ; `(te.moderationHiddenAt
  is null or ut.role in ('ORGANIZER','ADMIN'))` pour un connecté, sauf `PLATFORM_ADMIN`.

Des sous-requêtes dans la même instruction : le nombre de requêtes par page ne change pas.

Commentaires (`CommentService`) : masquage **en mémoire**, deux requêtes par appel quel que soit
le nombre de commentaires — les ids bloqués par le lecteur, et les ids de commentaires qu'il a
signalés parmi ceux chargés. Un commentaire est masqué pour le lecteur si son auteur est bloqué,
s'il l'a signalé, ou s'il est `moderationHidden` et que le lecteur n'est ni modérateur de l'équipe
ni `PLATFORM_ADMIN`. Réponse masquée → retirée. Racine masquée avec des réponses visibles →
**tombstone** (`deleted = true`, contenu vide, rendu existant des clients) ; sans réponse visible →
retirée. Les totaux restent ceux de la base.

Notifications : `COMMENT_REPLY` et `COMMENT_ON_MY_PUBLICATION` ne partent pas vers un destinataire
qui a bloqué l'auteur du commentaire.

Un `CommentQueryCountTest` couvre la liste paginée des commentaires.

## Notification `CONTENT_REPORTED`

- `NotificationType.CONTENT_REPORTED(EnumSet.of(IN_APP, PUSH, EMAIL), PERSONAL, urgent = true)`,
  jamais relayé au webhook d'équipe.
- Événement `ContentReported(long reportId)`, clé `CONTENT_REPORTED:<teamId>:<targetType>:<targetId>`,
  `coalescesWhilePending() = true` : une rafale de signalements sur un même contenu fait un avis.
  L'équipe est dans la clé : un membre signalé dans deux équipes doit prévenir les modérateurs des deux.
- Publié **sans acteur** (le signaleur est anonyme pour l'équipe).
- Résolution : signalement introuvable ou non OPEN → ignoré. Destinataires : modérateurs de
  l'équipe selon la règle ci-dessus, plus les `PLATFORM_ADMIN` du domaine, moins le signaleur et
  la personne visée. Nouveau `NotificationSubjectType.REPORT` ; `subjectSlug` = slug de l'équipe,
  `subjectName` = nom de l'équipe, **pas d'extrait** : le push s'affiche sur l'écran verrouillé.
- Lien : `/teams/{teamSlug}/admin/reports` (la file d'équipe ; un `PLATFORM_ADMIN` y a accès).
- Textes : « Nouveau signalement dans {team} » / « Un contenu de l'équipe {team} a été signalé et
  attend votre décision. »

## Filtre de publication

`@AcceptableText` (Bean Validation) sur : `CommentRequest.content`, `MediaDto.markdown`, `name` de
`PostRequest`, `AdRequest`, `RideRequest`, `TripRequest`, `StageRequest`, `RouteRequest`,
`TeamRequest`, `displayName` de `RegisterRequest` et `UpdateUserRequest`. Le validateur s'appuie sur
`TextFilter`, qui charge `src/main/resources/moderation/blocked-terms.txt` (un terme ou une
expression par ligne, `#` commente). Normalisation identique du texte et des termes : minuscules,
accents retirés, leetspeak simple (`0→o 1→i 3→e 4→a @→a $→s 5→s 7→t`), tout ce qui n'est pas une
lettre → espace ; correspondance sur des suites de mots entiers. `GlobalExceptionMapper` renvoie
`CONTENT_REJECTED` (avec le détail des champs) dès qu'une violation vient de `@AcceptableText`.

## Effacement de compte et export

- `AccountErasureService.erase` : supprime les `user_blocks` où l'utilisateur est bloqueur **ou**
  bloqué ; supprime les `content_reports` qui le **visent** (leur extrait est son contenu) ; met
  `reporter_id` et `resolved_by_id` à nul là où c'est lui.
- `UserExportBuilder` : ses blocages (nom, date) et ses signalements (type de cible, motif,
  message, date, statut).

## Clients

**Web** — menu `⋯` (`IconDots`) visible par tout connecté, distinct du bouton d'édition, sur post,
annonce, sortie, voyage, parcours et chaque commentaire ; « Signaler » (pas sur son propre
contenu) et « Bloquer {nom} » (commentaire, annonce ; pas soi-même). Fenêtre de signalement :
motifs, texte libre, et la phrase « Transmis aux organisateurs de {équipe} et à l'équipe
Pedalons ». Confirmation : « Merci. Ce contenu est masqué pour vous. » Onglet « Signalements »
dans `TeamAdminLayout` (route `teamAdminReports`) et dans `AdminLayout` (route `adminReports`).
Section « Utilisateurs bloqués » dans `UserProfilePage`. Case CGU obligatoire dans le mode
inscription de `LoginPage`. Type `CONTENT_REPORTED` et sujet `REPORT` dans `notificationDisplay`.
Message pour `CONTENT_REJECTED`.

**Mobile** — action `PdlIcons.more` dans la barre des pages détail (sortie, voyage, post, annonce ;
`PdlMapButton` sur le parcours) et un `⋯` par commentaire, qui ouvrent une `PdlSheet` : Signaler,
Bloquer {nom} (quand l'auteur est affiché), Supprimer (commentaire : auteur **ou organisateur**).
Tap sur une ligne du trombinoscope ou de la liste des participants → même sheet (Signaler le
membre, Bloquer). Sheet de signalement : motifs, texte libre, Envoyer. Page « Utilisateurs
bloqués » (route `blockedUsers`, mobile seulement) ouverte depuis le profil. Case CGU obligatoire à
l'inscription. Type et sujet de notification. Message pour `CONTENT_REJECTED`. Parcours attendus :
`⋯` → Signaler → motif → Envoyer ; `⋯` → Bloquer → Confirmer.

## Ce qui est écarté

- Un écran de profil public (voir `docs/NEXT.md` §6).
- Prévenir le signaleur de l'issue.
- Modération automatique (IA, images).
- Blocage par équipe ; masquer au bloqueur les sorties, voyages, parcours ou participants.
- Signalement anonyme (lien vers `/support`).
- Suspension de compte au niveau plateforme : retrait de l'équipe (ADMIN), ou suppression du compte
  à la main.
- L'e-mail des notifications en prod : le push et la boîte de réception suffisent.
