# Audit de sécurité Pedalons — septembre 2026

- **Révision auditée** : `0a4c211a` (develop, arbre propre). Statuts mis à jour le 2026-09-29 (develop, `e404330c`).
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

> **Version expurgée.** Pour les constats encore ouverts, ce fichier ne garde que l'identifiant, la sévérité et une phrase de constat. Le détail (preuves, scénarios, correctifs) est conservé hors du dépôt. Les constats corrigés gardent leur détail.

## Synthèse

| # | Sévérité | Constat | Statut |
|---|---|---|---|
| H1 | **Élevée** | Le code OTP à 6 chiffres se brute-force sans limite : prise de compte, admin plateforme compris | Corrigé (commit `911e93a2`) |
| H2 | **Élevée** | Des fichiers téléversés peuvent être servis de façon à exécuter du contenu actif (XSS stockée) | Ouvert |
| H3 | **Élevée** | L'autorisation d'un appareil peut aboutir sans confirmation explicite de l'utilisateur | Ouvert |
| H4 | **Élevée** | L'app mobile peut transmettre ses identifiants à des hôtes autres que l'API | Ouvert |
| H5 | **Élevée** | Un point du flux d'autorisation des appareils n'a aucune limitation de débit | Ouvert |
| M1 | Moyenne (élevée en chaîne) | L'access token n'est pas lié à son domaine : l'utilisateur est résolu par e-mail sur le Host de la requête | Corrigé (commit `6a791794`) |
| M2 | Moyenne | Le flou d'~1 km de la position des annonces peut être affiné par des requêtes répétées | Ouvert |
| M3 | Moyenne | Un traitement de tracé GPX n'est pas borné en mémoire (déni de service) | Ouvert |
| M4 | Moyenne | La connexion par mot de passe n'a ni limitation de débit ni verrouillage | Ouvert |
| M5 | Moyenne | Un lien de vérification d'e-mail peut connecter la victime à un compte qui n'est pas le sien (login CSRF) | Ouvert |
| M6 | Moyenne | Une expression régulière appliquée au markdown est exposée au ReDoS | Partiellement corrigé : l'expression est linéaire (ledger `SEC-10`) ; la taille du markdown reste à borner (`SEC-19`) |
| M7 | Moyenne | Le refresh token n'est pas renouvelé à l'usage | Ouvert |
| M8 | Moyenne | Deux requêtes de résolution d'identité ne filtrent pas par domaine | Ouvert |
| M9 | Moyenne | Le jeton d'accès des appareils a une durée longue pour un jeton non révocable | Ouvert |
| M10 | Moyenne | Aucune limitation de débit HTTP globale | Ouvert |
| L1–L14 | Faible | Voir la section dédiée | L2 caduc, L11 partiellement corrigé, les autres ouverts |
| V1–V8 | À valider | Faits hors du dépôt, dont la clé JWT présente dans l'historique public | V2 caduc pour l'avenir, les autres à valider |

H5, M7 à M10 et L12 à L14 viennent de l'audit d'infrastructure de février
([`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md), lignes S2 à S11), versés ici
le 29 septembre 2026 après revérification dans le code : la sécurité applicative n'est suivie
qu'ici. S8 et S12 de ce même audit (origines CORS et cookie non `Secure` par défaut) sont des défauts
de développement surchargés en `%prod`, rangés dans les contrôles conformes.

Les constats ouverts sont suivis, sans détail, sous le préfixe `SEC` de [`LEDGER_NEXT.md`](LEDGER_NEXT.md) (colonne « Audit ») : un
changement de statut ici se reporte là-bas.

**Ordre de correction conseillé** :
1. ~~H1~~ (corrigé), H2, H3 et H4.
2. Vérifier V1.
3. ~~M1~~ (corrigé).
4. M3 et M4.

---

## Constats de sévérité élevée

### H1 — Brute-force illimité du code OTP — **Corrigé (commit `911e93a2`)**

- **Acteur** : anonyme qui connaît l'e-mail d'un utilisateur vérifié.
- **Constat d'origine** : la vérification de l'OTP (`AuthService.java`) ne comptait pas les échecs. Le code a 6 chiffres et reste valide 5 minutes ; `otpMaxAttempts` ne limitait que le nombre d'*envois* de code, jamais le nombre de *vérifications*. L'admin plateforme créé au bootstrap se connecte par OTP : la prise de ce compte donnait accès à tous les tenants.
- **Correctif appliqué** : colonne `failed_attempts` sur `auth_tokens` (migration V42), incrémentée même quand la vérification échoue (`@Transactional(dontRollbackOn = BadRequestException.class)`). Le code est brûlé après `pedalons.auth.otp.max-verify-attempts` échecs (5 par défaut). Test de régression dans `AuthServiceTest`.
- La limitation de débit sur l'API d'authentification relève de M4, toujours ouvert.

### H2 — XSS stockée via un fichier téléversé — **Ouvert**

### H3 — Approbation d'un appareil sans confirmation — **Ouvert**

### H4 — Fuite des identifiants mobiles vers des hôtes tiers — **Ouvert**

### H5 — Flux d'autorisation des appareils sans limitation de débit — **Ouvert** (audit de février, S2)

---

## Constats de sévérité moyenne

### M1 — L'access token n'est pas lié à son tenant — **Corrigé (commit `6a791794`)**

- **Constat d'origine** : `PedalonsQueryContext` ignorait les claims `domainId` et `userId` dès que le Host résolvait un domaine, et chargeait l'utilisateur par e-mail sur ce domaine. Une seule clé de signature sert à tous les tenants : un token émis sur le domaine A pour l'e-mail E était accepté sur le domaine B, où il agissait en tant que le compte B de E, qui est un compte distinct. Une révocation sur B restait aussi sans effet sur un token émis par A.
- **Correctif appliqué** : un JWT dont la claim `domainId` diffère du domaine résolu n'authentifie plus personne. Test `AccessTokenDomainTest`, qui rejoue un token de A contre B.

### M2 — Le filtre de proximité des annonces contourne le flou d'~1 km — **Ouvert**

### M3 — DoS mémoire sur le traitement GPX — **Ouvert**

### M4 — Pas de throttling sur la connexion par mot de passe — **Ouvert**

### M5 — Login CSRF via le lien de vérification d'e-mail — **Ouvert**

### M6 — ReDoS sur le markdown — **Partiellement corrigé** (ledger `SEC-10`, reste `SEC-19`)

### M7 — Pas de rotation du refresh token — **Ouvert** (audit de février, S3)

### M8 — Résolution d'identité sans filtre de domaine — **Ouvert** (audit de février, S4 et S5)

### M9 — Jeton d'accès des appareils trop long pour un jeton non révocable — **Ouvert** (audit de février, S6)

### M10 — Pas de limitation de débit HTTP globale — **Ouvert** (audit de février, S7)

---

## Constats de sévérité faible

| # | Constat | Statut |
|---|---|---|
| L1 | Réinitialiser le mot de passe ne révoque pas les sessions existantes | Ouvert |
| L2 | L'état OAuth Strava n'était pas lié au navigateur qui avait lancé le flux | Caduc : la connexion Strava a été retirée (API 5.0.0) |
| L3 | Les comptes existants peuvent être énumérés | Ouvert |
| L4 | Pré-inscription : un mot de passe fixé avant la vérification de l'e-mail survit à celle-ci | Ouvert |
| L5 | Le contrôle de domaine est incomplet sur la lecture d'un asset | Ouvert |
| L6 | Le contrôle de domaine est incomplet sur l'ajout d'un membre à une équipe | Ouvert |
| L7 | SSR : un contenu utilisateur peut défigurer la page rendue (pas de XSS repérée) | Ouvert |
| L8 | Un paramètre n'est pas encodé dans une redirection | Ouvert |
| L9 | Un nom de fichier n'est pas encodé dans une URL | Ouvert |
| L10 | Karoo : les tokens sont stockés sans chiffrement et inclus dans les sauvegardes | Ouvert |
| L11 | GitHub Actions : durcissement des workflows | Partiellement corrigé : `ci.yml` est en `permissions: contents: read` par défaut (commit `09c65ecd`) ; le reste est ouvert |
| L12 | Des jokers ne sont pas échappés dans des recherches (audit de février, S9) | Ouvert |
| L13 | Un en-tête de réponse est construit sans encodage (audit de février, S10) | Ouvert |
| L14 | Les échecs de connexion ne sont pas journalisés (audit de février, S11) | Ouvert |

Informationnel (ouverts) :
- Le markdown web accepte des images externes (pistage de l'IP des lecteurs, sans fuite de token).
- Un parseur XML n'est pas durci ; ce n'est pas exploitable aujourd'hui.
- Un paramètre de requête du frontend n'est pas encodé.

---

## À valider : faits hors du dépôt

| # | Hypothèse | Statut |
|---|---|---|
| V1 | Une clé privée JWT a figuré dans l'historique public du dépôt : vérifier que les clés de production et de staging n'en sont pas des copies (**critique** si c'est le cas) | À valider |
| V2 | La migration biketeam par dump pouvait rattacher un compte Pedalons existant sur une preuve d'e-mail insuffisante | Caduc pour l'avenir : l'import par dump a été retiré (commit `d93fd3af`) et la migration en direct n'importe aucune personne. Les comptes déjà importés n'existent plus en staging ni en prod (vérifié le 2026-09-29, `SEC-15`) |
| V3 | La confiance accordée aux en-têtes `X-Forwarded-*` dépend de la configuration du proxy de l'hôte | À valider |
| V4 | Les en-têtes de sécurité HTTP dépendent de la configuration du proxy de l'hôte | À valider |
| V5 | L'intégrité de l'historique des sauvegardes dépend de la configuration de l'hôte de sauvegarde | À valider |
| V6 | La protection CSRF entre tenants dépend de la façon dont leurs domaines sont enregistrés | À valider |
| V7 | La neutralisation des en-têtes des e-mails dépend du client SMTP | À valider |
| V8 | Le traitement des SVG par imgproxy dépend de sa configuration | À valider |

---

## Contrôles vérifiés et conformes

- **Tokens** :
  - Refresh, vérification, reset et device : 32 octets `SecureRandom`, stockés hachés en SHA-256 ; à usage unique, sauf le refresh token, qui n'est pas renouvelé (M7).
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
  - gpx2web durci contre XXE et billion laughs (1.4.5 à l'audit, toujours vrai en 1.5.2).
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
  - Pas d'open redirect. (Un open redirect via `?next=` a depuis été trouvé hors de cet audit et corrigé, commit `9665d15e`.)
  - Source maps désactivées en prod.
- **Mobile** :
  - Refresh token dans `flutter_secure_storage`.
  - Aucun `badCertificateCallback`, aucune WebView.
  - Uniquement des App Links https vérifiés, sans scheme custom.
- **Infra** :
  - Postgres, Traefik, Mailpit, MinIO et imgproxy exposés sur loopback uniquement. Depuis le passage des hôtes à Docker Swarm (postérieur à l'audit), qui ne sait pas publier sur le loopback : sur un hôte, postgres ne publie plus rien et Traefik écoute sur toutes les interfaces, fermé par des règles `DOCKER-USER` qui ne laissent passer que Caddy ([`OPERATIONS.md`](OPERATIONS.md#only-caddy-may-reach-traefik)) ; le loopback ne vaut plus que pour un poste de travail.
  - Dashboard Traefik désactivé.
  - Origines CORS de développement et cookie non `Secure` seulement par défaut : `%prod` les surcharge (audit de février, S8 et S12).
  - `.env` jamais versionné.
  - Staging des sauvegardes en `700`, restauration avec confirmation.
  - Aucun keystore ni `.p8` versionné. `mobile/android/app/google-services.json` l'est depuis le push (commit `0bd0db44`, postérieur à l'audit) : c'est une configuration client Firebase, pas un secret serveur.
