# Politique de confidentialité

**Dernière mise à jour : 21 septembre 2026**

La présente politique de confidentialité décrit la manière dont Pedalons (« nous », « notre », « nos ») collecte, utilise et protège vos données personnelles lorsque vous utilisez notre plateforme (site web, application mobile, extensions pour appareils GPS).

Pour toute question relative à vos données personnelles, vous pouvez nous contacter à l'adresse : **privacy@pedalons.fr**

---

## 1. Données que nous collectons

### Données de compte

Lors de la création de votre compte, nous collectons :

- **Adresse e-mail** : pour l'authentification et les communications liées au service
- **Nom d'affichage** : choisi par vous, visible par les membres de votre équipe
- **Photo de profil** (facultatif) : image que vous téléchargez pour personnaliser votre profil. Dans l'application mobile, vous la choisissez dans votre photothèque par le sélecteur du système : l'application ne reçoit que la photo choisie, et n'accède ni à l'appareil photo ni au reste de votre photothèque.
- **Préférences** : système d'unités (métrique/impérial), langue

### Données d'authentification

Pour sécuriser l'accès à votre compte, nous traitons :

- **Clés d'accès (passkeys/WebAuthn)** : identifiant de clé, clé publique et compteur de signatures. La clé privée reste sur votre appareil et ne nous est jamais transmise.
- **Jetons de session** : un jeton de rafraîchissement (haché, jamais stocké en clair) est conservé dans un cookie sécurisé HttpOnly pendant 30 jours maximum.
- **Liens magiques et codes à usage unique (OTP)** : hachés côté serveur, valides 5 minutes.
- **Codes d'appairage d'appareils GPS** : codes temporaires (10 minutes) pour connecter des appareils Karoo ou Garmin.

### Données de session

À chaque connexion, nous enregistrons :

- **Adresse IP** et **agent utilisateur** (type de navigateur/appareil) : pour la sécurité du compte et la détection d'activité suspecte.
- **Date de dernière connexion** et **dernière utilisation de session**.

### Données de localisation et GPS

Lorsque vous créez ou consultez des itinéraires :

- **Traces GPS** : coordonnées géographiques (latitude, longitude, altitude) issues de fichiers GPX que vous importez.
- **Points d'intérêt** : noms et coordonnées des lieux que vous ajoutez.
- **Coordonnées de l'équipe** (facultatif) : point géographique représentant la localisation de votre équipe.

### Position approximative (application mobile, facultatif)

Si vous activez le filtre « autour de moi » de l'application mobile, celle-ci lit la position **approximative** de votre téléphone (à quelques centaines de mètres près, jamais la position précise) et l'envoie comme critère de recherche pour trier les parcours par distance. Cette position :

- n'est lue qu'à ce moment-là, application ouverte, et avec votre autorisation — jamais en arrière-plan ;
- n'est ni enregistrée avec votre compte, ni conservée dans notre base de données ;
- comme l'adresse de toute requête, peut figurer dans les journaux techniques de notre serveur web.

**Important** : nous ne suivons pas votre position en temps réel et n'enregistrons pas vos sorties. Les traces GPS proviennent exclusivement de fichiers que vous importez volontairement.

### Contenu que vous créez

- **Sorties (rides)** : titre, description, date, groupes de niveau, itinéraire associé.
- **Publications (posts)** : texte au format Markdown.
- **Commentaires** : texte associé à une publication.
- **Itinéraires (routes)** : nom, distance, dénivelé, type de surface, traces et points GPS.
- **Photos et images** : fichiers que vous téléchargez pour illustrer vos contenus.

### Notifications

Pour vous prévenir de ce qui se passe dans vos équipes (nouvelle sortie, commentaire, changement d'une sortie à laquelle vous êtes inscrit…), nous traitons :

- **Vos notifications** : le type d'évènement, l'élément concerné, le texte affiché et la date à laquelle vous l'avez lue. Elles apparaissent dans la page Notifications du site et de l'application.
- **Vos préférences de notification** : pour chaque type de notification, les canaux par lesquels vous acceptez d'être prévenu (e-mail, notification sur votre téléphone).
- **Le suivi des envois** : pour chaque notification envoyée par e-mail ou sur votre téléphone, le canal, l'état de l'envoi et sa date.
- **L'enregistrement de votre téléphone** (application mobile uniquement, et seulement si vous autorisez les notifications) : un jeton d'enregistrement délivré par Firebase Cloud Messaging (service de Google), le système (Android ou iOS), le modèle de l'appareil et la version de l'application. Ce jeton identifie l'installation de l'application, pas votre personne ; il nous sert uniquement à adresser les notifications à votre téléphone.

L'application ne demande l'autorisation d'afficher des notifications que lorsque vous le choisissez, jamais à son lancement. Vous pouvez la retirer à tout moment dans les réglages de votre téléphone, ou couper un type de notification depuis vos préférences de notification. Le jeton est supprimé de nos serveurs lorsque vous vous déconnectez de l'application, lorsque Google nous signale qu'il n'est plus valide (application désinstallée, par exemple) et lorsque vous supprimez votre compte.

### Données de connexion à des services GPS tiers

Si vous connectez un service GPS externe (Hammerhead, Garmin, Wahoo) :

- **Jetons d'accès OAuth** : chiffrés en AES-256-GCM avant stockage. Nous ne stockons jamais vos identifiants (nom d'utilisateur/mot de passe) de ces services.
- **Identifiant utilisateur externe** : fourni par le service tiers pour faire le lien avec votre compte Pedalons.

### Données stockées localement sur votre appareil

Dans votre navigateur web ou application mobile :

- **Préférence de langue** : dans le stockage local (localStorage)
- **Système d'unités** : dans le stockage local
- **Préférences de carte** : style de carte choisi, dans le stockage local
- **Cookie de session** : un cookie HttpOnly contenant votre jeton de rafraîchissement (non accessible par JavaScript)

---

## 2. Comment nous collectons vos données

- **Directement auprès de vous** : lorsque vous créez un compte, remplissez votre profil, importez des fichiers GPX, créez du contenu ou connectez un service GPS.
- **Automatiquement** : adresse IP et agent utilisateur lors de vos connexions ; cookie de session pour maintenir votre authentification.
- **Nous ne collectons aucune donnée auprès de tiers** : pas d'achat de données, pas de suivi publicitaire, pas de collecte via des réseaux sociaux.

---

## 3. Pourquoi nous utilisons vos données

| Finalité | Base légale (RGPD) |
|----------|-------------------|
| Fournir le service (compte, authentification, navigation) | Exécution du contrat |
| Afficher les itinéraires et sorties de votre équipe | Exécution du contrat |
| Envoyer des e-mails de vérification et codes de connexion | Exécution du contrat |
| Vous notifier dans le site et l'application de l'activité de vos équipes | Exécution du contrat |
| Vous notifier par e-mail, selon vos préférences de notification | Exécution du contrat (réglable à tout moment) |
| Vous notifier sur votre téléphone (notifications push) | Consentement (autorisation donnée sur le téléphone) |
| Sécuriser votre compte (détection de sessions suspectes) | Intérêt légitime |
| Synchroniser vos itinéraires avec des appareils GPS connectés | Consentement (connexion volontaire) |
| Trier les parcours par distance (filtre « autour de moi » de l'application) | Consentement (autorisation de localisation donnée sur le téléphone) |
| Afficher les cartes | Intérêt légitime |
| Améliorer le service (analyse agrégée d'utilisation) | Intérêt légitime |

Nous n'utilisons **jamais** vos données pour :
- De la publicité ciblée
- La revente à des tiers
- Du profilage automatisé ou de la prise de décision automatisée

---

## 4. Partage de vos données

### Visibilité au sein de la plateforme

- **Contenu d'équipe** : visible uniquement par les membres de votre équipe (visibilité « équipe »).
- **Contenu public** : si vous ou votre équipe choisissez la visibilité « public », le contenu est accessible à tous les utilisateurs de la plateforme.
- **Votre nom d'affichage et photo de profil** sont visibles par les membres de vos équipes.

### Sous-traitants techniques

Nous faisons appel à des services techniques pour le fonctionnement de la plateforme :

| Service | Rôle | Données concernées |
|---------|------|-------------------|
| OVHcloud (OVH SAS, France) | Hébergement de l'application, de la base de données et du stockage objet | Toutes les données |
| Brevo (Sendinblue SAS, France) | Envoi d'e-mails transactionnels et des notifications par e-mail | Adresse e-mail, nom d'affichage, contenu de l'e-mail (dont le titre et le texte des notifications) |
| Google Firebase Cloud Messaging (Google Ireland Limited, Irlande) | Acheminement des notifications push vers l'application mobile, via Apple Push Notification service pour les iPhone | Jeton d'enregistrement du téléphone, titre et texte de chaque notification, identifiant technique permettant d'ouvrir le bon écran au toucher |

**Tous nos services de traitement d'images (imgproxy) et de calcul d'itinéraires (Valhalla) sont auto-hébergés** et ne transmettent aucune donnée à des tiers. Les polices de caractères sont intégrées au site et à l'application : aucune n'est chargée depuis un service tiers.

### Fonds de carte

Les cartes sont affichées par votre navigateur ou votre téléphone, qui télécharge les images de carte directement auprès du fournisseur du fond choisi. Comme pour toute page web, ce fournisseur reçoit alors votre **adresse IP** et la **zone de carte affichée** ; il ne reçoit ni votre compte, ni votre nom, ni vos itinéraires. Ces fournisseurs agissent en responsables de traitement indépendants, selon leurs propres politiques.

| Fond de carte | Fournisseur | Quand |
|---------------|-------------|-------|
| Plan (par défaut) | VersaTiles (tiles.versatiles.org) | Fond par défaut |
| Relief (ombrage) | Mapterhorn (tiles.mapterhorn.com) | Si l'ombrage du relief est activé |
| IGN, Satellite (IGN), IGN SCAN 25 | Institut national de l'information géographique et forestière — Géoplateforme (France) | Si vous choisissez ce fond |
| Satellite (ESRI) | Esri Inc. (États-Unis) | Si vous choisissez ce fond |
| OpenStreetMap | OpenStreetMap Foundation (Royaume-Uni) | Si vous choisissez ce fond |
| CyclOSM | OpenStreetMap France | Si vous choisissez ce fond |
| Michelin | Servi par nos soins (tiles.pedalons.fr) | Si vous choisissez ce fond |

### Nous ne vendons pas vos données

Nous ne vendons, ne louons et ne partageons pas vos données personnelles à des fins commerciales ou publicitaires.

### Autorités

Nous pouvons être amenés à communiquer vos données si la loi l'exige (demande judiciaire, obligation légale).

---

## 5. Transferts internationaux de données

Nos serveurs sont hébergés par **OVHcloud** (OVH SAS, Roubaix, France) et sont situés en France. Vos données restent dans l'Union européenne.

Si vous autorisez les notifications push dans l'application mobile, leur contenu transite par **Firebase Cloud Messaging**, fourni par Google Ireland Limited. Google peut traiter ces données aux États-Unis ; ce transfert est encadré par les clauses contractuelles types de la Commission européenne et par l'adhésion de Google LLC au cadre de protection des données UE–États-Unis (Data Privacy Framework). Sur iPhone, les notifications sont remises par le service Apple Push Notification d'Apple, comme pour toute application iOS.

Le fond de carte « Satellite (ESRI) » est servi depuis les États-Unis : votre adresse IP et la zone affichée n'y sont transmises que si vous le choisissez. Le fond OpenStreetMap est servi depuis le Royaume-Uni, qui bénéficie d'une décision d'adéquation de la Commission européenne.

La connexion à des services GPS tiers (Hammerhead, Garmin, Wahoo) implique un transfert de données vers ces services, situés aux États-Unis. Ce transfert repose sur votre consentement explicite lors de la connexion du service.

---

## 6. Conservation des données

| Type de données | Durée de conservation |
|----------------|----------------------|
| Données de compte | Tant que votre compte est actif |
| Sessions de connexion | 30 jours après la dernière utilisation |
| Jetons d'authentification temporaires (OTP, liens magiques) | 5 minutes |
| Codes d'appairage d'appareils | 10 minutes |
| Challenges WebAuthn | 5 minutes |
| Contenu (sorties, posts, itinéraires) | Tant que vous ne le supprimez pas |
| Notifications et suivi de leurs envois | 90 jours, puis suppression automatique |
| Préférences de notification | Tant que votre compte est actif |
| Enregistrement du téléphone pour les notifications push | Jusqu'à votre déconnexion de l'application, la désinstallation de l'application ou la suppression de votre compte |
| Fichiers (images, GPX) | Tant que le contenu associé existe |
| Données après suppression de compte | Effacement immédiat ; disparition des sauvegardes sous 30 jours (voir section 7) |

---

## 7. Vos droits

Conformément au Règlement Général sur la Protection des Données (RGPD), vous disposez des droits suivants :

- **Droit d'accès** : obtenir une copie de vos données personnelles.
- **Droit de rectification** : corriger des données inexactes ou incomplètes.
- **Droit à l'effacement** (« droit à l'oubli ») : demander la suppression de vos données.
- **Droit à la limitation du traitement** : restreindre temporairement l'utilisation de vos données.
- **Droit à la portabilité** : recevoir vos données dans un format structuré et lisible par machine.
- **Droit d'opposition** : vous opposer au traitement fondé sur l'intérêt légitime.
- **Droit de retirer votre consentement** : à tout moment, sans affecter la licéité du traitement antérieur.

### Exporter vos données vous-même

Les droits d'accès et de portabilité s'exercent vous-même, sans nous écrire : depuis le site web, dans **Profil → Vos données**, choisissez « Télécharger mes données ». Nous préparons une archive ZIP et vous envoyons un lien de téléchargement par email. Cette fonction n'est pas encore proposée dans l'application mobile ; le lien reçu par email fonctionne en revanche sur tous vos appareils.

L'archive contient votre profil, vos équipes, vos inscriptions, tout ce que vous avez publié, vos notifications, leurs envois, vos préférences de notification et les téléphones enregistrés pour les notifications push, ainsi que vos fichiers (photo de profil, images envoyées, fichiers GPX et FIT de vos parcours). Les données sont au format JSON, structuré et lisible par machine.

Pour des raisons de sécurité, les éléments d'identification en sont exclus : hachage de votre mot de passe, jetons de session, matériel cryptographique de vos clés d'accès, jeton de votre calendrier, jetons d'accès à vos services GPS connectés et jetons d'enregistrement de vos téléphones pour les notifications push. Leurs métadonnées (dates, appareils, services concernés) sont bien présentes. Le lien de téléchargement expire au bout de **7 jours**, après quoi l'archive est supprimée de nos serveurs. Un export par heure et par compte.

### Supprimer votre compte

Vous pouvez supprimer votre compte vous-même, à tout moment, sans nous écrire :

- **dans l'application mobile** : **Profil → Compte → Zone de danger**, puis « Supprimer le compte » ;
- **sur le site web** : **Profil → Actions du compte → Zone de danger**, puis « Supprimer le compte ».

Si vous n'avez plus accès à votre compte ou à l'application, écrivez-nous depuis l'adresse e-mail de votre compte à **privacy@pedalons.fr** en demandant sa suppression ; nous la traiterons dans un délai de 30 jours.

La suppression est irréversible et immédiate. Dès votre confirmation, votre compte est désactivé et vos données personnelles sont effacées : adresse e-mail, nom, photo de profil, mot de passe et clés d'accès, sessions, préférences, services GPS connectés, appartenance aux équipes, inscriptions aux sorties et voyages à venir, petites annonces et leurs photos, commentaires, notifications, enregistrement de vos téléphones pour les notifications push, exports de données et aperçus GPX.

Ce que vous avez publié pour une équipe (sorties, voyages, parcours, posts et leurs fichiers) appartient à cette équipe et reste en ligne. Ces contenus sont désormais attribués à « Ancien membre » et ne sont plus rattachés à aucune donnée permettant de vous identifier. De même, un commentaire auquel d'autres membres ont répondu est conservé vide, avec la mention « Commentaire supprimé », pour que leurs réponses ne disparaissent pas avec lui. Vos inscriptions aux sorties passées sont conservées sous la même forme anonyme et ne sont plus affichées.

Pour qu'un contenu publié pour une équipe disparaisse, supprimez-le avant de supprimer votre compte, ou demandez-le à un organisateur de l'équipe.

Nos sauvegardes, conservées 30 jours, contiennent encore vos données jusqu'à leur renouvellement ; elles ne servent qu'à rétablir le service après un incident.

### Nous contacter

Pour les autres droits, ou si vous préférez passer par nous, contactez-nous à : **privacy@pedalons.fr**

Nous répondrons à votre demande dans un délai de **30 jours**. Si nous ne pouvons pas donner suite, nous vous expliquerons pourquoi.

Vous pouvez également introduire une réclamation auprès de la **CNIL** (Commission Nationale de l'Informatique et des Libertés) : [www.cnil.fr](https://www.cnil.fr)

---

## 8. Cookies et stockage local

Pedalons utilise un nombre minimal de cookies et de données de stockage local :

| Élément | Type | Finalité | Durée |
|---------|------|----------|-------|
| refresh_token | Cookie HttpOnly | Maintenir votre session authentifiée | 30 jours |
| i18nextLng | localStorage | Mémoriser votre préférence de langue | Persistant |
| Préférences d'unités | localStorage | Mémoriser votre système d'unités | Persistant |
| Style de carte | localStorage | Mémoriser vos préférences d'affichage de carte | Persistant |

**Nous n'utilisons aucun cookie de suivi, d'analyse ou de publicité.** Aucun consentement aux cookies n'est donc requis au-delà du cookie de session, qui est strictement nécessaire au fonctionnement du service.

---

## 9. Sécurité

Nous mettons en oeuvre les mesures suivantes pour protéger vos données :

- **Chiffrement en transit** : toutes les communications utilisent HTTPS (TLS).
- **Chiffrement au repos** : les jetons OAuth des services GPS sont chiffrés en AES-256-GCM.
- **Hachage des secrets** : les jetons de session et d'authentification sont stockés sous forme de hachages irréversibles.
- **Cookies sécurisés** : HttpOnly, Secure, SameSite=Strict.
- **Isolation multi-tenant** : les données de chaque domaine sont strictement isolées au niveau de la base de données.
- **Limitation de débit** : protection contre les tentatives de connexion par force brute.
- **Suppression logique** : les données supprimées sont d'abord désactivées avant suppression définitive.

Aucun système n'est infaillible. Si vous constatez une activité suspecte sur votre compte, contactez-nous immédiatement.

---

## 10. Mineurs

Pedalons n'est pas destiné aux enfants de moins de 16 ans. Nous ne collectons pas sciemment de données personnelles de mineurs de moins de 16 ans. Si vous êtes parent et pensez que votre enfant nous a fourni des données, contactez-nous pour que nous les supprimions.

---

## 11. Modifications de cette politique

Nous pouvons mettre à jour cette politique pour refléter des changements dans nos pratiques ou dans la réglementation. En cas de modification substantielle :

- Nous publierons la version mise à jour sur cette page.
- Nous mettrons à jour la date de « dernière mise à jour » en haut de ce document.
- Pour les changements importants, nous vous informerons par e-mail ou par notification dans l'application.

---

## 12. Responsable du traitement

Le responsable du traitement de vos données personnelles est :

- **LANDAIS Gabriel** (entreprise individuelle)
- **Adresse** : 29 rue Docteur Jean Rostand, 44800 Saint-Herblain, France
- **SIRET** : 897 872 958 00011

### Délégué à la protection des données (DPO)

Le délégué à la protection des données est **Gabriel Landais**. Vous pouvez le contacter à l'adresse : **privacy@pedalons.fr**

## 13. Contact

Pour toute question relative à cette politique ou à vos données personnelles :

- **E-mail** : privacy@pedalons.fr
- **Délai de réponse** : 30 jours maximum

---
