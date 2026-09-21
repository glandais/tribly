# Ledger — notifications évènementielles

Conception : [`2026-09-18-notifications.md`](2026-09-18-notifications.md). Ce fichier tient **l'état** :
ce qui est fait, ce qui ne l'est pas, ce qui a été vérifié et comment. On le met à jour à chaque
passe, en tête de la phase concernée ; une case ne se coche que vérifiée.

- Branche : `feat/notifications` · worktree `../tribly.worktrees/feat/notifications`
- Contrat : **3.4.0 → 3.5.0** (six endpoints ajoutés), puis **3.5.0 → 3.6.0** (deux de plus pour les
  appareils push) — rien retiré
- Migrations : **V37** `notifications`, **V38** `notification event backoff` — appliquées sans heurt
  sur la base locale restaurée (schéma 36 → 38) le 20 septembre 2026 ; **V39** `push_devices`,
  écrite le 20 septembre 2026, ☑ appliquée sur une base locale neuve le 21 septembre 2026

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

## Phase 4 — Push, côté serveur (20 septembre 2026)

Reprend le §4.2 de `docs/NEXT.md`. Le code est la petite partie ; les préalables console et store
sont plus bas, et rien ne part tant qu'ils ne sont pas faits.

### Le canal
- ☑ Table `push_devices` (V39) : utilisateur, plateforme, jeton, nom, version de l'app,
  `last_seen_at`. **Unique sur le jeton seul**, pas sur (utilisateur, jeton) : un jeton adresse une
  *installation*. Se connecter sur un téléphone prêté déplace la ligne au lieu d'en créer une
  seconde — sans quoi son propriétaire précédent continuerait d'être notifié dessus.
- ☑ `POST /api/push-devices` (enregistrer/rafraîchir, idempotent, appelé à chaque lancement) et
  `DELETE /api/push-devices/{token}` (à la déconnexion, muet sur un jeton qui n'est pas celui de
  l'appelant : la déconnexion ne doit pas devenir un moyen de sonder l'existence d'un jeton)
- ☑ `PushNotificationSender` : un message FCM par appareil du destinataire. `isEnabled()` ne regarde
  pas un booléen mais `FcmClient.isConfigured()` — donc **tant qu'il n'y a pas de compte de service,
  le canal reste indisponible**, le fan-out ne crée aucune livraison et la matrice ne propose
  aucune case. C'est le même filet que pour l'e-mail, §5.
- ☑ `FcmClient` (FCM HTTP v1) : assertion JWT RS256 signée avec la clé du compte de service,
  échangée contre un jeton d'accès **mis en cache** jusqu'à deux minutes avant son expiration.
  Aucune dépendance ajoutée : `smallrye-jwt-build` (déjà là pour nos JWT) signe, et deux
  `@RegisterRestClient` (`fcm`, `google-oauth`) parlent. Ils renvoient `Response` et non un corps
  typé, parce que **le corps d'un 404 est ce qui dit si le jeton est mort**.
- ☑ Purge sur `UNREGISTERED` (et sur un `INVALID_ARGUMENT` à l'envoi) : la ligne part, et la
  livraison n'est **pas** rejouée — la retenter jusqu'à `max-attempts` ne ferait que retarder la
  purge. Seul un échec passager, et seulement si **rien** n'est parti, repasse `PENDING` : si un
  appareil avait reçu, réessayer le notifierait deux fois.
- ☑ Aucun appareil enregistré ⇒ livraison `SENT` sans appel réseau. C'est l'état normal de tout
  membre qui n'utilise que le web ; lever ici aurait fait mourir la livraison en `FAILED`.
- ☑ Titre et corps depuis `NotificationTexts` (les mêmes clés que l'e-mail, aucune à ajouter) ;
  `data` = `type`, `notificationId`, `teamSlug`, `subjectType`, `subjectSlug` et `path` — le chemin
  vient de `NotificationLinks`, donc une route renommée dans `contracts/routes.yaml` déplace le
  deeplink du push et le lien de l'e-mail d'un seul geste.
- ☑ `NotificationMessage` gagne `notificationId` et `recipientUserId` : l'e-mail avait son adresse
  sur la ligne utilisateur, le push doit résoudre des appareils.
- ✗ Badge iOS (`aps.badge`) : il aurait fallu recompter les non-lues à l'envoi, et
  `NotificationMessage` ne porte ni le domaine ni ce compteur. ~~L'app pose son badge elle-même au
  réveil (`content-available: 1` est envoyé pour ça).~~ Le badge posé par l'app a été écarté à son
  tour en phase 4 bis, et `content-available` retiré le 21 septembre 2026 : il réveillait l'app
  tuée avant le tap et faisait perdre ce tap (voir la recette iOS).
- Rien n'a changé au fan-out, comme annoncé : les défauts `PUSH` de `NotificationType` s'appliquent
  du jour où l'émetteur est configuré.

### Configuration
```properties
pedalons.push.enabled=false              # et, en prod, les deux lignes commentées :
# %prod.pedalons.push.fcm.credentials=${FCM_CREDENTIALS}   # chemin du secret monté, ou le JSON
# %prod.pedalons.push.fcm.project-id=${FCM_PROJECT_ID}     # facultatif, sinon celui du JSON
```
Un compte de service illisible **n'empêche pas l'application de démarrer** : l'erreur est journalisée
et le canal reste indisponible. Un démarrage cassé pour une clé mal montée serait pire que pas de
push.

### Tests — verts (lancés par l'utilisateur le 20 septembre 2026)
- ☑ `PushDeviceResourceTest` — enregistrement, double enregistrement, jeton déplacé d'un utilisateur
  à l'autre, désinscription croisée sans effet, jeton vide refusé, 401
- ☑ `PushNotificationTest` — `FcmClient` mocké (le pipeline, pas le format de Google) : livraison
  créée et envoyée à chaque appareil, contenu de `data`, aucun appareil ⇒ `SENT`, jeton mort purgé
  et non rejoué, échec passager rejoué avec l'appareil conservé, canal non configuré ⇒ aucune
  livraison ni case dans la matrice
- ☑ `Notification*Test` et `ArchitectureTest` de nouveau verts : la phase 4 n'a rien bougé au
  fan-out, et le canal reste indisponible en test faute de compte de service

```bash
cd backend
mvn test -Dtest='Push*Test,Notification*Test,ArchitectureTest'
```

### Reste à faire côté serveur
- ☑ Appliquer V39 sur la base locale et faire la recette — faite le 21 septembre 2026 avec la
  phase 4 bis, voir « Recette sur appareil réel » plus bas. **Reportée à la phase 4 bis** le
  20 septembre 2026 : la stack locale de ce dossier n'a plus de `.env` utilisable (celui de la
  recette précédente vivait dans un worktree disparu), et une recette push sans jeton d'appareil ne
  montrerait de toute façon que le cas « aucun appareil ⇒ `SENT` sans appel réseau ». Elle se fera
  quand l'app enregistrera un vrai jeton, avec :
  ```bash
  PEDALONS_PUSH_ENABLED=true \
  PEDALONS_PUSH_FCM_CREDENTIALS=$HOME/Documents/pedalons/firebase/fcm-service-account.json \
  mvn quarkus:dev
  ```
  Au démarrage, le journal doit dire `Push notifications enabled — FCM project pedalons-9e595` ;
  sans cette ligne, le canal est resté indisponible et rien ne sera mis en file.
- ☑ Commit (`e241d921` pour le code, `7c0923c4` pour les préalables console)

### Préalables hors dépôt (20 septembre 2026)

Les fichiers ne sont **pas** dans le dépôt : ils vivent dans `~/Documents/pedalons/firebase/`, à
côté des autres secrets du projet (keystore Android, profil iOS). Rien de tout cela ne se commite.

- ☑ **Projet Firebase** `pedalons-9e595` (numéro de projet `46396427421`), Analytics et Gemini
  laissés **désactivés** : le push n'en a pas besoin, et Analytics aurait ouvert une déclaration de
  collecte de plus dans les deux formulaires de confidentialité. Application Android
  *Pedalons Android* et application Apple *Pedalons iOS*, toutes deux `fr.pedalons.mobile` ⇒
  `google-services.json` et `GoogleService-Info.plist` téléchargés.
- ☑ **Compte de service** `firebase-adminsdk-fbsvc@pedalons-9e595.iam.gserviceaccount.com`, clé JSON
  téléchargée (`fcm-service-account.json`, `chmod 600`). Vérifiée de bout en bout hors application :
  l'assertion RS256 s'échange contre un jeton d'accès, et `messages:send` sur un faux jeton répond
  **400 `INVALID_ARGUMENT`** — donc l'authentification passe, et c'est exactement le cas que
  `FcmClient.isTokenInvalid` traite comme « jeton mort, purger sans rejouer ». L'API *Firebase Cloud
  Messaging (V1)* est active ; l'ancienne API est désactivée, ce qui est le bon sens de l'histoire.
- ☑ **Clé APNs `.p8`** créée (« Pedalons APNs », key id `LCNR9RNF47`, team id `7Q49262697`), en
  **Sandbox & Production** : la portée ne se change plus après coup, et une clé *Sandbox* seule
  n'aurait rien livré en TestFlight. Capacité *Push Notifications* activée sur l'App ID
  `fr.pedalons.mobile` — ce qui **invalide le profil de provisionnement existant**
  (`~/Documents/pedalons/ios/provisioning-profile/pedalons.mobileprovision`) : il faut le
  régénérer avant la prochaine build iOS, en même temps que l'entitlement `aps-environment`.
  La `.p8` est **téléversée dans Firebase sur les deux lignes** (APNs de développement et de
  production), même clé, même key id — le téléversement se fait à la main, la console n'expose pas
  d'`input` fichier mais un sélecteur natif.
- ☑ Permission `POST_NOTIFICATIONS` (Android 13+) et l'écran qui la demande — faites en phase 4 bis,
  où le bandeau de la boîte de réception tient lieu de maquette manquante
- ☑ `mobile/store-metadata/data-safety.md` et `PrivacyInfo.xcprivacy` mis à jour ; ☐ les deux
  formulaires des stores, et la politique de confidentialité, restent à faire
- ☐ Nouvelle soumission aux deux stores

## Phase 4 bis — Push, côté mobile (21 septembre 2026)

Les deux fichiers de configuration sont **commités** (`mobile/android/app/google-services.json`,
`mobile/ios/Runner/GoogleService-Info.plist`) : ce ne sont pas des secrets — identifiants d'app
publics et clé API restreinte au bundle — et les tenir hors dépôt aurait cassé toute build faite
ailleurs. Le compte de service et la clé APNs, eux, restent dans `~/Documents/pedalons/firebase/`.

### Le canal côté appareil
- ☑ `firebase_core` + `firebase_messaging`, **et rien d'autre de Firebase** : pas d'Analytics, pas
  de Crashlytics. `Firebase.initializeApp()` est appelé dans `main.dart` dans un `try` — une app qui
  refuserait de s'ouvrir parce que FCM est injoignable serait un défaut bien pire que l'absence de
  push. La configuration vient des fichiers natifs, donc **pas de `firebase_options.dart`** à tenir
  en phase avec eux.
- ☑ `PushGateway` (`features/notifications/services/push_gateway.dart`) est la seule chose qui
  touche Firebase : autorisation, jeton, flux de messages, bannière. C'est cette couture qui rend
  `push_test.dart` possible — un test de widget qui instancierait `FirebaseMessaging` mourrait sur
  les canaux de plateforme.
- ☑ `PushController` : jeton enregistré à l'ouverture de session **et à chaque rotation**
  (`onTokenRefresh`), `DELETE /api/push-devices/{token}` à la déconnexion. La désinscription est
  appelée par `AuthNotifier.logout` **avant** d'effacer la session, l'endpoint étant authentifié ;
  le jeton lui-même vit dans `registeredPushTokenProvider`, un provider à part, pour que
  `authProvider` n'ait pas à instancier le contrôleur qui l'écoute.
- ☑ **L'app ne demande jamais l'autorisation au lancement.** `PushActivationBanner` la propose
  depuis la boîte de réception, et seulement quand `PUSH` figure dans les canaux de la matrice —
  c'est-à-dire quand le serveur sait vraiment pousser. La boîte de dialogue du système ne s'affiche
  qu'une fois dans la vie d'une installation : la dépenser avant que le membre ait vu ce que l'app
  notifie, c'est la dépenser mal. Refus définitif ⇒ le bandeau renvoie aux réglages au lieu de
  rejouer un bouton sans effet.
- ☑ Canal Android `pedalons_default`, créé par `flutter_local_notifications` (sinon le `channel_id`
  envoyé par `FcmClient` retomberait sur « Divers ») ; c'est elle aussi qui affiche la bannière
  **au premier plan**, qu'Android n'affiche pas tout seul. Sur iOS, `setForegroundNotificationPresentationOptions`.
- ☑ Icône de la barre d'état `ic_stat_notification`, générée par `scripts/generate-icons.sh` : le
  système ne garde que l'alpha et peint tout en blanc, donc l'icône du lanceur donnerait un carré
  blanc. Le masque garde ce qui est **orange** dans `icon.svg` plutôt que d'enlever le bleu — retirer
  le fond laisserait le liseré antialiasé du carré arrondi, c'est-à-dire un cadre bien visible.
- ☑ Tap ⇒ `data.notificationId` marqué lu, puis `data.path` déposé dans `pendingPushRouteProvider`,
  que `main.dart` fait passer par le **même** tuyau qu'un lien web (attendre la session, attendre la
  première route, reconstruire les ancêtres). Les trois états sont couverts : premier plan,
  arrière-plan (`onMessageOpenedApp`) et **application tuée** (`getInitialMessage`).
- ☑ `POST_NOTIFICATIONS` déclarée, et vérifiée sur le manifeste **fusionné** : elle y est,
  `ACCESS_FINE_LOCATION` n'y est toujours pas, et le push ajoute `VIBRATE` et `WAKE_LOCK`.
- ☑ iOS : entitlement `aps-environment` (`development`, réécrit en `production` à l'archivage),
  ~~`UIBackgroundModes: remote-notification` pour le `content-available: 1` du serveur~~ (retiré
  avec `content-available` le 21 septembre 2026, plus rien ne s'en sert), et
  `GoogleService-Info.plist` ajouté aux ressources de la cible Runner.
- ✗ Badge iOS posé par l'app : `flutter_local_notifications` ne sait poser un badge qu'en affichant
  une notification, et en arrière-plan c'est le système qui affiche celle de FCM. Il faudrait une
  dépendance de plus pour un compteur que la cloche montre déjà à l'ouverture.
- ✗ Isolat de fond (`onBackgroundMessage`) : le serveur envoie `notification` **et** `data`, donc le
  système affiche la bannière sans l'app. Un isolat n'aurait rien à faire de plus.

### Vérifications
- ☑ `flutter analyze` propre, `bash check.sh` vert, **560 tests** (545 + 15)
- ☑ `test/features/notifications/push_test.dart` (15 cas) : pas de jeton sans autorisation, jeton
  envoyé une seule fois, bouton d'activation qui enregistre dans la foulée, refus sans erreur,
  rotation réenregistrée, échec d'inscription non retenu (la session suivante réessaie), tap qui
  marque lu et dépose la route, app tuée réveillée par un tap, message sans route, et les quatre
  états du bandeau
- ☑ `flutter build apk --debug` : la desugarisation des bibliothèques du cœur a dû être activée
  (`isCoreLibraryDesugaringEnabled`, `desugar_jdk_libs`) — `flutter_local_notifications` refuse de
  lier sans elle. Avertissement connu et sans effet : `firebase_core` applique encore le plugin
  Gradle Kotlin, ce que Flutter annonce vouloir refuser un jour.
- ☑ `flutter build ios --simulator --debug` : Firebase passe par Swift Package Manager sans Podfile.
- ☑ Recette sur appareil réel **Android** (Pixel 6a, 21 septembre 2026) : build debug sur
  `API_BASE_URL=http://localhost:8080` via `adb reverse tcp:8080 tcp:8080`, backend `quarkus:dev`
  lancé avec `PEDALONS_PUSH_ENABLED=true` et le compte de service (journal : `Push notifications
  enabled — FCM project pedalons-9e595`), base neuve, un membre et un organisateur dans une équipe
  de test.
  - Push activé depuis le bandeau de la boîte ⇒ une ligne `push_devices` (`ANDROID`,
    `Google Pixel 6a`, `1.0.0+51`) ; la matrice propose `[EMAIL, PUSH]`.
  - Une sortie publiée par l'organisateur dans chacun des trois états de l'app — **premier plan,
    arrière-plan, application tuée** (aucun processus) : livraison `PUSH` `SENT` au premier essai,
    bannière reçue, tap qui ouvre la sortie et la marque lue, les trois fois.
  - Délai publication → envoi de 3 à 15 s : c'est le tick du dispatcher, pas un défaut.
- ☑ Recette sur appareil réel **iOS** (iPhone 13 Pro Max, 21 septembre 2026) : build *release*
  signée développement (APNs sandbox) sur `API_BASE_URL=http://<IP du Mac>:8080` — backend lancé
  avec `-Dquarkus.http.host=0.0.0.0`, et le `Domain` en base renommé à cette IP, faute de quoi
  `DomainResolver` ne trouve rien. Une build *debug* ne se relance pas depuis l'écran d'accueil sans
  débogueur : elle ne permet pas le cas « application tuée ».
  - Jeton enregistré (`IOS`), bannière et tap OK au **premier plan** et en **arrière-plan**.
  - **Application tuée : défaut trouvé et corrigé.** Le `content-available: 1` du message réveille
    parfois l'app tuée en arrière-plan dès l'arrivée du push ; `firebase_messaging` retient alors le
    message comme « initial », `getInitialMessage()` — appelé à ce réveil — répond `null`, et au tap
    le plugin reconnaît le même `gcm.message_id`, n'émet **pas** `onMessageOpenedApp` et garde le
    message pour un second `getInitialMessage()` que personne ne faisait. Vu dans le journal de
    l'appareil (réveil `state=2`, puis `didReceive` trois secondes plus tard, rien côté Dart).
    Correctif : `FirebasePushGateway` refait un `getInitialMessage()` à chaque retour au premier plan
    sur iOS (le plugin ne le rend qu'une fois). Et le serveur n'envoie plus `content-available`,
    qui ne servait qu'au badge écarté plus haut ; `UIBackgroundModes: remote-notification` est
    parti avec lui.
  - Après correctif : trois taps sur app tuée, trois ouvertures de la sortie marquée lue — **mais
    les trois par le chemin direct** (le tap lance l'app, `willConnect` porte la réponse) : iOS n'a
    plus réveillé l'app en arrière-plan, il bride ces réveils après des arrêts forcés répétés. Le
    chemin corrigé n'a donc pas été observé ; sans `content-available`, il ne devrait plus se
    présenter.
  - **Après retrait de `content-available` et de `UIBackgroundModes`** : les trois états refaits
    (premier plan, arrière-plan, application tuée) — bannière reçue, tap qui ouvre la sortie et la
    marque lue, les trois fois. Le push n'a pas besoin du mode d'arrière-plan pour s'afficher.
  - Deux fausses pistes écartées en route : agrandir le tampon du canal (le message n'atteignait
    pas Dart du tout), et poser `UNUserNotificationCenter.delegate = self` dans `AppDelegate` — ce
    qui a **coupé la réception** des pushes sur l'iPhone. Ne pas y revenir.

### Déclarations de confidentialité
- ☑ `mobile/store-metadata/data-safety.md` repris : le jeton FCM et le modèle d'appareil entrent à
  l'inventaire (#11, #12), Apple gagne `NSPrivacyCollectedDataTypeDeviceID` (lié, *App
  Functionality*), Play gagne *Device or other IDs*, et la phrase « aucun SDK Firebase » — qui
  était vraie et ne l'est plus — est corrigée plutôt que laissée à pourrir.
- ☑ `ios/Runner/PrivacyInfo.xcprivacy` aligné (`plutil -lint` propre).
- ☐ **La politique de confidentialité ne parle pas encore du push.** Elle doit nommer Google
  (Firebase Cloud Messaging) comme sous-traitant, dire que le titre et le corps de la notification
  passent par lui, et que le jeton est supprimé à la déconnexion — **avant** que le push n'arrive
  aux membres. Consigné dans les points ouverts de `data-safety.md`.
- ☐ Reporter §4 et §5 de `data-safety.md` dans les deux formulaires des stores.

### Reste à faire
- ☑ Profil de provisionnement iOS : **réémis** le 21 septembre 2026 (capacités *Associated Domains,
  In-App Purchase, Push Notifications*, expire le 21/09/2027) et remplacé dans
  `~/Documents/pedalons/ios/provisioning-profile/pedalons.mobileprovision` — profil de
  **développement** (`aps-environment = development`, un seul appareil), celui de la recette sur
  iPhone. Le profil de distribution, lui, est généré par fastlane à l'archivage
  (`-allowProvisioningUpdates`, `3ac6dfa7`).
- ☐ Nouvelle soumission aux deux stores.

## Phase 5 — Nouveaux types et canaux (☐)

- ☐ `RIDE_REMINDER` (J-1) — premier producteur **planifié** : clé de dédup `RIDE_REMINDER:id:date`,
  qui règle d'office l'idempotence exigée par NEXT.md §4.2
- ☐ `RIDE_UPDATED` (heure ou lieu changés, pour les inscrits) — payload enrichi : ce qui a changé
- ☐ `COMMENT_ON_MY_PUBLICATION`, `RIDE_JOINED` (pour l'organisateur), invitation d'équipe reçue
- ☐ Webhook d'équipe : branché sur l'évènement (étage 2), pas par destinataire
- ☐ Résumé quotidien par e-mail (regroupe les `EMAIL` d'une journée)
- ☐ Préférences par équipe (colonne `team_id` nullable sur `notification_preferences`)

## Hors pipeline, à ne pas oublier

- ☑ **Brevo** : gabarit `notification` créé le 21 septembre 2026 (`pedalons-notification-fr` **16**,
  `pedalons-notification-en` **17**, actifs, sujet `{{ params.subject }}`, émetteur
  `Pédalons ! <contact@pedalons.fr>`), rendu depuis les gabarits Qute du repli SMTP
  (`templates/mail/notification.{fr,en}.html`, `base` aplati dedans — Brevo n'a pas d'`include`) et
  les ids renseignés dans `%prod.pedalons.email.brevo.templates.notification.{fr,en}`
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
| 2026-09-20 | Phase 4 | Push côté serveur : `push_devices` (V39), deux endpoints, `PushNotificationSender` et `FcmClient` (FCM HTTP v1 sans dépendance nouvelle — `smallrye-jwt-build` signe l'assertion). Le canal reste indisponible faute de compte de service, ce qui est exactement le filet de §5 : rien n'est mis en file. Contrat 3.6.0, clients régénérés. Le mobile et les préalables console restent à faire. |
| 2026-09-21 | Brevo | Gabarit `notification` créé (16 fr, 17 en) et ids renseignés en `%prod`. Reste la décision produit : `PEDALONS_NOTIFICATIONS_EMAIL_ENABLED=true`. |
| 2026-09-20 | Préalables push | Console faite : projet Firebase `pedalons-9e595` (Analytics et Gemini coupés), apps Android et Apple `fr.pedalons.mobile`, compte de service vérifié hors application (jeton minté, `messages:send` répond 400 `INVALID_ARGUMENT` sur un faux jeton), clé APNs Sandbox & Production créée et capacité *Push Notifications* activée sur l'App ID — ce qui invalide le profil de provisionnement iOS existant. Les fichiers vivent dans `~/Documents/pedalons/firebase/`, hors dépôt. |
| 2026-09-21 | Phase 4 bis | Push côté mobile : `firebase_messaging` derrière une `PushGateway` (seule couche qui connaît Firebase, ce qui rend les tests possibles), enregistrement du jeton et désinscription à la déconnexion, canal Android et icône de barre d'état, tap qui marque lu et ouvre `data.path` par le tuyau des liens web. L'autorisation se demande depuis la boîte de réception et jamais au lancement. Deux surprises de build : `flutter_local_notifications` exige la desugarisation des bibliothèques du cœur, et l'icône de notification ne pouvait pas être celle du lanceur (le système n'en garde que l'alpha). 560 tests verts ; déclarations de confidentialité reprises, politique et formulaires des stores encore à faire. |
| 2026-09-21 | Recette push iOS | iPhone 13 Pro Max, build release signée développement. Premier plan et arrière-plan OK du premier coup. App tuée : tap perdu quand `content-available` avait déjà réveillé l'app — le plugin garde alors le message pour un second `getInitialMessage()`. Corrigé côté app (nouvel appel au retour au premier plan) et côté serveur (`content-available` retiré). Relevé aussi : deux `POST /api/push-devices` concurrents au lancement (session + `onTokenRefresh`), l'un en 500 sur le verrou optimiste — `register` est devenu un upsert natif `ON CONFLICT (token) DO UPDATE`. |
| 2026-09-21 | Recette push Android | Pixel 6a contre un backend local : jeton enregistré, bannière et tap OK au premier plan, en arrière-plan et application tuée. Rien à corriger dans le code. En chemin : `minio/minio` n'est plus tiré depuis Docker Hub (même tag pris sur `quay.io/minio/minio`), et `postgis/postgis:17-3.5-alpine` n'existe pas en arm64 (tiré en amd64). Reste l'iPhone. |
| 2026-09-18 | Revue | Clé de dédup rendue par les évènements `SKIPPED`/`FAILED` ; recul avant nouvelle tentative d'un évènement (V38, `next_attempt_at`) ; récupération des bloqués toutes les 5 min, livraisons bloquées sans tentative restante → `FAILED`. |
