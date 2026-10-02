# Onboarding d'un Karoo — le parcours refait d'un bout à l'autre

Écrit le 2 octobre 2026. Ledger `API-63`.

## 1. Ce qui ne va pas aujourd'hui

Associer un Karoo demande deux choses : lier le Karoo à un compte Pédalons (device code, RFC 8628)
et lier ce compte à Hammerhead (OAuth), sans quoi `POST /api/device/routes/…/sync?type=hammerhead`
ne peut rien envoyer au Karoo. Les deux étapes ont été construites séparément, et ça se voit :

| # | Défaut | Où |
|---|---|---|
| D1 | L'app mobile valide le code et s'arrête là : elle ne parle jamais de Hammerhead. | `mobile/lib/features/device/presentation/pages/device_verify_page.dart` (écran « succès ») |
| D2 | Le web propose Hammerhead **après** « Autoriser » — mais le Karoo, qui interroge `/token` toutes les 5 s, a déjà son jeton, lit `GET /api/device/me` et affiche son second QR avant que l'OAuth ne soit fini. | `frontend/src/pages/device/DeviceVerifyPage.tsx:202-236`, `karoo/…/MainActivity.kt:1579-1621` |
| D3 | Le second QR du Karoo mène à `/profile` : rien n'y dit qu'il faut associer Hammerhead, ni où. | `karoo/…/auth/GpsConnectActivity.kt:99` |
| D4 | Le Karoo n'apprend la connexion que si on appuie sur « J'ai connecté » ; pas de suivi automatique. | `GpsConnectActivity.kt:205-328` |
| D5 | Le retour d'OAuth atterrit toujours sur `/profile?gps_connected=…` / `?gps_error=…`, paramètres que ni le web ni le mobile ne lisent : ni confirmation, ni erreur affichée. | `backend/…/api/gps/GpsResource.java:77-126` |
| D6 | « Ignorer » laisse un Karoo qui ne peut rien recevoir, et l'écran revient au lancement suivant sans plus d'explication. | `MainActivity.kt:261-262, 404-426` |
| D7 | Le web reconnaît un Karoo à son URL (`pathname === '/karoo'`), pas à `VerifyResponse.clientId`. | `DeviceVerifyPage.tsx:41` |

## 2. Le parcours cible

Un seul QR, un seul fil conducteur côté téléphone, et le Karoo qui **suit** au lieu de redemander.

```
 KAROO                                   TÉLÉPHONE (app ou site, même parcours)
 ─────                                   ──────────────────────────────────────
 ① « Associer ce Karoo »
   QR → /karoo?code=ABC123  ───scan───▶  (connexion si besoin, inchangé)
   « Sur ton téléphone : autorise ce     ② « Autoriser ce Karoo ? »  (SEC-2, inchangé)
     Karoo, puis associe Hammerhead »       [Refuser] [Autoriser]
   attend /token …                               │
 ◀── jeton ──────────────────────────────────────┘
   GET /api/device/me                    ③ Hammerhead déjà associé ? ──oui──▶ ⑤
   ├─ Hammerhead ✓ ──────────▶ prêt        non : « Dernière étape : associer Hammerhead
   └─ ✗ ④ « Termine sur ton téléphone »      pour que tes parcours arrivent sur le Karoo »
        (suit /api/device/me toutes les 5 s,  [Associer Hammerhead]
         passe tout seul à « prêt »)              │ OAuth Hammerhead (navigateur)
        petit QR de secours → /karoo/hammerhead   ▼
                                        retour → /karoo?gps_connected=hammerhead
 ◀── /api/device/me : HAMMERHEAD ──────  ⑤ « Ton Karoo est prêt » (ou erreur + Réessayer)
   « Prêt » → écran principal
```

Les règles qui en découlent :

- **L'ordre n'importe plus.** Le Karoo ne décide plus sur une photo prise 5 s après le jeton : à
  l'étape ④ il **suit** `/api/device/me` jusqu'à voir Hammerhead. C'est ce qui supprime D2 sans
  changer l'ordre des étapes, ni la confirmation explicite de SEC-2 (qui reste la première).
- **Le second QR n'est plus le chemin normal.** Le téléphone est déjà sur la bonne page ; le QR de
  l'étape ④ ne sert que si on l'a fermée, et il mène à `/karoo/hammerhead` — une page qui ne fait
  que l'étape ③, jamais au profil (D3).
- **Le téléphone reconnaît un Karoo par `VerifyResponse.clientId == 'karoo'`** (D7), sur le web
  comme dans l'app. Garmin ne voit jamais l'étape Hammerhead.
- **Hammerhead est obligatoire** (décision du 2 octobre 2026) : sans lui le Karoo ne reçoit aucun
  parcours. Ni le téléphone ni le Karoo ne proposent « Plus tard » ou « Ignorer » (D6). Le Karoo
  reste sur l'étape ④ tant que Hammerhead manque, y revient à chaque lancement où
  `/api/device/me` ne le montre pas (une association retirée depuis le profil comprise), et quand
  une synchronisation échoue faute de connexion. Seule sortie : se déconnecter du Karoo. Une panne
  réseau au contrôle ne bloque pas (on laisse passer, comme aujourd'hui).

## 3. Les changements, module par module

### Backend (contrat `10.3.0` → `10.4.0`, additif)

- `GET /api/gps/connect/{serviceType}` prend un `returnTo` optionnel, **une énumération**
  `GpsConnectReturn { PROFILE, DEVICE_KAROO }` (défaut `PROFILE`) — jamais une URL libre, pour ne
  pas rouvrir la redirection ouverte que le commentaire SEC-12 de `GpsResource` écarte.
- `GpsOAuthState` garde ce choix : colonne `return_to` **nullable** (migration Flyway additive, lisible
  par la version précédente pendant le déploiement start-first ; `null` = `PROFILE`).
- `GET /api/gps/callback/{serviceType}` redirige selon `returnTo` : `/karoo?gps_connected=hammerhead`
  ou `/karoo?gps_error=<clé>`, sinon `/profile?…` comme aujourd'hui. Une erreur avant lecture de
  l'état (paramètres absents) garde `/profile`.
- Rien ne change dans le device code (`/device`, `/verify`, `/complete`, `/token`) ni dans
  `GET /api/device/me`.
- Tests : `GpsResourceTest` (redirection `DEVICE_KAROO` en succès et en erreur, valeur inconnue →
  400, défaut `PROFILE`), `GpsOAuthStateRepositoryTest` (persistance de `return_to`).

### Routes (`contracts/routes.yaml`)

- Nouvelle route `deviceHammerhead` : `/karoo/hammerhead`, `web` + `mobile` + `deeplink`.

### Web

- `DeviceVerifyPage` devient un parcours à étapes (autoriser → Hammerhead → prêt), aiguillé par
  `clientId`. L'étape Hammerhead appelle `/api/gps/connect/HAMMERHEAD?returnTo=DEVICE_KAROO`.
- `/karoo` lit `gps_connected` / `gps_error` au retour et affiche l'étape ⑤ ou l'erreur avec
  « Réessayer ».
- Page `/karoo/hammerhead` : l'étape ③ seule (ou ⑤ si déjà associé).
- Le profil lit enfin `gps_connected` / `gps_error` (notification), pour le chemin `PROFILE` (D5).
- e2e `frontend/e2e/flow-device.e2e.ts` : Karoo autorisé → étape Hammerhead proposée ; déjà
  associé → directement « prêt » ; retour `gps_connected` → « prêt » ; retour `gps_error` → erreur.

### Mobile

- `device_verify_page.dart` reprend les mêmes étapes (D1), même aiguillage par `clientId`.
- L'OAuth part dans le navigateur externe (`openLink`, les fournisseurs refusent les webviews),
  avec `returnTo=DEVICE_KAROO`.
- **Le retour dans l'app se fait au premier plan, pas par le lien.** Sur iOS, la redirection du
  callback reste sur le même domaine : Safari l'affiche lui-même (page web ⑤) sans rouvrir l'app.
  L'app recharge donc l'utilisateur (`getMe`) quand elle revient au premier plan, sur la page
  d'appairage et sur la section « Services connectés » du profil, et passe à ⑤ si Hammerhead y est.
- Route `/karoo/hammerhead` câblée dans `router.dart` et `link_launcher.dart`.
- Patrol `device_link_test.dart` : l'étape Hammerhead s'affiche pour un Karoo, pas pour un Garmin ;
  déjà associé → « prêt ». Libellés dans `assets/l10n/{en,fr}.json`.

### Karoo

- Écran ① : le texte annonce les deux étapes à faire sur le téléphone.
- `GpsConnectActivity` devient l'étape ④ : texte « Termine sur ton téléphone », suivi automatique
  de `/api/device/me` toutes les 5 s tant que l'écran est visible, QR de secours vers
  `/karoo/hammerhead`, bouton « Se déconnecter » pour seule sortie. « J'ai connecté » et
  « Ignorer » disparaissent.
- `MainActivity` : contrôle après l'appairage et à chaque lancement (comme aujourd'hui, mais
  bloquant) ; une synchro refusée faute de connexion rouvre ④.
- Pas de test (le module n'en a aucun, `AUD-23`) : recette manuelle sur Karoo, décrite au §4.
  On n'entreprend pas ici le découpage de `MainActivity.kt` (`AUD-22`).

## 4. Recette manuelle

1. Compte sans Hammerhead, Karoo neuf, scan avec l'**app** : autoriser → étape Hammerhead →
   OAuth → l'app revient au premier plan sur « prêt » ; le Karoo passe seul de ④ à « prêt ».
2. Même chose avec le **navigateur** (app non installée) : retour OAuth sur `/karoo`, « prêt ».
3. Compte **déjà** associé : autoriser → « prêt » des deux côtés, le Karoo ne montre jamais ④.
4. Fermer la page sur le téléphone à l'étape ③ : le Karoo reste sur ④ (aussi après relancement) ;
   scan du QR de secours → `/karoo/hammerhead` → association → le Karoo passe seul à « prêt ».
   Retirer Hammerhead depuis le profil, relancer le Karoo : ④ revient.
5. Garmin : aucune étape Hammerhead.
6. Refus OAuth chez Hammerhead : erreur lisible sur `/karoo`, « Réessayer » relance l'OAuth.

## 5. Hors champ

- Proposer Garmin Connect à l'appairage d'une montre Garmin : même mécanique possible
  (`returnTo=DEVICE_GARMIN`), mais rien ne la demande aujourd'hui.
- Faire passer l'OAuth Hammerhead **avant** l'autorisation du Karoo : écarté. Le suivi de
  `/api/device/me` par le Karoo rend l'ordre indifférent, et la confirmation SEC-2 doit rester la
  première chose que voit l'utilisateur.
