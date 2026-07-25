# Politique de confidentialité

**Dernière mise à jour : 2 avril 2026**

La présente politique de confidentialité décrit la manière dont Pedalons (« nous », « notre », « nos ») collecte, utilise et protège vos données personnelles lorsque vous utilisez notre plateforme (site web, application mobile, extensions pour appareils GPS).

Pour toute question relative à vos données personnelles, vous pouvez nous contacter à l'adresse : **privacy@pedalons.fr**

---

## 1. Données que nous collectons

### Données de compte

Lors de la création de votre compte, nous collectons :

- **Adresse e-mail** : pour l'authentification et les communications liées au service
- **Nom d'affichage** : choisi par vous, visible par les membres de votre équipe
- **Photo de profil** (facultatif) : image que vous téléchargez pour personnaliser votre profil
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

**Important** : nous ne suivons pas votre position en temps réel. Les données GPS proviennent exclusivement de fichiers que vous importez volontairement.

### Contenu que vous créez

- **Sorties (rides)** : titre, description, date, groupes de niveau, itinéraire associé.
- **Publications (posts)** : texte au format Markdown.
- **Commentaires** : texte associé à une publication.
- **Itinéraires (routes)** : nom, distance, dénivelé, type de surface, traces et points GPS.
- **Photos et images** : fichiers que vous téléchargez pour illustrer vos contenus.

### Données de connexion à des services GPS tiers

Si vous connectez un service GPS externe (Hammerhead, Garmin) :

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
| Sécuriser votre compte (détection de sessions suspectes) | Intérêt légitime |
| Synchroniser vos itinéraires avec des appareils GPS connectés | Consentement (connexion volontaire) |
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
| Brevo (Sendinblue SAS, France) | Envoi d'e-mails transactionnels | Adresse e-mail |

**Tous nos services de traitement d'images (imgproxy) et de calcul d'itinéraires (Valhalla) sont auto-hébergés** et ne transmettent aucune donnée à des tiers.

### Nous ne vendons pas vos données

Nous ne vendons, ne louons et ne partageons pas vos données personnelles à des fins commerciales ou publicitaires.

### Autorités

Nous pouvons être amenés à communiquer vos données si la loi l'exige (demande judiciaire, obligation légale).

---

## 5. Transferts internationaux de données

Nos serveurs sont hébergés par **OVHcloud** (OVH SAS, Roubaix, France) et sont situés en France. Vos données restent dans l'Union européenne.

La connexion à des services GPS tiers (Hammerhead, Garmin) implique un transfert de données vers ces services, situés aux États-Unis. Ce transfert repose sur votre consentement explicite lors de la connexion du service.

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
| Fichiers (images, GPX) | Tant que le contenu associé existe |
| Données après suppression de compte | Suppression logique immédiate, suppression définitive sous 30 jours |

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

L'archive contient votre profil, vos équipes, vos inscriptions, tout ce que vous avez publié, ainsi que vos fichiers (photo de profil, images envoyées, fichiers GPX et FIT de vos parcours). Les données sont au format JSON, structuré et lisible par machine.

Pour des raisons de sécurité, les éléments d'identification en sont exclus : hachage de votre mot de passe, jetons de session, matériel cryptographique de vos clés d'accès, jeton de votre calendrier et jetons d'accès à vos services GPS connectés. Leurs métadonnées (dates, appareils, services concernés) sont bien présentes. Le lien de téléchargement expire au bout de **7 jours**, après quoi l'archive est supprimée de nos serveurs. Un export par heure et par compte.

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
