# Ledger — notifications évènementielles

Conception : [`2026-09-18-notifications.md`](2026-09-18-notifications.md). Ce fichier tient **l'état** :
ce qui est fait, ce qui ne l'est pas, ce qui a été vérifié et comment. On le met à jour à chaque
passe, en tête de la phase concernée ; une case ne se coche que vérifiée.

- Branche : `feat/notifications` · worktree `../tribly.worktrees/feat/notifications`
- Contrat : **3.4.0 → 3.5.0** (six endpoints ajoutés, rien retiré)
- Migration : **V37** `notifications`

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
- ☐ Essai manuel : `mvn quarkus:dev`, publier une sortie, voir l'évènement passer `DONE` et
  `GET /api/notifications` d'un autre membre ; annuler une sortie avec un inscrit, voir l'e-mail dans
  Mailhog (:8025)
- ☑ Commit

---

## Phase 2 — Web (☐)

- ☐ Cloche dans l'en-tête : pastille sur `unread-count` (React Query, `refetchOnWindowFocus`,
  `refetchInterval` ≥ 60 s), liste déroulante des dernières notifications, « Tout marquer lu »
- ☐ Page « Notifications » (liste paginée, filtre non lues) — route à ajouter dans
  `contracts/routes.yaml` puis `pnpm generate-routes`
- ☐ Libellés par `NotificationType` dans `locales/{fr,en}` ; route par `subjectType`
- ☐ Section « Notifications » du profil : matrice type × `channels` (masquée si `channels` est vide)
- ☐ Lien « Choisir vos notifications » des e-mails : aujourd'hui `/profile`, à faire pointer sur la
  section (ancre) une fois qu'elle existe — `NotificationLinks.PREFERENCES_PATH`

## Phase 3 — Mobile (☐)

- ☐ Cloche de l'accueil (NEXT.md l'interdisait tant qu'il n'y avait pas d'endpoint : il existe)
- ☐ Écran « Notifications » (non maquetté — à faire valider)
- ☐ Section « Notifications » du profil (même règle : non rendue si `channels` est vide)

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
| 2026-09-18 | Revue | Clé de dédup rendue par les évènements `SKIPPED`/`FAILED` ; recul avant nouvelle tentative d'un évènement (V38, `next_attempt_at`) ; récupération des bloqués toutes les 5 min, livraisons bloquées sans tentative restante → `FAILED`. |
