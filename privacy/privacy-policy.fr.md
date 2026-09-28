# Politique de confidentialité

**Dernière mise à jour : 29 septembre 2026**

La présente politique de confidentialité décrit la manière dont Pedalons (« nous », « notre », « nos ») collecte, utilise et protège vos données personnelles lorsque vous utilisez notre plateforme (site web, application mobile, extensions pour appareils GPS).

Pour toute question relative à vos données personnelles, vous pouvez nous contacter à l'adresse : **privacy@pedalons.fr**

---

## 1. Données que nous collectons

### Données de compte

Lors de la création de votre compte, nous collectons :

- **Adresse e-mail** : pour l'authentification et les communications liées au service
- **Nom d'affichage** : choisi par vous, visible par les membres de votre équipe
- **Photo de profil** (facultatif) : image que vous téléchargez pour personnaliser votre profil. Dans l'application mobile, vous la choisissez dans votre photothèque par le sélecteur du système : l'application ne reçoit que la photo choisie, et n'accède ni à l'appareil photo ni au reste de votre photothèque.
- **Préférences** : système d'unités (métrique/impérial), langue, thème (clair/sombre), fuseau horaire, et si les membres de vos équipes peuvent vous écrire au sujet de vos petites annonces
- **Acceptation des conditions d'utilisation** : la date à laquelle vous les avez acceptées à l'inscription, conservée comme preuve de cette acceptation

### Données d'authentification

Pour sécuriser l'accès à votre compte, nous traitons :

- **Mot de passe** : conservé uniquement sous forme de hachage irréversible (bcrypt), jamais en clair.
- **Clés d'accès (passkeys/WebAuthn)** : identifiant de clé, clé publique et compteur de signatures. La clé privée reste sur votre appareil et ne nous est jamais transmise.
- **Jetons de session** : un jeton de rafraîchissement (haché, jamais stocké en clair) est conservé dans un cookie sécurisé HttpOnly pendant 30 jours maximum.
- **Codes à usage unique (OTP)** : hachés côté serveur, valides 5 minutes.
- **Liens de vérification d'adresse e-mail et de réinitialisation du mot de passe** : à usage unique, hachés côté serveur, valides respectivement 24 heures et 1 heure.
- **Jeton de calendrier** : une clé secrète incluse dans l'adresse de votre flux de calendrier, pour que votre application d'agenda affiche vos sorties. Vous pouvez la régénérer à tout moment, ce qui désactive l'ancienne adresse.
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
- **Petites annonces** : titre, description, type, prix, photos, une description du lieu et une position. La position exacte ne sert qu'à modifier votre annonce : les autres membres ne voient jamais qu'une position floutée à environ 1 km.
- **Messages à l'auteur d'une annonce** : envoyés par e-mail via nos serveurs, avec votre adresse e-mail comme adresse de réponse, pour que l'auteur puisse vous répondre directement. Nous ne conservons pas le message lui-même ; nous enregistrons seulement que vous avez écrit au sujet de cette annonce, et quand, pour limiter les abus.

### Notifications

Pour vous prévenir de ce qui se passe dans vos équipes (nouvelle sortie, commentaire, changement d'une sortie à laquelle vous êtes inscrit…), nous traitons :

- **Vos notifications** : le type d'évènement, l'élément concerné, le texte affiché et la date à laquelle vous l'avez lue. Elles apparaissent dans la page Notifications du site et de l'application.
- **Vos préférences de notification** : pour chaque type de notification, les canaux par lesquels vous acceptez d'être prévenu (e-mail, notification push sur votre téléphone ou dans votre navigateur).
- **Le suivi des envois** : pour chaque notification envoyée par e-mail ou en notification push, le canal, l'état de l'envoi et sa date.
- **L'enregistrement de votre téléphone ou de votre navigateur** (seulement si vous autorisez les notifications) : un jeton d'enregistrement délivré par Firebase Cloud Messaging (service de Google). Dans l'application mobile, il est accompagné du système (Android ou iOS), du modèle de l'appareil et de la version de l'application ; sur le site, du nom du navigateur et du système (par exemple « Firefox · Android »). Ce jeton identifie l'installation de l'application ou le navigateur, pas votre personne ; il nous sert uniquement à adresser les notifications à ce téléphone ou à ce navigateur.

L'application et le site ne demandent l'autorisation d'afficher des notifications que lorsque vous le choisissez, jamais à leur lancement. Vous pouvez la retirer à tout moment dans les réglages de votre téléphone ou de votre navigateur, couper les notifications sur le site, ou couper un type de notification depuis vos préférences de notification. Le jeton est supprimé de nos serveurs lorsque vous vous déconnectez de l'application ou du site, lorsque vous coupez les notifications sur le site, lorsque Google nous signale qu'il n'est plus valide (application désinstallée, par exemple) et lorsque vous supprimez votre compte.

### Signalements et blocages

Pour permettre la modération (voir la section 6 des conditions d'utilisation), nous enregistrons :

- **Vos signalements** : qui a signalé (vous), ce qui est signalé (le contenu ou le membre, et l'équipe concernée), l'auteur du contenu ou le membre visé, le motif choisi, le message facultatif que vous ajoutez, une **copie du texte signalé** (1 000 caractères au plus) et la décision prise (contenu supprimé ou signalement rejeté, par qui et quand). La copie permet de vérifier la décision même si le contenu a été modifié ou supprimé depuis.
- **Vos blocages** : qui a bloqué qui, et depuis quand.

Qui y a accès :

- **Votre identité de signaleur** n'est visible **que par l'équipe Pedalons**. Elle n'est jamais montrée aux organisateurs de l'équipe, ni à l'auteur du contenu, ni au membre signalé, et la notification envoyée aux modérateurs n'en contient rien.
- **Les organisateurs et administrateurs de l'équipe** voient le contenu signalé, son auteur, les motifs et les messages ajoutés, mais pas qui a signalé. Un message peut vous identifier par ce qu'il dit : écrivez-le en sachant qu'ils le liront. Un organisateur visé par un signalement ne le voit pas.
- **Vos blocages** ne sont visibles que par vous. La personne bloquée n'en est pas informée, et rien dans le service ne le lui révèle.

La notification qui prévient les modérateurs d'un signalement ne contient que le nom de l'équipe, jamais le contenu signalé.

**Filtre de publication** : au moment où vous publiez, votre texte est comparé à une courte liste de termes injurieux ou haineux. S'il en contient un, la publication est refusée et le texte n'est pas enregistré ; ce refus n'a aucune autre conséquence pour votre compte.

### Signalements de problèmes et rapports d'erreur

Pour corriger les bugs, nous recevons :

- **Vos signalements de problème ou suggestions** (« Signaler un problème », dans le site ou l'application) : le texte que vous écrivez et, si vous laissez cochée la case « Joindre les informations techniques », la page ou l'écran affiché, la version de l'application, le système et le modèle de l'appareil ou le navigateur, la langue, le fuseau horaire, et un journal de vos dernières actions dans l'application (pages visitées, requêtes en échec, erreurs). Le formulaire vous montre exactement ce qui sera envoyé.
- **Des rapports d'erreur automatiques** (connecté uniquement) : quand l'application rencontre une erreur inattendue, elle envoie la description technique de l'erreur, les mêmes informations techniques et le journal de vos dernières actions. Vous pouvez désactiver cet envoi dans **Profil → Préférences**.

Le journal ne contient ni mot de passe, ni contenu de formulaire, ni paramètre d'adresse web ; les jetons de connexion et les adresses e-mail qui s'y trouveraient sont masqués par nos serveurs avant tout enregistrement. Ces informations sont transmises à l'équipe Pedalons sous forme de tickets dans un dépôt **privé** GitHub (voir section 4), où vous êtes désigné par un identifiant technique, jamais par votre nom ni votre adresse e-mail.

### Données de connexion à des services GPS tiers

Si vous connectez un service GPS externe (Hammerhead, Garmin, Wahoo) :

- **Jetons d'accès OAuth** : chiffrés en AES-256-GCM avant stockage. Nous ne stockons jamais vos identifiants (nom d'utilisateur/mot de passe) de ces services.
- **Identifiant utilisateur externe** : fourni par le service tiers pour faire le lien avec votre compte Pedalons.

### Données stockées localement sur votre appareil

Dans votre navigateur web ou application mobile :

- **Préférence de langue** : dans un cookie sur le site (seulement une fois que vous avez choisi une langue), dans le stockage de l'application sur mobile
- **Système d'unités** et **thème** : dans le stockage local
- **Préférences de carte** : style de carte choisi, relief et affichage 3D, dans le stockage local
- **Notifications push** (site) : le jeton de ce navigateur, pour pouvoir le désinscrire lorsque vous vous déconnectez ou coupez les notifications
- **Invitation en attente** : une invitation à une équipe que vous avez ouverte, gardée pour l'onglet en cours le temps de vous connecter ou de vous inscrire
- **Journal des dernières actions** : en mémoire dans le navigateur, et dans un fichier de l'application mobile (200 entrées au plus), pour un éventuel signalement de problème ; il ne quitte votre appareil que dans les cas décrits plus haut
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
| Vous notifier sur votre téléphone ou dans votre navigateur (notifications push) | Consentement (autorisation donnée sur le téléphone ou dans le navigateur) |
| Modérer les contenus : traiter les signalements, appliquer vos blocages, filtrer les termes injurieux à la publication | Exécution du contrat (conditions d'utilisation) et intérêt légitime (protéger les membres) |
| Conserver la preuve de votre acceptation des conditions d'utilisation | Intérêt légitime |
| Sécuriser votre compte (détection de sessions suspectes) | Intérêt légitime |
| Synchroniser vos itinéraires avec des appareils GPS connectés | Consentement (connexion volontaire) |
| Trier les parcours par distance (filtre « autour de moi » de l'application) | Consentement (autorisation de localisation donnée sur le téléphone) |
| Afficher les cartes | Intérêt légitime |
| Traiter vos signalements de problème et corriger les erreurs de l'application | Intérêt légitime (fiabilité du service ; rapports automatiques désactivables) |
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
- **Petites annonces** : les membres voient la position floutée de votre annonce, jamais sa position exacte, et jamais votre adresse e-mail. Lorsque vous écrivez à l'auteur d'une annonce, il reçoit votre adresse e-mail, puisqu'il vous répond à cette adresse.

### Sous-traitants techniques

Nous faisons appel à des services techniques pour le fonctionnement de la plateforme :

| Service | Rôle | Données concernées |
|---------|------|-------------------|
| OVHcloud (OVH SAS, France) | Hébergement de l'application, de la base de données et du stockage objet | Toutes les données |
| Scaleway (Scaleway SAS, France) | Envoi d'e-mails transactionnels et des notifications par e-mail (Transactional Email) | Adresse e-mail, nom d'affichage, contenu de l'e-mail (dont le titre et le texte des notifications, et les messages envoyés à l'auteur d'une annonce) |
| GitHub (GitHub, Inc., États-Unis) | Suivi des signalements de problème et des rapports d'erreur, dans un dépôt privé accessible à la seule équipe Pedalons | Texte du signalement, informations techniques, journal des dernières actions, identifiant technique du compte et domaine |
| Google Firebase Cloud Messaging (Google Ireland Limited, Irlande) | Acheminement des notifications push vers l'application mobile, via Apple Push Notification service pour les iPhone, et vers votre navigateur | Jeton d'enregistrement du téléphone ou du navigateur, titre et texte de chaque notification, identifiant technique permettant d'ouvrir le bon écran au toucher |

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

Si vous autorisez les notifications push dans l'application mobile ou sur le site, leur contenu transite par **Firebase Cloud Messaging**, fourni par Google Ireland Limited. Google peut traiter ces données aux États-Unis ; ce transfert est encadré par les clauses contractuelles types de la Commission européenne et par l'adhésion de Google LLC au cadre de protection des données UE–États-Unis (Data Privacy Framework). Sur iPhone, les notifications sont remises par le service Apple Push Notification d'Apple, comme pour toute application iOS ; dans un navigateur, par le service push propre au navigateur, comme pour tout site qui envoie des notifications.

Le fond de carte « Satellite (ESRI) » est servi depuis les États-Unis : votre adresse IP et la zone affichée n'y sont transmises que si vous le choisissez. Le fond OpenStreetMap est servi depuis le Royaume-Uni, qui bénéficie d'une décision d'adéquation de la Commission européenne.

Les signalements de problème et rapports d'erreur sont transmis à **GitHub** (GitHub, Inc.), aux États-Unis ; ce transfert est encadré par les clauses contractuelles types de la Commission européenne. Ils ne contiennent ni votre nom ni votre adresse e-mail.

La connexion à des services GPS tiers (Hammerhead, Garmin, Wahoo) implique un transfert de données vers ces services, situés aux États-Unis. Ce transfert repose sur votre consentement explicite lors de la connexion du service.

---

## 6. Conservation des données

| Type de données | Durée de conservation |
|----------------|----------------------|
| Données de compte | Tant que votre compte est actif |
| Sessions de connexion | 30 jours après la dernière utilisation |
| Jetons d'authentification temporaires (OTP) | 5 minutes |
| Liens de vérification d'adresse e-mail et de réinitialisation du mot de passe | 24 heures et 1 heure |
| Codes d'appairage d'appareils | 10 minutes |
| Challenges WebAuthn | 5 minutes |
| Contenu (sorties, posts, itinéraires) | Tant que vous ne le supprimez pas |
| Notifications et suivi de leurs envois | 90 jours, puis suppression automatique |
| Préférences de notification | Tant que votre compte est actif |
| Enregistrement du téléphone ou du navigateur pour les notifications push | Jusqu'à votre déconnexion de l'application ou du site, la coupure des notifications sur le site, la désinstallation de l'application ou la suppression de votre compte |
| Fichiers (images, GPX) | Tant que le contenu associé existe |
| Date d'acceptation des conditions d'utilisation | Tant que votre compte est actif |
| Blocages | Jusqu'à ce que vous débloquiez la personne, ou jusqu'à la suppression du compte de l'un de vous deux |
| Signalements, copie du texte signalé et décision | Tant que le compte de la personne visée existe, pour garder la trace des décisions de modération ; à la suppression du compte du signaleur, conservés sans lien avec lui (voir section 7) |
| Signalements de problème et suggestions | 1 an sur nos serveurs, ou jusqu'à la suppression de votre compte ; le ticket GitHub correspondant est conservé tant qu'il est utile au suivi du bug |
| Rapports d'erreur automatiques | 90 jours sur nos serveurs, ou jusqu'à la suppression de votre compte ; le ticket GitHub, commun à tous les membres touchés par la même erreur, est conservé tant qu'il est utile |
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

Les droits d'accès et de portabilité s'exercent vous-même, sans nous écrire : depuis le site web, dans **Profil → Vos données**, choisissez « Télécharger mes données ». Nous préparons une archive ZIP et vous envoyons un lien de téléchargement par email. Dans l'application mobile, la même demande se trouve dans **Profil → Vos données**, « Demander un export ». Le lien reçu par email fonctionne sur tous vos appareils.

L'archive contient votre profil, vos équipes, vos inscriptions, tout ce que vous avez publié, vos notifications, leurs envois, vos préférences de notification, les téléphones et navigateurs enregistrés pour les notifications push, les membres que vous avez bloqués et les signalements que vous avez faits (sans la copie du texte signalé, qui est le contenu de quelqu'un d'autre), ainsi que vos fichiers (photo de profil, images envoyées, fichiers GPX et FIT de vos parcours). Les données sont au format JSON, structuré et lisible par machine.

Pour des raisons de sécurité, les éléments d'identification en sont exclus : hachage de votre mot de passe, jetons de session, matériel cryptographique de vos clés d'accès, jeton de votre calendrier, jetons d'accès à vos services GPS connectés et jetons d'enregistrement de vos téléphones et navigateurs pour les notifications push. Leurs métadonnées (dates, appareils, services concernés) sont bien présentes. Le lien de téléchargement expire au bout de **7 jours**, après quoi l'archive est supprimée de nos serveurs. Un export par heure et par compte.

### Supprimer votre compte

Vous pouvez supprimer votre compte vous-même, à tout moment, sans nous écrire :

- **dans l'application mobile** : **Profil → Compte → Zone de danger**, puis « Supprimer le compte » ;
- **sur le site web** : **Profil → Actions du compte → Zone de danger**, puis « Supprimer le compte ».

Si vous êtes le seul administrateur d'une équipe qui compte d'autres membres, vous devez d'abord nommer un autre membre administrateur : l'application et le site vous indiquent les équipes concernées et la marche à suivre. Une équipe dont vous êtes le seul membre est supprimée avec votre compte.

Si vous n'avez plus accès à votre compte ou à l'application, écrivez-nous depuis l'adresse e-mail de votre compte à **privacy@pedalons.fr** en demandant sa suppression ; nous la traiterons dans un délai de 30 jours.

La suppression est irréversible et immédiate. Dès votre confirmation, votre compte est désactivé et vos données personnelles sont effacées : adresse e-mail, nom, photo de profil, mot de passe et clés d'accès, sessions, préférences, services GPS connectés, appartenance aux équipes, inscriptions aux sorties et voyages à venir, petites annonces et leurs photos, commentaires, notifications, enregistrement de vos téléphones et navigateurs pour les notifications push, exports de données et aperçus GPX. Les blocages sont supprimés dans les deux sens, ceux que vous aviez faits comme ceux qui vous visaient, et les signalements qui vous visent sont supprimés avec la copie de votre contenu qu'ils contenaient. Les signalements que vous avez faits sont conservés, sans plus aucun lien avec vous, pour que les décisions prises restent vérifiables.

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
| lang | Cookie | Mémoriser la langue que vous avez choisie, pour afficher les pages dans cette langue | 1 an |
| pedalons-unit-system | localStorage | Mémoriser votre système d'unités | Persistant |
| mantine-color-scheme-value | localStorage | Mémoriser votre thème (clair/sombre) | Persistant |
| pedalons-map-style, pedalons-map-terrain3d, pedalons-map-hillshade | localStorage | Mémoriser vos préférences d'affichage de carte | Persistant |
| pedalons-error-reports | localStorage | Mémoriser que vous avez désactivé les rapports d'erreur automatiques | Persistant |
| pedalons.webPush.token | localStorage | Désinscrire ce navigateur des notifications push lorsque vous vous déconnectez ou les coupez | Jusqu'à votre déconnexion ou la coupure des notifications |
| pedalons.installBanner.dismissedAt | localStorage | Mémoriser que vous avez fermé la proposition d'installer le site comme une application | Persistant (la proposition revient après 90 jours) |
| pendingInvitationToken, pendingBiketeamMigrationRequest | sessionStorage | Garder une invitation à une équipe, ou une demande de transfert d'équipe depuis biketeam, le temps de vous connecter | Jusqu'à la fermeture de l'onglet |

**Nous n'utilisons aucun cookie de suivi, d'analyse ou de publicité.** Aucun consentement aux cookies n'est donc requis : le cookie de session est strictement nécessaire au fonctionnement du service, et le cookie lang ne fait que mémoriser un choix que vous avez fait.

---

## 9. Sécurité

Nous mettons en oeuvre les mesures suivantes pour protéger vos données :

- **Chiffrement en transit** : toutes les communications utilisent HTTPS (TLS).
- **Chiffrement au repos** : les jetons OAuth des services GPS sont chiffrés en AES-256-GCM.
- **Hachage des secrets** : les jetons de session et d'authentification sont stockés sous forme de hachages irréversibles.
- **Cookies sécurisés** : HttpOnly, Secure, SameSite=Lax, complétés par un contrôle qui rejette les requêtes venant d'autres sites.
- **Isolation multi-tenant** : les données de chaque domaine sont strictement isolées au niveau de la base de données.
- **Limitation de débit** : protection contre les tentatives de connexion par force brute.
- **Suppression effective** : ce que vous supprimez, compte compris, est effacé de notre base de données, et non simplement masqué ; il ne subsiste que dans nos sauvegardes, 30 jours au plus.

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
