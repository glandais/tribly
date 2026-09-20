# Ledger — notifications évènementielles

Conception : [`2026-09-18-notifications.md`](2026-09-18-notifications.md). Ce fichier tient **l'état** :
ce qui est fait, ce qui ne l'est pas, ce qui a été vérifié et comment. On le met à jour à chaque
passe, en tête de la phase concernée ; une case ne se coche que vérifiée.

- Branche : `feat/notifications` · worktree `../tribly.worktrees/feat/notifications`
- Contrat : **3.4.0 → 3.5.0** (six endpoints ajoutés, rien retiré)
- Migrations : **V37** `notifications`, **V38** `notification event backoff` — appliquées sans heurt
  sur la base locale restaurée (schéma 36 → 38) le 20 septembre 2026

Légende : ☑ fait et vérifié · ◐ fait, vérification en attente · ☐ à faire · ✗ écarté (raison sur place)

---

## Phase 1 — Socle backend (18 septembre 2026)

### Modèle et pipeline
- ☑ Enums `NotificationType` (6 types, canaux par défaut), `NotificationChannel`,
  `NotificationSubjectType`, `NotificationEventStatus`, `NotificationDeliveryStatus`
- ☑ Évènements typés : `sealed interface NotificationEvent` + 6 `record`
  (`service/notification/event/`)
- ☑ Entités `NotificationEventEntry` (outbox + instantané), `Notification`, `NotificationDelivery`,
  `NotificationPreference` ; migration V37 alignée à la main sur le mapping (la prod valide le schéma)
- ☑ Étage 1 — `NotificationPublisher` : `INSERT … ON CONFLICT DO NOTHING`, `TxType.MANDATORY`,
  `publicationStatusChanged` comme table unique des transitions qui notifient
- ☑ Étage 2 — `NotificationDispatchService` + `NotificationRecipientResolver` (`switch` exhaustif)
- ☑ Étage 3 — `NotificationDeliveryService` : lots, hors transaction, recul exponentiel
- ☑ `NotificationScheduler` (15 s) + ménage nocturne (bloqués, rétention 90 j)

### Producteurs branchés
- ☑ `RideService.createRide / updateRide`, `TripService.createTrip / updateTrip`,
  `PostService.createPost / updatePost`, `PublicationPublishScheduler`
- ☑ `CommentService.createComment` (réponses uniquement)
- ☑ **`BiketeamMigrationService` rendu muet** (`NotificationPublisher.silently`) : la migration passe
  par `createRide/createPost/createTrip` et aurait annoncé tout l'historique de chaque club

### Canaux
- ☑ `IN_APP` : la ligne `notifications`, toujours active
- ☑ `EMAIL` : `EmailNotificationSender`, gabarit générique `notification` (Qute fr/en),
  textes `notifications/texts_{fr,en}.properties` — **désactivé par défaut**
  (`pedalons.notifications.email.enabled`), activé en `%dev` (Mailhog) et `%test`
- ✗ `PUSH` : le type existe, aucun émetteur — donc aucune livraison créée ni case proposée (phase 4)

### API
- ☑ `GET /api/notifications`, `GET /api/notifications/unread-count`,
  `POST /api/notifications/{id}/read`, `POST /api/notifications/read-all`,
  `GET|PUT /api/notifications/preferences`
- ☑ Contrat régénéré, clients web (Orval) et mobile (Retrofit/Freezed) régénérés :
  `regenerate.sh` vert — `pnpm check` (typecheck, lint, build) et `mobile/check.sh`
  (`flutter analyze`, tests) ; `dart analyze lib/api/generated/` propre

### Tests — verts (lancés par l'utilisateur le 18 septembre 2026)
- ☑ `NotificationPipelineTest` — publication, brouillon, republication dédupliquée, date passée,
  dépublication avant dispatch, annulation (e-mail envoyé et lu dans `MockMailbox`), préférence
  e-mail coupée, membre parti de l'équipe, réponse à un commentaire, mode muet, rétention
- ☑ `NotificationResourceTest` — liste, confidentialité, 401, lu/idempotence, 404 croisé,
  tout lire, matrice de préférences, mise à jour partielle, `IN_APP` refusé (400)
- ☑ `NotificationQueryCountTest` — inbox plate (invariant `…QueryCountTest`)
- ☑ `NotificationTextsTest`, `NotificationEventTest` — tests unitaires purs

```bash
cd backend
mvn test -Dtest='Notification*Test,ArchitectureTest'
# puis les suites dont les services ont reçu un appel au publieur :
mvn test -Dtest='Ride*Test,Trip*Test,Post*Test,Comment*Test,PublicationPublishScheduler*Test'
```

Budget de la liste à surveiller : chaque ligne hydrate 2 entités (notification + évènement), soit
exactement `MAX_ENTITY_GROWTH` (54) entre 3 et 30 lignes. Si le test échoue *là*, c'est une ligne
de trop dans la requête, pas un budget à relever.

### Reste à faire avant de fusionner la phase 1
- ☑ Faire passer les tests ci-dessus
- ☑ Essai manuel, **20 septembre 2026**, `mvn quarkus:dev` sur la base locale restaurée, équipe
  `gaby` (7 membres, dont 6 destinataires) :
  - `RIDE_PUBLISHED` — sortie publiée par l'admin ⇒ évènement `DONE` en un tick, `attempts=1`,
    **6 notifications** (tous les membres sauf l'acteur) et **0 livraison** (les défauts du type sont
    in-app + push, et le push n'a pas d'émetteur : c'est exactement ce que §5 exige).
  - Instantané figé sur l'évènement : équipe, sujet, acteur, `base_url` **du domaine en base**
    (`http://localhost:8090`), pas de la requête — la résolution du site de §8 tient hors HTTP.
  - `GET /api/notifications` d'un autre membre : l'entrée typée, sans texte pré-rendu ;
    `unread-count` cohérent ; `POST /{id}/read` idempotent (204 deux fois) ; `read-all` ⇒ 0.
  - `GET /preferences` : `channels = [EMAIL]` seul — ni `IN_APP` (non configurable) ni `PUSH`
    (indisponible), et la matrice porte bien `enabled`/`enabledByDefault` par type.
  - `RIDE_CANCELLED` — sortie annulée avec un inscrit ⇒ **1 seule** notification (l'inscrit, pas
    l'équipe), 1 livraison `EMAIL` `SENT` au premier essai, e-mail lu dans Mailhog : gabarit
    générique, heure au fuseau du destinataire (09:00Z rendu « 11h00 »), lien du sujet et lien
    « Choisir vos notifications » sur `base_url`.
  - Suppression des évènements ⇒ notifications et livraisons parties en cascade (le chemin qu'emprunte
    la rétention nocturne).
  - Aucune erreur côté notifications dans le journal du serveur.
- ☑ Commit

---

## Phase 2 — Web (20 septembre 2026)

- ☑ Cloche dans l'en-tête (`components/notification/NotificationBell.tsx`) : pastille sur
  `unread-count` (React Query, `refetchOnWindowFocus`, `refetchInterval` 60 s), liste déroulante des
  6 dernières, « Tout marquer lu ». **La liste n'est demandée qu'à l'ouverture du menu** : la
  pastille coûte un `unread-count` par minute, une liste coûterait une page de notifications à
  chaque sondage pour un menu que personne n'a ouvert.
- ☑ Page « Notifications » (`pages/notification/`) : liste paginée, filtre « non lues » **dans l'URL**
  (`?unread=true`, via `useUrlFilters`), états vides absolu et filtré distincts. Route `notifications`
  ajoutée à `contracts/routes.yaml` (`web: true`, `mobile: false`, `deeplink: false` — la phase 3 les
  bascule, sans quoi une app installée avalerait le lien sans écran pour le recevoir), puis
  `pnpm generate-routes`. Pas de `prefetch` SSR : la boîte est propre à l'utilisateur.
- ☑ Libellés par `NotificationType` dans `locales/{fr,en}` ; icône, couleur et route dans
  `components/notification/notificationDisplay.ts` — la route vient du **`subjectType`**, jamais du
  `type`, donc un type de plus sur un sujet existant n'y touche pas.
- ☑ Section « Notifications » du profil (`components/profile/NotificationPreferences.tsx`) : matrice
  type × `channels`, une case par appel (envoyer la matrice entière transformerait chaque défaut en
  dérogation). **Rendue nulle, séparateur compris, quand `channels` est vide** — vérifié en coupant
  `pedalons.notifications.email.enabled`.
- ☑ Lien « Choisir vos notifications » des e-mails : `NotificationLinks.PREFERENCES_PATH` vaut
  désormais `/profile#notifications`, et la section **fait elle-même le défilement** vers son ancre
  une fois ses données chargées — sans quoi le fragment ne fait rien, la section n'existant pas
  encore quand le navigateur le traite.

### Recette en direct (`mvn quarkus:dev` + `pnpm dev`, équipe `gaby`)
- ☑ Pastille à 3, menu déroulant, navigation vers la sortie et décompte à 2 ; « Tout marquer lu »
  vide la pastille et fait disparaître son propre bouton
- ☑ `?unread=true` filtre bien (2 sur 4) ; libellés vérifiés **en français et en anglais**, et le
  rendu relu en thème clair comme en thème sombre
- ☑ Matrice : colonne `E-mail` seule (ni `IN_APP`, ni `PUSH`), dérogation écrite en base pour la
  seule case touchée, e-mail reçu ensuite pour un `RIDE_PUBLISHED` qui n'en envoyait pas par défaut
- ☑ `channels` vide ⇒ section absente, sans double séparateur sur la page profil
- ☑ `pnpm typecheck`, `pnpm lint`, `pnpm i18n:lint`, `pnpm build`, `pnpm test` (56) verts ;
  SSR (`pnpm dev:ssr`) rend `/notifications` en 200, avec le même avertissement `<Navigate>` que
  `/profile` — préexistant, pas une régression

### Reste ouvert côté web
- ☑ `contracts/routes.yaml` : `notifications` est passé `mobile: true` + `deeplink: true` avec la
  phase 3
- ☐ Tests web : aucun test Vitest n'a été ajouté (le dossier n'en compte que 8, tous sur des
  utilitaires) — à trancher si la cloche mérite le sien. Le mobile, lui, a le sien.

## Phase 3 — Mobile (20 septembre 2026)

- ☑ Cloche de la barre d'accueil (`features/notifications/presentation/widgets/notification_bell.dart`) :
  pastille `unread-count`, sondage **au plus une fois par minute** (`kUnreadPollInterval`), arrêté
  net quand la session tombe — sinon l'écran de connexion produirait une 401 par minute. Elle ouvre
  la boîte **pastille ou pas** : une cloche qui n'obéit qu'au-delà de zéro serait l'icône-action sans
  effet que le brief §5 interdit.
- ☑ Écran « Notifications » (non maquetté) : liste paginée `PagedListNotifier`, segmenté
  Toutes / Non lues, états vides absolu et filtré distincts, « Tout marquer lu » dans la barre.
  Il vit dans la branche **Accueil** du shell : `getDestinationIndex` retombe sur l'accueil pour une
  URL qu'aucune destination ne réclame, donc l'onglet allumé et la branche active disent la même
  chose sans règle supplémentaire.
- ☑ Section « Notifications » du profil : matrice type × canal, une case par appel. **Rendue nulle,
  en-tête compris**, quand `channels` est vide — elle porte donc son propre `PdlSectionHeader`,
  contrairement à ses voisines.
- ☑ `contracts/routes.yaml` : `notifications` passe `mobile: true` + `deeplink: true` dans ce commit,
  avec sa hiérarchie de lien froid (`_deepLinkHierarchies` → accueil) et son entrée dans
  `internalRouteTemplates` — sans quoi un lien de notification partirait dans le navigateur
- ☑ `PdlIcons.notifications` / `notificationsOff` ajoutées (seul fichier autorisé à nommer `Icons.*`)
- ☑ Test de widget `test/features/notifications/notifications_page_test.dart` (6 cas) : la phrase
  vient du client, l'équipe remplace l'acteur absent, le type brut n'atteint jamais l'écran, le
  sujet inconnu ne fabrique pas de route

### Recette sur émulateur (Pixel 8a, API locale, compte membre de `gaby`)
- ☑ Pastille « 2 » sur la cloche ; l'écran liste les deux entrées, formulées en français
- ☑ Taper une entrée ouvre la sortie **et** la marque lue : point et graisse disparaissent au
  retour, `read_at` est posé en base
- ☑ Matrice de préférences : six lignes, colonne unique (le nom du canal n'est pas répété par ligne
  quand il n'y en a qu'un) ; une case cochée écrit **une seule** dérogation en base
- ☑ `pedalons.notifications.email.enabled=false` ⇒ la section disparaît entièrement du profil,
  sans titre orphelin entre « Préférences » et « Sécurité »
- ☑ `flutter analyze` propre, `bash check.sh` vert (**545 tests**)
- ☐ Reste à voir sur un appareil réel : thème sombre (l'app suit la préférence utilisateur, pas le
  mode système de l'émulateur), text scaling ×1,3 / ×2,0, et l'ouverture d'un deeplink
  `/notifications` application tuée

## Phase 4 — Push (☐)

Reprend le §4.2 de `docs/NEXT.md`. Le code est la petite partie : entitlement `aps-environment` et
clé APNs `.p8`, `google-services.json`, permission `POST_NOTIFICATIONS` (Android 13+), formulaires de
confidentialité (`mobile/store-metadata/data-safety.md`), nouvelle soumission aux deux stores.

- ☐ Table `push_devices` (user, plateforme, jeton, `last_seen_at`), `POST`/`DELETE /api/push-devices`
- ☐ `PushNotificationSender` (FCM HTTP v1) — un envoi par appareil ; `isEnabled()` sur la présence
  des identifiants
- ☐ Purge des jetons sur `UNREGISTERED` — sans quoi la table grossit indéfiniment
- ☐ Titre/corps depuis `NotificationTexts` ; `data` = type + slugs pour le deeplink
- Rien à changer au fan-out : le jour où l'émetteur est actif, les défauts `PUSH` de
  `NotificationType` s'appliquent et les livraisons apparaissent

## Phase 5 — Nouveaux types et canaux (☐)

- ☐ `RIDE_REMINDER` (J-1) — premier producteur **planifié** : clé de dédup `RIDE_REMINDER:id:date`,
  qui règle d'office l'idempotence exigée par NEXT.md §4.2
- ☐ `RIDE_UPDATED` (heure ou lieu changés, pour les inscrits) — payload enrichi : ce qui a changé
- ☐ `COMMENT_ON_MY_PUBLICATION`, `RIDE_JOINED` (pour l'organisateur), invitation d'équipe reçue
- ☐ Webhook d'équipe : branché sur l'évènement (étage 2), pas par destinataire
- ☐ Résumé quotidien par e-mail (regroupe les `EMAIL` d'une journée)
- ☐ Préférences par équipe (colonne `team_id` nullable sur `notification_preferences`)

## Hors pipeline, à ne pas oublier

- ☐ **Brevo** : créer le gabarit `notification` (fr, en ; sujet `{{ params.subject }}`), renseigner
  `%prod.pedalons.email.brevo.templates.notification.{fr,en}` — **avant** d'activer l'e-mail en prod
- ☐ **Décision produit** : activer `PEDALONS_NOTIFICATIONS_EMAIL_ENABLED=true` en production, en
  connaissant les défauts (annulations, voyages publiés)
- ☐ Export RGPD (`UserExportBuilder`) : y inclure les notifications et préférences
- ☐ Suppression de compte : les lignes suivent l'utilisateur (FK `on delete cascade` en base), mais
  les comptes sont supprimés *logiquement* — vérifier ce que la purge doit en faire

## Journal

| Date | Passe | Notes |
|---|---|---|
| 2026-09-18 | Phase 1 | Conception, socle backend, contrat 3.5.0, clients régénérés. Découverte en cours de route : la migration biketeam passe par les services producteurs → mode muet ajouté. Tests verts, commité. |
| 2026-09-20 | Phase 3 | Mobile livré : cloche, écran, matrice, deeplink. Deux filets du dépôt ont demandé leur entrée (`_deepLinkHierarchies`, `internalRouteTemplates`) — c'est leur raison d'être. « Tout marquer lu » a été redérivé des lignes visibles en plus du compteur global, qui est muet hors session. |
| 2026-09-20 | Phase 2 | Web livré : cloche, page, libellés fr/en, matrice de préférences, ancre des e-mails. Deux défauts trouvés en recette et corrigés sur place : le fragment `#notifications` n'amenait nulle part, et la section masquée laissait un double séparateur. |
| 2026-09-20 | Recette locale | Essai manuel de bout en bout sur la base restaurée : publication, fan-out, inbox, préférences, annulation, e-mail Mailhog, cascade. Rien à corriger. Relevé au passage, **hors notifications** : `POST /api/teams/{slug}/rides` lève une NPE 500 quand `media.assets` est `{}` (`AssetService.updateAssets` déréférence `images()` nul) — le client web envoie toujours des listes, donc invisible depuis l'application. |
| 2026-09-18 | Revue | Clé de dédup rendue par les évènements `SKIPPED`/`FAILED` ; recul avant nouvelle tentative d'un évènement (V38, `next_attempt_at`) ; récupération des bloqués toutes les 5 min, livraisons bloquées sans tentative restante → `FAILED`. |
