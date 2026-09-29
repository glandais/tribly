# Politique de confidentialité : points ouverts

Relevés le 29 septembre 2026 pendant l'audit des markdowns. Les corrections que le code prouve sont
déjà dans `privacy/privacy-policy.{fr,en}.md` : export dans l'app, Web Push, stockage local,
`SameSite=Lax`. Ce qui suit est resté en l'état parce qu'il engage juridiquement et demande une
décision. Toute modification se fait **en parité FR/EN**. Le texte est embarqué dans l'app mobile
(`privacy/` est un asset) : un changement n'y apparaît qu'avec la build suivante.

Suivi : chaque point est repris au §7.3 de [`LEDGER_NEXT.md`](../LEDGER_NEXT.md), avec ses liens
vers les lignes voisines du ledger. Un point tranché quitte les deux fichiers.

## 1. Import biketeam et « pas de données de tiers »

- **Constat** : la migration biketeam a importé des comptes de membres, e-mails compris. Le §2 de
  la politique dit pourtant « We do not collect data from third parties ».
- **À décider** :
  - faut-il ajouter une sous-section sur l'import et nuancer la phrase du §2 ?
  - qui est responsable du traitement côté biketeam ?
  - quelle base légale : intérêt légitime, ou exécution du contrat ?
  - comment les membres importés ont-ils été, ou seront-ils, informés ?
- **Contexte** : l'import par dump a été retiré le 2026-09-28. La migration en direct
  ([plan](2026-09-22-biketeam-live-migration.md)) n'importe aucune personne, mais les comptes
  déjà créés restent en base.

## 2. Relais des messages vers l'auteur d'une annonce (`AdContact`)

- **Constat** : la table `ad_contacts` n'a aucune purge. Une ligne vit tant que le compte de
  l'expéditeur existe, ou jusqu'à la suppression physique de l'annonce. Le corps du message n'est
  pas stocké : il est relayé par e-mail.
- **À décider** :
  - une durée de conservation à annoncer au §6, ou l'ajout d'une purge. Une purge simplifie le
    texte.
  - une finalité et une base légale au §3 : exécution du contrat pour le relais, intérêt légitime
    pour la limitation des abus ?

## 3. Position précise envoyée par l'app Garmin

- **Constat** : `garmin-app/source/ApiClient.mc` envoie la position GPS précise de l'appareil
  (`?lat=&lon=`) pour trier les parcours par proximité. Le client Karoo
  (`PedalonsApiClient.getRoutes`) accepte les mêmes paramètres, mais l'app ne les renseigne pas
  aujourd'hui : `MainActivity` appelle `getRoutes(accessToken)` sans position, et l'app ne demande
  aucune permission de localisation. À surveiller si on les branche. Cette position est lue à l'ouverture de la liste et n'est pas stockée, mais elle figure
  dans les logs d'accès comme toute URL. La politique ne parle que de la position approximative de
  l'app mobile, et affirme « we do not track your real-time location ».
- **À décider** :
  - faut-il une sous-section sur les extensions GPS, et avec quelle base légale ?
  - Autre option : arrondir la position côté appareil, sur la même grille d'~1 km que les annonces.
    Le texte existant resterait alors vrai.

## 4. Contenu public et non listé lisible sans compte

- **Constat** : le §4 dit que le contenu public est accessible « à tous les utilisateurs de la
  plateforme ». Or le rendu serveur anonyme et la visibilité `PUBLIC_UNLISTED` rendent un contenu
  public ou non listé lisible par n'importe qui ayant le lien, sans compte.
- **Proposition** : « public : accessible à tous, y compris sans compte, et indexable ; non listé :
  accessible à quiconque a le lien, sans compte ».

## 5. Web Push : formulation et calendrier

- **Constat** : la politique dit désormais que, dans un navigateur, les notifications passent « par
  le service push propre au navigateur ». Elle nomme FCM (Google) comme relais, mais pas
  les services push des éditeurs de navigateurs qui remettent le message (Google pour Chrome,
  Mozilla, Apple, Microsoft), ni les transferts correspondants. Le Web Push est en production depuis
  le 29 septembre 2026 : la question n'est plus théorique.
- **À décider** : faut-il lister ces services dans le tableau des sous-traitants et des transferts ?

## 6. Points mineurs

- **Wahoo** : il s'active par domaine (`DomainFormModal`). S'il est actif sur pedalons.fr, il faut
  l'ajouter à la dernière FAQ de `privacy/support.{fr,en}.md` et au §2 des CGU, qui ne citent que
  Karoo et Garmin.
- **Classement Play du message à l'auteur d'une annonce** : il est rattaché à « Other user-generated
  content » dans `mobile/store-metadata/data-safety.md` (#20). Faut-il le déclarer plutôt, ou en
  plus, en « Messages → Other in-app messages » ?
- **Jeton de calendrier** : il est stocké en clair (`CalendarToken.token`). La politique (§9) parle
  de jetons « stored as irreversible hashes ». Elle ne décrit pour l'instant ce jeton que comme une
  clé secrète régénérable. Il faut soit le hacher, soit l'exclure explicitement de cette phrase.
