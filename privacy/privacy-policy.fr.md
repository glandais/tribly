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
- **Préférences** : système d'unités (métrique/impérial), thème d'affichage (clair, sombre ou celui du système), langue, fuseau horaire, le fait que d'autres membres puissent ou non vous contacter au sujet de vos petites annonces, et vos réglages de notifications. Le système d'unités, le thème, la langue et le fuseau horaire ne sont enregistrés dans votre compte qu'une fois que vous les avez choisis (unités, thème et langue sur le site ou dans l'application, fuseau horaire sur le site) ; une préférence enregistrée vous suit ensuite sur tous vos appareils, et tant que vous n'en avez pas choisi, c'est le réglage de votre navigateur ou de votre appareil qui s'applique. Les messages au sujet de vos annonces sont autorisés, sauf si vous les désactivez dans votre profil. Nos e-mails sont envoyés en français ou en anglais : les codes de connexion et les e-mails de vérification d'adresse ou de réinitialisation de mot de passe sont rédigés dans la langue du site ou de l'application depuis lesquels vous les avez demandés ; les e-mails déclenchés par quelqu'un d'autre (notifications, messages au sujet de vos petites annonces, invitations dans une équipe) sont rédigés dans la langue enregistrée dans votre compte — si vous n'en avez pas choisi, les notifications sont en français et les deux autres dans la langue de l'expéditeur. Les dates et heures des notifications sont affichées dans votre fuseau horaire enregistré, ou à l'heure de Paris si vous n'en avez pas choisi.
- **Acceptation des conditions d'utilisation** : la date à laquelle vous les avez acceptées à l'inscription, conservée comme preuve de cette acceptation
- **Données d'état du compte** : le fait que votre adresse e-mail ait été vérifiée et la date de cette vérification, les dates de création et de dernière modification du compte, le site (domaine) auquel votre compte est rattaché, et — pour les rares comptes qui administrent la plateforme — un indicateur de rôle d'administrateur.
- **Appartenances aux équipes** : les équipes dont vous êtes membre, votre rôle dans chacune (membre, organisateur, administrateur) et la date à laquelle vous les avez rejointes. Les administrateurs d'une équipe voient la liste de ses membres (nom d'affichage, photo de profil, rôle, date d'arrivée) et peuvent y chercher par adresse e-mail. Les organisateurs voient les noms d'affichage et les photos des membres, pour pouvoir choisir un meneur de groupe. Ce n'est que si les administrateurs d'une équipe ouvrent le trombinoscope (fermé par défaut) que chaque membre de l'équipe peut voir la liste, avec le rôle et la date d'arrivée de chacun. La liste des membres ne montre jamais les adresses e-mail aux autres membres.

### Comptes et équipes venus de Biketeam

Certaines équipes ont migré vers Pedalons depuis la plateforme Biketeam.

**Imports antérieurs.** Pour les premières équipes arrivées, nous avons importé les données des membres depuis la base de données de la plateforme précédente : adresse e-mail, le fait que cette adresse y avait été vérifiée, prénom et nom (utilisés comme nom d'affichage), statut d'administrateur, appartenances aux équipes et rôles, inscriptions aux sorties et voyages, commentaires, ainsi que l'identifiant de compte Strava, Facebook ou Google utilisé auparavant pour se connecter — avec les itinéraires, sorties, voyages, publications et images de l'équipe. Lorsque votre adresse e-mail avait été vérifiée sur la plateforme précédente, ou était liée à une connexion Google ou Facebook, nous avons aussi importé votre mot de passe sous la forme de son hachage bcrypt existant, pour que vous puissiez continuer à vous connecter avec le même mot de passe. Lorsque la plateforme précédente ne détenait pas d'adresse e-mail, nous avons généré une adresse technique factice à partir de cet identifiant (de la forme `strava_…`, `facebook_…` ou `google_…@pedalons.fr`), qui ne peut pas recevoir de courrier, afin de préserver vos appartenances, inscriptions et commentaires. Lorsque la plateforme précédente détenait deux comptes dont les adresses e-mail ne différaient que par la casse, et que l'un d'eux n'avait plus aucun moyen de connexion, son historique (appartenances, inscriptions, commentaires) a été fusionné dans le compte qui a conservé l'adresse. Ces imports sont terminés : les comptes qu'ils ont créés restent en place, mais plus aucune donnée de membre n'est importée.

**Les comptes portant une adresse factice ne peuvent plus se connecter** : la connexion avec Strava a été retirée de Pedalons, qui ne propose pas non plus de connexion Facebook ou Google, et aucun e-mail ne peut atteindre une telle adresse. Si vous aviez l'un de ces comptes, écrivez à privacy@pedalons.fr : nous pouvons y rattacher une adresse e-mail réelle ou le supprimer. De même, si votre compte a été créé par un import et que vous n'en voulez pas, contactez-nous et nous le supprimerons.

**Transférer une équipe aujourd'hui.** Un administrateur d'équipe Biketeam peut désormais transférer lui-même son équipe : il lance le transfert depuis Biketeam, puis se connecte (ou s'inscrit) sur Pedalons et le confirme. Notre serveur récupère alors le contenu de l'équipe directement auprès du serveur de Biketeam : le nom et les réglages de l'équipe, sa page « à propos » (y compris les coordonnées de contact qu'elle affiche), sa page FAQ et son logo, les lieux, les itinéraires avec leurs fichiers GPX, les modèles de sortie, les publications, les sorties, les voyages et leurs images. **Aucune donnée de membre n'est transférée** : ni comptes, ni appartenances, ni inscriptions, ni commentaires. Le compte Pedalons qui a confirmé le transfert devient administrateur de l'équipe et apparaît comme l'auteur de tout ce qui est importé ; les membres rejoignent ensuite l'équipe par le lien d'invitation habituel. Pour chaque demande de transfert, nous enregistrons le compte qui l'a confirmée, l'équipe Biketeam concernée, les dates et le résultat.

### Données d'authentification

Pour sécuriser l'accès à votre compte, nous traitons :

- **Mot de passe** (si vous choisissez d'en définir un) : nous ne stockons jamais votre mot de passe lui-même. Il n'est conservé que sous forme de hachage bcrypt irréversible, utilisé pour vérifier votre connexion et remplacé à chaque réinitialisation. Nous ne vous l'envoyons jamais par e-mail.
- **Clés d'accès (passkeys/WebAuthn)** : identifiant de clé, clé publique, compteur de signatures, les types de transport pris en charge par votre authentificateur (USB, NFC, Bluetooth, interne), un identifiant de modèle d'authentificateur (AAGUID) indiquant le type de clé de sécurité ou d'authentificateur de plateforme utilisé, un libellé d'appareil que vous pouvez choisir lors de l'enregistrement, et les dates de création et de dernière utilisation de la clé. La clé privée reste sur votre appareil et ne nous est jamais transmise.
- **Jetons de session** : un jeton de rafraîchissement (haché, jamais stocké en clair) est conservé dans un cookie sécurisé HttpOnly (site web) ou dans le stockage sécurisé de votre appareil (application mobile) pendant 30 jours maximum. Les sessions créées pour un compteur GPS connecté (Karoo, Garmin) durent jusqu'à 90 jours, car ces appareils ne sont pas connectés en permanence.
- **Codes à usage unique (OTP)** : hachés côté serveur, valides 5 minutes, et invalidés après 5 tentatives erronées.
- **Liens de vérification d'e-mail** : lors de votre inscription, nous stockons votre adresse e-mail, le nom d'affichage choisi, le hachage bcrypt de votre mot de passe et l'heure à laquelle vous avez accepté les conditions d'utilisation, avec un jeton de vérification haché, à usage unique, valide 24 heures. Votre compte n'est créé que lorsque vous cliquez sur le lien.
- **Liens de réinitialisation de mot de passe** : un jeton haché, à usage unique, valide 1 heure, lié à votre adresse e-mail.
- **Vérification de changement d'e-mail** : lorsque vous changez l'adresse e-mail de votre compte, nous stockons la nouvelle adresse avec un jeton de vérification haché, à usage unique, valide 24 heures ; l'adresse n'est appliquée à votre compte qu'une fois le lien suivi.
- **Codes d'appairage d'appareils GPS** : codes temporaires (10 minutes) pour connecter des appareils Karoo ou Garmin.
- **Jeton de calendrier** : si vous utilisez l'abonnement calendrier, nous générons un jeton aléatoire pour votre compte. Il est intégré à l'adresse du flux, n'expire pas, et c'est lui qui vous identifie lorsqu'une application de calendrier récupère votre flux. Comme votre application de calendrier doit pouvoir l'envoyer à chaque requête, ce jeton est stocké en clair sur nos serveurs ; quiconque détient l'adresse du flux peut lire votre calendrier de sorties. Vous pouvez le régénérer à tout moment depuis vos réglages de calendrier, ce qui invalide immédiatement l'ancienne adresse.

### Données de session

À chaque connexion, nous enregistrons :

- **Adresse IP** et **agent utilisateur** (type de navigateur/appareil) : pour la sécurité du compte et la détection d'activité suspecte.
- **Date de dernière connexion** et **dernière utilisation de session**.
- **Journaux serveur** : nos serveurs conservent des journaux techniques des requêtes et des événements d'authentification (date et heure, adresse IP, type de navigateur/appareil, adresse demandée et code de réponse). Ils peuvent contenir votre adresse IP, votre adresse e-mail et des identifiants techniques, et servent uniquement à la sécurité, à la détection d'abus et au dépannage — jamais au profilage ni à la publicité. Comme l'application Garmin, et l'application mobile lorsque vous utilisez « Autour de moi », transmettent vos coordonnées dans l'adresse demandée (voir ci-dessous), ces coordonnées apparaissent aussi dans ces journaux.

### Données de localisation et GPS

Lorsque vous créez ou consultez des itinéraires :

- **Traces GPS** : coordonnées géographiques (latitude, longitude, altitude) issues soit de fichiers GPX que vous importez, soit d'itinéraires que vous dessinez vous-même avec notre planificateur (lorsque votre site ou votre équipe l'a activé ; il est désactivé par défaut) — dans ce cas, les points que vous placez sur la carte sont envoyés à notre moteur de calcul d'itinéraires auto-hébergé et stockés comme une trace.
- **Données contenues dans les fichiers que vous importez** : nous stockons le fichier GPX que vous téléversez tel quel, en plus de la trace simplifiée que nous en calculons. Si le fichier a été enregistré par un appareil GPS ou une application de suivi d'activité plutôt que créé comme un parcours, il peut aussi contenir la date et l'heure de chaque point enregistré et des mesures de capteurs telles que fréquence cardiaque, cadence, puissance et température. Nous n'affichons, n'analysons ni n'utilisons ces informations — les traces affichées sur notre site et dans nos applications ne contiennent que la position, l'altitude et la distance — mais elles restent dans le fichier stocké et dans les fichiers que nous en générons pour le téléchargement. La fréquence cardiaque est une donnée de santé : si vous ne souhaitez pas nous la confier, exportez votre fichier comme parcours plutôt que comme activité, ou retirez ces champs avant l'import. Vous pouvez supprimer un fichier importé à tout moment en supprimant l'itinéraire ou le fichier analysé auquel il appartient.
- **Points d'intérêt** : noms et coordonnées des lieux que vous ajoutez.
- **Coordonnées de l'équipe** (facultatif) : point géographique représentant la localisation de votre équipe.
- **Lieux d'annonces et de rendez-vous** : l'adresse postale ou le point sur la carte que vous attachez à une petite annonce ou à un lieu de rendez-vous d'équipe. Le point sur la carte d'une annonce n'est montré aux autres membres que sous forme de zone approximative (voir « Petites annonces » ci-dessous). Pour le lieu en texte libre d'une annonce et pour les lieux de rendez-vous, choisissez un point de repère proche plutôt que votre domicile si vous ne voulez pas que votre adresse soit visible par votre équipe.

Nous ne suivons pas vos déplacements en continu et ne conservons jamais d'historique de localisation. Deux exceptions existent :

- **Application Garmin Edge** : si vous l'installez, elle lit la position GPS actuelle de votre appareil chaque fois que son écran principal s'affiche, au chargement de vos itinéraires, et l'envoie à notre serveur dans le seul but de trier les itinéraires de votre équipe par distance par rapport à vous. Cela nécessite la permission Garmin « Positionnement », que vous accordez à l'installation.
- **Application iOS/Android** : l'application peut vous demander votre position approximative, mais uniquement si vous touchez « Autour de moi » dans la liste des itinéraires. Elle lit alors votre position une seule fois, en basse précision : sur Android, elle ne demande que la permission de localisation approximative ; sur iOS, uniquement l'accès pendant l'utilisation de l'application, jamais en arrière-plan. Tant que ce filtre est actif, la position est envoyée avec les requêtes de liste des itinéraires, pour filtrer et trier les itinéraires de votre équipe selon leur distance par rapport à vous. L'application ne l'enregistre pas sur votre téléphone.

Dans les deux cas, ces coordonnées ne servent qu'à ce tri et à ce filtrage et ne sont stockées ni dans votre compte ni dans notre base de données ; elles apparaissent en revanche dans nos journaux serveur (voir « Données de session »). Toutes les autres données GPS proviennent d'itinéraires que vous importez en GPX ou dessinez dans le planificateur. Notre site web et notre extension Hammerhead Karoo n'accèdent pas à la localisation de votre appareil.

### Outil d'analyse GPX

Notre outil GPX permet d'analyser un fichier GPX, ou un itinéraire que vous dessinez, sans le rattacher à une équipe. Son utilisation nécessite un compte. Pour chaque fichier analysé, nous stockons : le fichier GPX téléversé, une version simplifiée, un export FIT, une image de carte de la trace, les statistiques calculées (distance, dénivelés, vallonnement), le nom de la trace, et un lien vers votre compte (pour vous montrer votre historique et vous permettre de supprimer ou modifier vos fichiers). Ces fichiers sont conservés **30 jours** puis supprimés automatiquement, avec les fichiers stockés ; vous pouvez les supprimer plus tôt à tout moment. La liste des fichiers analysés au cours des 30 derniers jours est visible sur votre page « Mes fichiers ».

**Quiconque possède le lien peut voir le fichier.** Chaque fichier analysé reçoit une longue adresse aléatoire impossible à deviner. Quiconque détient cette adresse — y compris des personnes non connectées et étrangères à vos équipes — peut voir la trace sur une carte, consulter ses statistiques et la télécharger en GPX ou FIT. Vous seul pouvez la modifier ou la supprimer. Traitez le lien comme le secret : ne le partagez qu'avec les personnes à qui vous voulez donner la trace, et supprimez le fichier si un lien a circulé plus largement que prévu.

### Contenu que vous créez

- **Sorties (rides)** : titre, description, date et heure, groupes de niveau, itinéraire associé, lieux de départ et d'arrivée, statut de publication (brouillon, publié, annulé) et date de publication programmée.
- **Voyages (trips)** : voyages sur plusieurs jours et leurs étapes (titre, description, dates, itinéraires, lieux de départ et d'arrivée).
- **Inscriptions (participations)** : lorsque vous vous inscrivez à une sortie ou à un voyage, nous enregistrons la sortie ou le voyage concerné, le groupe de niveau choisi, et la date et l'heure de votre inscription. Votre nom d'affichage et votre photo de profil apparaissent alors dans la liste des participants, visible par toute personne pouvant voir la sortie ou le voyage — y compris des visiteurs non connectés lorsque la sortie ou le voyage est public. L'annulation de votre inscription supprime l'enregistrement.
- **Publications (posts)** : titre et texte au format Markdown.
- **Commentaires** : texte associé à une publication, une sortie, un voyage ou un itinéraire.
- **Petites annonces** : titre, description, photos, type (vente, achat, location), prix, période de location, et le lieu que vous indiquez (texte libre et/ou point sur la carte). Les annonces ne sont visibles que par les membres connectés de votre équipe, jamais publiquement. Nous stockons exactement le point que vous placez sur la carte, mais seuls vous, les administrateurs de votre équipe et les administrateurs de la plateforme le voient précisément ; les autres membres ne voient qu'une zone d'environ 1 km de côté. Le lieu que vous saisissez en texte est affiché tel que vous l'avez écrit. Les photos que vous joignez peuvent porter leur propre position GPS, plus précise que la zone approximative de l'annonce (voir « Photos et images »).
- **Messages au sujet d'une petite annonce** : lorsque vous utilisez « Contacter le vendeur », nous envoyons votre message (2 000 caractères au maximum) par e-mail à l'auteur de l'annonce, avec votre nom d'affichage, le titre de l'annonce et un lien vers elle. Votre adresse e-mail est indiquée comme adresse de réponse : l'auteur la voit et peut vous répondre directement ; l'adresse de l'auteur ne vous est jamais montrée. Nous ne conservons pas le texte de votre message dans notre base de données : il ne fait que transiter par notre prestataire d'e-mails. Nous enregistrons seulement que vous avez écrit au sujet de cette annonce, et quand, pour limiter les abus (10 messages par heure au maximum). Vous pouvez refuser les messages au sujet de vos propres annonces dans votre profil.
- **Pages d'équipe** : la page « à propos » et les pages supplémentaires publiées par votre équipe.
- **Modèles de sortie** : les modèles réutilisables (titre, description, groupes de niveau) enregistrés par les organisateurs de votre équipe.
- **Lieux** : noms, adresses postales, liens et coordonnées des points de rendez-vous et d'arrivée que vous enregistrez pour votre équipe.
- **Itinéraires (routes)** : nom, distance, dénivelé positif et négatif, vallonnement, type de surface, traces et points GPS. Pour chaque itinéraire et chaque fichier analysé avec l'outil GPX, nous générons et stockons aussi deux fichiers dérivés — un GPX simplifié et un fichier FIT — pour l'envoi vers un appareil GPS.
- **Photos et images** : les fichiers que vous téléversez, conservés tels que vous les avez envoyés, avec leur nom d'origine, leur format et leurs dimensions. Les fichiers photo peuvent contenir des métadonnées ajoutées par votre appareil photo ou téléphone (EXIF), pouvant inclure les coordonnées GPS de la prise de vue, la date et l'heure, et le modèle d'appareil. Nous ne retirons pas ces métadonnées : elles restent dans le fichier original stocké et sont incluses lorsque le fichier est téléchargé par toute personne autorisée à voir le contenu illustré. Une photo jointe à une petite annonce peut donc révéler l'endroit où elle a été prise — souvent votre domicile — plus précisément que la zone approximative de l'annonce. Retirez ces métadonnées avant l'envoi si vous ne souhaitez pas les partager.

Chaque élément ci-dessus est stocké avec l'identité du compte qui l'a créé et les dates de création et de dernière modification.

### Invitations dans une équipe

Un administrateur d'équipe peut inviter quelqu'un par son adresse e-mail, que cette personne ait déjà un compte ou non. Nous stockons l'adresse invitée, l'équipe, le rôle proposé, l'administrateur qui a envoyé l'invitation, son statut (en attente, acceptée, révoquée, expirée), les dates correspondantes et la personne qui l'a acceptée ou révoquée. Le lien contenu dans l'e-mail n'est stocké que sous forme de hachage irréversible. Chaque invitation donne lieu à un e-mail indiquant le nom d'affichage de l'administrateur qui invite et le nom de l'équipe ; renvoyer l'invitation remplace la précédente, et une adresse qui reçoit trop d'invitations dans la même journée ne reçoit plus d'e-mail ce jour-là. L'e-mail est rédigé dans la langue choisie par la personne invitée si elle a un compte, sinon dans celle de l'administrateur. Personne ne rejoint une équipe sans avoir accepté ; créer un compte ne vaut pas acceptation d'une invitation. Les administrateurs de l'équipe voient les invitations envoyées, avec chaque adresse et son statut. Une invitation peut être acceptée pendant 14 jours. Une fois acceptée, révoquée ou expirée, l'invitation — y compris l'adresse invitée, même si cette personne n'a jamais créé de compte — est conservée 365 jours pour que l'équipe sache qui a fait venir chaque membre, puis supprimée automatiquement.

### Notifications

Lorsqu'il se passe quelque chose dans vos équipes, nous créons une notification pour vous : une sortie, un voyage ou une publication est publié ; une sortie ou un voyage auquel vous êtes inscrit est annulé, change de date ou de lieu de rendez-vous, ou perd le groupe que vous aviez rejoint ; une sortie à laquelle vous êtes inscrit commence dans moins d'un jour ; quelqu'un s'inscrit à une sortie que vous avez créée ou dont vous menez un groupe ; quelqu'un commente l'un de vos contenus ou répond à l'un de vos commentaires ; vous êtes invité dans une équipe ; ou, si vous modérez une équipe, un élément y est signalé. Pour chacune, nous stockons son type, sa date de création, et si et quand vous l'avez lue. La notification conserve aussi une copie de ce dont elle parle, prise au moment de sa création : le nom du membre qui l'a déclenchée, le nom de l'équipe, le titre et la date de la sortie, du voyage ou de la publication, et, pour les commentaires et les réponses, un extrait de 280 caractères au maximum du commentaire.

Chaque notification apparaît dans votre boîte de réception, sur le site et dans l'application. Certains types peuvent aussi vous être envoyés par e-mail, ou en notification push sur votre téléphone ou dans votre navigateur. Chaque type a des réglages par défaut, que vous pouvez modifier par type et par canal dans vos réglages de notifications ; nous ne stockons que les réglages que vous modifiez. Vous pouvez aussi choisir de recevoir vos e-mails non urgents en un seul récapitulatif quotidien, envoyé à 7 h dans votre fuseau horaire, et mettre une équipe en sourdine : ses annonces (nouvelles sorties, nouveaux voyages et publications) ne vous parviennent alors plus, pas même dans votre boîte de réception, tandis que les notifications qui vous concernent personnellement continuent d'arriver. Nous stockons aussi ces choix. Pour chaque notification envoyée par e-mail ou en notification push, nous enregistrons le canal, l'état de l'envoi et sa date.

Si vous autorisez les notifications, nous stockons, pour chaque téléphone ou navigateur : un jeton push émis par Google Firebase Cloud Messaging, qui identifie l'installation de l'application ou le navigateur, et non votre personne ; la plateforme (Android, iOS ou web) ; le nom ou le modèle de l'appareil (sur Android, fabricant et modèle ; sur iOS, le nom de l'appareil tel que le système le fournit ; sur le site, le nom du navigateur et du système, par exemple « Firefox · Android ») ; la version de l'application ; et les dates d'enregistrement et de dernière activité. L'application et le site ne demandent l'autorisation d'afficher des notifications que lorsque vous le choisissez, jamais à leur lancement. Vous pouvez la retirer à tout moment dans les réglages de votre téléphone ou de votre navigateur, couper les notifications sur le site, ou couper un type de notification dans vos réglages de notifications.

Les notifications push affichent leur titre et leur texte sur votre téléphone ou votre ordinateur, et, selon vos réglages, sur l'écran verrouillé. Pour les commentaires et les réponses, ce texte contient le nom du membre qui a écrit et un extrait de ce qu'il a écrit. Si vous ne voulez pas qu'il soit visible, modifiez les réglages de notifications de l'écran verrouillé de votre appareil, ou désactivez les notifications push pour ces types dans vos réglages de notifications.

**Canaux de discussion d'équipe** : les administrateurs d'une équipe peuvent la relier à un canal de discussion (Slack, Discord, Mattermost) ou à toute adresse HTTPS de leur choix. Les annonces de l'équipe — une sortie, un voyage ou une publication publié, une sortie ou un voyage annulé ou modifié — y sont alors aussi publiées, avec le nom du membre qui les a déclenchées, le nom de l'équipe, le titre et la date de l'élément, ce qui a changé, et un lien vers lui. Toute personne ayant accès à ce canal peut lire ces messages, qui sont ensuite conservés par le service choisi selon ses propres règles. Pour chaque message, nous enregistrons s'il a été remis, et quand.

### Signalements et blocages

Pour permettre la modération (voir la section 6 des conditions d'utilisation), nous enregistrons :

- **Vos signalements** : qui a signalé (vous), ce qui est signalé (le contenu ou le membre, et l'équipe concernée), l'auteur du contenu ou le membre visé, le motif choisi, le message facultatif que vous ajoutez, une **copie du texte signalé** (1 000 caractères au plus) et la décision prise (contenu supprimé ou signalement rejeté, par qui et quand). La copie permet de vérifier la décision même si le contenu a été modifié ou supprimé depuis.
- **Vos blocages** : qui a bloqué qui, et depuis quand.

Qui y a accès :

- **Votre identité de signaleur** n'est visible **que par l'équipe Pedalons**. Elle n'est jamais montrée aux organisateurs de l'équipe, ni à l'auteur du contenu, ni au membre signalé, et la notification envoyée aux modérateurs n'en contient rien.
- **Les organisateurs et administrateurs de l'équipe** voient le contenu signalé, son auteur, les motifs et les messages ajoutés, mais pas qui a signalé. Un message peut vous identifier par ce qu'il dit : écrivez-le en sachant qu'ils le liront. Un organisateur visé par un signalement ne le voit pas.
- **Vos blocages** ne sont visibles que par vous. La personne bloquée n'en est pas informée, et rien dans le service ne le lui révèle.

Un contenu signalé peut être masqué aux membres en attendant la décision d'un modérateur. La notification qui prévient les modérateurs d'un signalement ne contient que le nom de l'équipe, jamais le contenu signalé.

**Filtre de publication** : au moment où vous publiez, votre texte est comparé à une courte liste de termes injurieux ou haineux. S'il en contient un, la publication est refusée et le texte n'est pas enregistré ; ce refus n'a aucune autre conséquence pour votre compte.

### Signalements de problèmes et rapports d'erreur

Pour corriger les bugs, nous recevons :

- **Vos signalements de problème ou suggestions** (« Signaler un problème », dans le site ou l'application) : le texte que vous écrivez, la plateforme et la version de l'application, et, si vous laissez cochée la case « Joindre les informations techniques », la page ou l'écran affiché, l'équipe que vous consultez, le système et le modèle de l'appareil ou le navigateur, la langue, le fuseau horaire, et un journal de vos dernières actions dans l'application (pages visitées, requêtes en échec, erreurs). Le formulaire vous montre exactement ce qui sera envoyé.
- **Des rapports d'erreur automatiques** (connecté uniquement) : quand l'application rencontre une erreur inattendue, elle envoie la description technique de l'erreur, les mêmes informations techniques et le journal de vos dernières actions. Vous pouvez désactiver cet envoi dans **Profil → Préférences**.

Le journal ne contient ni mot de passe, ni contenu de formulaire, ni paramètre d'adresse web ; les jetons de connexion et les adresses e-mail qui s'y trouveraient sont masqués par nos serveurs avant tout enregistrement. Ces informations sont transmises à l'équipe Pedalons sous forme de tickets dans un dépôt **privé** GitHub (voir section 4), où vous êtes désigné par un identifiant technique, jamais par votre nom ni votre adresse e-mail.

### Inscriptions aux programmes bêta

Sur la page Applications (site web ou application mobile), toute personne, avec ou sans compte, peut laisser une adresse e-mail pour être prévenue de l'ouverture d'une version de test de notre application mobile ou Garmin. Nous stockons l'adresse, le site sur lequel elle a été saisie et la date. Aucun compte n'est nécessaire, l'adresse n'est rattachée à aucun compte et aucun e-mail de confirmation n'est envoyé. Seuls les administrateurs de la plateforme Pedalons peuvent voir la liste, qui couvre tous les sites. Nous n'utilisons l'adresse que pour vous contacter au sujet de la bêta. Si nous vous invitons, nous saisissons votre adresse à la main dans le service de test du magasin concerné (Apple TestFlight, Google Play Console ou Garmin Connect IQ), qui la traite ensuite selon sa propre politique de confidentialité. Envoyer deux fois la même adresse n'a aucun effet. Pour être retiré de la liste, écrivez à privacy@pedalons.fr.

### Données de connexion à des services GPS tiers

Si vous connectez un service GPS externe (Hammerhead, Garmin, Wahoo) :

- **Jetons d'accès OAuth** : chiffrés en AES-256-GCM avant stockage. Nous ne stockons jamais vos identifiants (nom d'utilisateur/mot de passe) de ces services.
- **Identifiant utilisateur externe** : lorsque le service en renvoie un à la connexion (c'est le cas de Hammerhead, pas de Wahoo), pour le relier à votre compte Pedalons.
- **Ce que nous envoyons** : uniquement lorsque vous choisissez d'envoyer un itinéraire, nous l'envoyons sur votre compte du service : sa trace (un fichier GPX pour Hammerhead, un parcours pour Garmin, un fichier FIT pour Wahoo) et son nom. Wahoo reçoit aussi le point de départ, la distance, le dénivelé positif et négatif, et une empreinte du fichier qui lui sert à éviter les doublons. La connexion à Wahoo demande l'autorisation de lire votre profil Wahoo (Wahoo l'exige) et de créer des itinéraires ; nous ne lisons ni votre profil ni vos activités Wahoo.

### Données stockées localement sur votre appareil

**Dans votre navigateur web :**

- **Langue** : une fois que vous avez choisi une langue, elle est conservée un an dans un cookie `lang`, pour que les pages s'affichent dans cette langue.
- **Système d'unités**, **préférence de thème** (mode clair ou sombre) et **préférences de carte** (fond de carte choisi, relief et affichage 3D) : dans le stockage local (localStorage), conservés jusqu'à ce que vous effaciez les données de votre navigateur. Lorsque vous êtes connecté et que vous choisissez votre langue, votre système d'unités ou votre thème, ce choix est aussi enregistré dans votre compte ; la valeur enregistrée dans votre compte prévaut, et la copie de votre navigateur n'est qu'un cache local.
- **Cookie de session** : un cookie HttpOnly nommé `refresh_token`, contenant votre jeton de rafraîchissement. Les scripts ne peuvent pas le lire. Il est envoyé avec chaque requête vers le site, y compris au chargement des pages, pour que notre serveur puisse vous afficher directement les pages en mode connecté. À chaque rafraîchissement de votre session, le navigateur reçoit une nouvelle échéance de 30 jours pour le cookie ; la session sur nos serveurs prend néanmoins fin 30 jours après la connexion qui l'a créée. Lorsque vous êtes connecté, les pages construites par notre serveur contiennent votre profil et un jeton d'accès de courte durée (valide 15 minutes). Ces pages sont envoyées avec l'en-tête « Cache-Control: no-store », qui indique aux navigateurs et aux caches intermédiaires de n'en garder aucune copie.
- **Notifications push** : si vous les activez, le jeton de ce navigateur, pour pouvoir le désinscrire lorsque vous vous déconnectez ou coupez les notifications. Le composant de messagerie Firebase qui inscrit le navigateur n'est chargé qu'une fois les notifications activées, et conserve ses propres données d'inscription dans le stockage du navigateur.
- **Invitation ou transfert d'équipe en attente** : lorsque vous ouvrez un lien d'invitation dans une équipe, ou confirmez un transfert d'équipe depuis Biketeam, le jeton de l'invitation ou la demande de transfert est conservé dans le stockage de session de l'onglet le temps que vous vous connectiez ou créiez un compte (voir section 8).
- **Rapports d'erreur et proposition d'installation** : le fait que vous ayez désactivé les rapports d'erreur automatiques, et la date à laquelle vous avez fermé la proposition d'installer le site comme une application.
- **Journal des dernières actions** : conservé dans la mémoire du navigateur (200 entrées au plus), pour un éventuel signalement de problème ; il ne quitte votre appareil que dans les cas décrits dans « Signalements de problèmes et rapports d'erreur ».

**Dans l'application mobile :**

- **Votre jeton de session** : l'application mobile n'utilise pas de cookies. Votre jeton de rafraîchissement est stocké dans le stockage sécurisé de votre appareil (trousseau iOS, accessible seulement après le premier déverrouillage de l'appareil, ou stockage chiffré protégé par le Keystore Android) et est supprimé à la déconnexion.
- **Préférences d'affichage** : une copie de votre thème, de votre langue et de votre système d'unités (la valeur enregistrée dans votre compte prévaut), ainsi que le fond de carte choisi, le réglage d'ombrage du relief et la présentation de la liste des itinéraires (liste ou carte, densité). Ils sont conservés dans les réglages locaux de l'application.
- **Cache d'images** : les photos et avatars affichés par l'application, y compris les photos privées de vos équipes, sont mis en cache dans le stockage de l'application pour ne pas être retéléchargés. Le cache est effacé à la désinstallation de l'application ou à l'effacement de ses données.
- **Cache de carte** : le moteur de carte conserve les images de carte et les couches d'itinéraires d'équipe qu'il a affichées dans le stockage de l'application, pour ne pas les retélécharger. Il est effacé à la désinstallation de l'application ou à l'effacement de ses données.
- **Fichiers que vous téléchargez** : lorsque vous téléchargez un itinéraire (GPX/FIT) ou l'original d'un fichier joint, l'application l'enregistre dans son dossier temporaire et le transmet au menu de partage de votre téléphone. Les fichiers joints peuvent être des photos partagées dans vos équipes, avec les métadonnées qu'elles contiennent (voir « Photos et images »). Le fichier reste dans ce dossier jusqu'à ce que le système d'exploitation le vide ou que vous désinstalliez l'application.
- **Jeton push** : le composant Firebase Cloud Messaging de l'application conserve sur votre appareil un identifiant d'installation et un jeton push. Le jeton n'est pas supprimé de l'appareil à la déconnexion, car il identifie l'installation de l'application et non votre personne, mais notre serveur cesse de l'utiliser pour votre compte.
- **Journal des dernières actions et rapports d'erreur** : un fichier de l'application contient vos dernières actions (200 entrées au plus), pour un éventuel signalement de problème ; l'application conserve aussi les rapports d'erreur qu'elle n'a pas encore pu envoyer, et votre réglage des rapports d'erreur automatiques. Rien de cela ne quitte votre téléphone, sauf dans les cas décrits dans « Signalements de problèmes et rapports d'erreur ».
- **Pas de sauvegarde** : sur Android, les données de l'application sont exclues de la sauvegarde dans le cloud et du transfert vers un nouvel appareil ; rien de ce qui précède ne quitte donc votre téléphone par ce biais.

**Sur votre appareil GPS (Karoo, Garmin) :**

Lorsque vous appairez un Hammerhead Karoo ou un appareil Garmin, l'extension Pedalons ne stocke sur l'appareil que : vos jetons de session (jeton d'accès et jeton de rafraîchissement) et leur expiration, plus, pendant l'appairage, le code d'appairage temporaire. Ils sont conservés dans le stockage applicatif privé de l'extension sur l'appareil et supprimés lorsque vous déconnectez l'appareil ou désinstallez l'extension. Se déconnecter dans l'application Garmin, ou se déconnecter dans l'extension Karoo, n'efface les jetons que de l'appareil : la session sur nos serveurs reste valide jusqu'à son expiration (90 jours après l'appairage). Pour y mettre fin immédiatement, utilisez « Déconnecter tous les appareils » dans le profil de l'application mobile. Ce stockage est protégé par le bac à sable applicatif de l'appareil, mais nous n'y ajoutons pas de chiffrement : une personne ayant accès à l'appareil déverrouillé (ou à une de ses sauvegardes) pourrait lire ces jetons ; si vous perdez l'appareil, utilisez « Déconnecter tous les appareils ». Aucun contenu de sortie ou d'itinéraire n'est conservé sur l'appareil par l'extension.

Vous pouvez révoquer tous ces identifiants de session à tout moment avec « Déconnecter tous les appareils » dans le profil de l'application mobile. Se déconnecter de l'application mobile supprime l'enregistrement push de ce téléphone, et se déconnecter du site, ou y couper les notifications, supprime celui de ce navigateur, qui cesse alors de recevoir des notifications. « Déconnecter tous les appareils » met fin à toutes les sessions mais ne supprime que l'enregistrement push de l'appareil depuis lequel vous l'utilisez ; vos autres téléphones et navigateurs continuent de recevoir des notifications push jusqu'à ce que vous vous y déconnectiez, que vous désactiviez les notifications ou que vous désinstalliez l'application.

---

## 2. Comment nous collectons vos données

- **Directement auprès de vous** : lorsque vous créez un compte, remplissez votre profil, importez des fichiers GPX, créez du contenu ou connectez un service GPS.
- **Automatiquement** : adresse IP et agent utilisateur lors de vos connexions ; cookie de session pour maintenir votre authentification ; journaux serveur décrits à la section 1 ; et, sauf si vous les désactivez, rapports d'erreur automatiques (voir « Signalements de problèmes et rapports d'erreur »).
- **Depuis la plateforme à laquelle Pedalons succède** : certains comptes et contenus ont été importés par le passé depuis la plateforme Biketeam, et un administrateur d'équipe Biketeam peut faire transférer le contenu de son équipe depuis le serveur de Biketeam, comme décrit à la section 1 (« Comptes et équipes venus de Biketeam »).
- **Auprès d'autres membres** : un administrateur d'équipe qui vous invite nous communique votre adresse e-mail (voir « Invitations dans une équipe »). Si vous n'avez pas de compte, nous n'utilisons cette adresse que pour vous envoyer l'invitation, pour montrer l'invitation aux administrateurs de l'équipe, et pour vous la proposer si vous vous inscrivez avec cette adresse. Elle est supprimée comme indiqué à la section 6. Un membre qui vous signale, ou signale votre contenu, nous communique le motif du signalement, son message et une copie du texte signalé (voir « Signalements et blocages »).
- **Depuis le formulaire d'inscription à la bêta** : toute personne peut saisir une adresse e-mail sur la page Applications, sans e-mail de confirmation (voir « Inscriptions aux programmes bêta »).
- **Lorsque vous utilisez un champ de recherche de lieu** (lieu de l'équipe, lieux de rendez-vous, petites annonces sur le site ; disponible uniquement une fois connecté) : le texte que vous saisissez est envoyé à notre serveur, qui le recherche pour vous auprès du service Nominatim d'OpenStreetMap. Nominatim reçoit le texte recherché et votre langue d'affichage, envoyés depuis l'adresse de notre serveur — jamais votre adresse IP ni votre identité. Les résultats sont conservés dans la mémoire de notre serveur pendant 24 heures au maximum, pour qu'une même recherche ne soit pas envoyée deux fois.

En dehors des imports et transferts depuis Biketeam, des invitations, des signalements et des services décrits ci-dessus, **nous ne collectons aucune donnée auprès de tiers** : nous n'achetons ni ne louons jamais de données, nous ne pratiquons aucun suivi publicitaire, et nous ne collectons rien depuis les réseaux sociaux.

---

## 3. Pourquoi nous utilisons vos données

| Finalité | Base légale (RGPD) |
|----------|-------------------|
| Fournir le service (compte, authentification, navigation) | Exécution du contrat |
| Afficher les itinéraires, sorties, voyages et autres contenus de votre équipe | Exécution du contrat |
| Envoyer des e-mails de vérification et codes de connexion | Exécution du contrat |
| Afficher les notifications concernant vos équipes dans votre boîte de réception, sur le site et dans l'application | Exécution du contrat |
| Vous envoyer des notifications par e-mail, selon vos réglages de notifications | Exécution du contrat (réglable à tout moment, type par type) |
| Vous notifier sur votre téléphone ou dans votre navigateur (notifications push) | Consentement (autorisation donnée sur le téléphone ou dans le navigateur) ; vous pouvez le retirer dans les réglages de votre appareil ou de votre navigateur, ou couper le push type par type dans vos réglages de notifications |
| Publier les annonces de l'équipe dans le canal de discussion relié par ses administrateurs | Intérêt légitime (l'intérêt de l'équipe à informer ses membres) |
| Relayer les messages au sujet des petites annonces | Exécution du contrat |
| Invitations dans une équipe | Intérêt légitime (l'intérêt de l'équipe à inviter ses membres) |
| Modérer les contenus : traiter les signalements, appliquer vos blocages, filtrer les termes injurieux à la publication | Exécution du contrat (conditions d'utilisation) et intérêt légitime (protéger les membres) |
| Conserver la preuve de votre acceptation des conditions d'utilisation | Intérêt légitime |
| Sécuriser votre compte (détection de sessions suspectes) | Intérêt légitime |
| Synchroniser vos itinéraires avec des appareils GPS connectés | Consentement (connexion volontaire) |
| Afficher les cartes (fonds de carte téléchargés par votre appareil auprès du fournisseur choisi) | Intérêt légitime |
| Position approximative (« Autour de moi », facultatif, application mobile) | Consentement, donné via la demande d'autorisation du système d'exploitation ; vous pouvez le retirer dans les réglages de votre appareil |
| Fournir votre flux calendrier personnel à l'application de calendrier de votre choix | Consentement (abonnement volontaire) |
| Vous prévenir de l'ouverture d'une bêta de nos applications | Consentement (inscription volontaire) |
| Traiter vos signalements de problème et corriger les erreurs de l'application | Intérêt légitime (fiabilité du service ; rapports automatiques désactivables) |
| Assurer la continuité du service pour les équipes venues de la plateforme précédente | Intérêt légitime |
| Exploiter et assister la plateforme | Intérêt légitime |

Nous n'utilisons **jamais** vos données pour :
- De la publicité ciblée
- La revente à des tiers
- Du profilage automatisé ou de la prise de décision automatisée
- Des statistiques d'utilisation — nous n'utilisons aucun outil d'analyse ou de suivi

---

## 4. Partage de vos données

### Visibilité au sein de la plateforme

- **Contenu d'équipe** (« équipe ») : visible par les membres de cette équipe. Les brouillons et éléments non publiés ne sont visibles que par les organisateurs et administrateurs de l'équipe.
- **Contenu non répertorié** (« non répertorié ») : absent des listes publiques, des résultats de recherche et des annuaires, mais lisible par quiconque possède le lien, y compris sans compte. Traitez un lien non répertorié comme semi-public : nous ne pouvons pas empêcher qu'il soit transféré.
- **Contenu public** (« public ») : un contenu « public » dans une équipe publique est accessible à quiconque sur internet, sans compte et sans connexion. Ces pages sont rendues par notre serveur et peuvent être indexées par les moteurs de recherche et affichées en aperçu de lien dans les réseaux sociaux et messageries. Cela inclut les listes de participants des sorties et voyages publics (nom d'affichage et photo de profil de chaque inscrit).
- **Votre nom d'affichage et votre photo de profil** sont visibles par les membres de vos équipes (selon les règles de la liste des membres décrites à la section 1). Ils sont aussi visibles par quiconque sur internet, sans connexion, partout où ils apparaissent sur du contenu public — par exemple dans la liste des participants d'une sortie ou d'un voyage public, ou comme auteur d'une publication publique. Le fichier de la photo de profil est lui-même servi à une adresse qui n'exige pas de connexion : quiconque détient cette adresse peut afficher l'image. Choisissez un nom d'affichage et une photo que vous acceptez de montrer publiquement ; vous pouvez retirer votre photo à tout moment depuis votre profil.
- **Contenu partagé par lien** : un fichier analysé avec l'outil GPX produit une page d'aperçu dont l'adresse contient un identifiant aléatoire impossible à deviner. Quiconque possède cette adresse peut ouvrir la page, télécharger la trace en GPX ou FIT et voir l'image de carte — aucun compte n'est nécessaire, le lien est la clé. Voir « Outil d'analyse GPX » à la section 1.
- **Aperçus de lien** : lorsqu'un lien vers un itinéraire, une sortie ou un fichier GPX analysé est collé dans une messagerie, un réseau social ou un outil de discussion, les serveurs de cette plateforme récupèrent la page et téléchargent l'image d'aperçu que nous publions — pour les traces GPS, cette image est une carte de la trace elle-même, accompagnée de son nom, de sa distance et de son dénivelé. La plateforme où vous collez le lien reçoit donc cette image et ces statistiques, même si personne ne clique. Cela vaut pour tout lien vers du contenu accessible publiquement et pour tout fichier GPX analysé. Ne partagez pas le lien si la trace part de votre domicile.
- **Abonnements calendrier (ICS)** : votre profil propose un lien personnel permettant à une application de calendrier externe d'afficher vos sorties et voyages. Ce lien contient un jeton secret et fonctionne sans connexion : quiconque l'obtient peut lire les sorties et voyages que vous voyez, y compris ceux réservés à l'équipe, tant que le lien est valide. Si vous vous abonnez depuis un service de calendrier hébergé (Google Calendar, Apple Calendar, Outlook…), ce service récupère le flux sur ses propres serveurs, environ une fois par heure, et stocke les titres, dates et liens des sorties et étapes de vos équipes ainsi que les noms de vos équipes ; nous n'avons aucun contrôle sur ce qu'il en fait. Traitez l'adresse du flux comme un mot de passe. Le lien n'expire pas ; vous pouvez l'invalider à tout moment en le régénérant, ce qui casse immédiatement l'ancien lien.
- **Canaux de discussion d'équipe** : si les administrateurs de votre équipe ont relié un canal de discussion ou une autre adresse (voir « Notifications » à la section 1), les annonces de l'équipe, avec le nom du membre qui les a déclenchées, sont publiées sur ce service. C'est l'équipe qui choisit le service, qui reçoit ces messages en tant que destinataire agissant selon ses propres conditions, et non en tant que prestataire de notre part.
- **Signalements** : les organisateurs et administrateurs de l'équipe voient les signalements portant sur les contenus de leur équipe sans l'identité du signaleur ; l'équipe Pedalons voit qui a signalé (voir « Signalements et blocages » à la section 1).
- **Administration de la plateforme** : un très petit nombre de comptes administrateurs de la plateforme (actuellement le responsable du traitement lui-même) peut techniquement accéder à tous les comptes et à tous les contenus de tous les sites, y compris les contenus d'équipe et les brouillons, et lister les adresses e-mail et dates de dernière connexion. Les administrateurs de la plateforme voient aussi la liste des adresses e-mail inscrites aux programmes bêta, tous sites confondus. Cet accès sert uniquement à exploiter, assister et modérer le service, et uniquement lorsque c'est nécessaire.

### Sous-traitants techniques

Nous faisons appel à des services techniques pour le fonctionnement de la plateforme :

| Service | Rôle | Données concernées |
|---------|------|-------------------|
| OVHcloud (OVH SAS, France) | Hébergement de l'application, de la base de données et du stockage objet | Toutes les données |
| Scaleway (Scaleway SAS, France) | Envoi des e-mails transactionnels et des notifications par e-mail, par son relais SMTP Transactional Email | Adresse e-mail, nom d'affichage, et le contenu du message que nous lui demandons de remettre : lien de vérification d'e-mail, code de connexion à usage unique, lien de réinitialisation de mot de passe (ces liens et codes sont à usage unique et à courte durée de vie) ; invitations dans une équipe (l'adresse e-mail de la personne invitée, qui peut ne pas avoir de compte, le nom d'affichage de l'administrateur qui invite, le nom de l'équipe et le lien d'invitation, valide 14 jours) ; messages au sujet des petites annonces (l'adresse e-mail et le nom d'affichage de l'auteur, le nom d'affichage de l'expéditeur, son adresse e-mail comme adresse de réponse, le titre et le lien de l'annonce, et le texte intégral du message) ; e-mails de notification et récapitulatifs quotidiens (votre nom d'affichage, le nom du site, le nom de l'équipe, le titre et la date de la sortie, du voyage ou de la publication concernés, le nom du membre qui l'a déclenchée, pour les commentaires et les réponses un extrait de 280 caractères au maximum, et des liens vers la page concernée et vers vos réglages de notifications) ; et le lien vers votre export de données |
| GitHub (GitHub, Inc., États-Unis) | Suivi des signalements de problème et des rapports d'erreur, dans un dépôt privé accessible à la seule équipe Pedalons | Texte du signalement, informations techniques, journal des dernières actions, équipe consultée, identifiant technique du compte et domaine |
| Google Firebase Cloud Messaging (Google Ireland Limited, Irlande) | Acheminement des notifications push vers l'application mobile, via Apple Push Notification service pour les iPhone, et vers votre navigateur | Le jeton push de votre téléphone ou de votre navigateur, l'adresse IP de votre appareil lorsque l'application ou le navigateur contacte Firebase, et le contenu de chaque notification : titre et texte (qui peuvent contenir un nom d'équipe, le titre et la date d'une sortie, d'un voyage ou d'une publication, le nom d'un membre et un extrait de commentaire), ainsi que des données techniques qui permettent d'ouvrir la bonne page (type et identifiant de la notification, identifiants de l'équipe et de la page) |
| Apple Push Notification service (Apple Inc., États-Unis) | Acheminement des notifications push vers les iPhone, relayées par Firebase Cloud Messaging | Le même contenu de notification et le jeton push Apple de votre appareil |
| Hammerhead, Garmin, Wahoo (États-Unis) | Envoi de vos itinéraires vers votre appareil GPS, uniquement si vous connectez le service | Jetons OAuth, et les itinéraires que vous choisissez d'envoyer (trace, nom ; pour Wahoo, aussi le point de départ, la distance et les dénivelés) |

Notre serveur compose lui-même chaque e-mail et le remet, complet, au relais SMTP de Scaleway, qui reçoit donc tout ce que contient le message : le lien de vérification, le code de connexion ou le lien de réinitialisation, le lien d'invitation, le texte d'un message au sujet d'une annonce, ou le contenu d'une notification. Contrairement aux liens et codes d'authentification, les liens d'invitation restent valides 14 jours et les e-mails de notification contiennent des liens durables vers les contenus concernés. Le prestataire est contractuellement tenu de n'utiliser ces données que pour remettre l'e-mail pour notre compte.

### Affichage des cartes et recherche d'adresses

Les cartes sont dessinées dans votre navigateur ou votre application. La liste des fonds de carte, et les documents de style de certains d'entre eux, proviennent de notre serveur ; les images de carte elles-mêmes sont téléchargées par votre appareil directement auprès du fournisseur du style que vous avez sélectionné. Ce fournisseur reçoit donc votre adresse IP, la version de votre navigateur ou application, et les coordonnées de la zone de carte affichée (ce qui révèle, approximativement, la zone de l'itinéraire que vous regardez). Pour les fonds de carte, nous ne lui transmettons rien vous concernant : il ne voit que la requête émise par votre appareil, et votre choix de fond de carte est stocké localement sur votre appareil. Ces fournisseurs agissent en responsables de traitement indépendants, selon leurs propres politiques. La recherche de lieux, en revanche, passe par notre serveur (voir section 2).

| Service | Rôle | Données concernées |
|---------|------|-------------------|
| VersaTiles (tiles.versatiles.org) | Fond de carte vectoriel (style par défaut), polices et sprites de carte | Adresse IP, zone de carte affichée |
| Mapterhorn (tiles.mapterhorn.com) | Tuiles d'altitude et d'ombrage (relief 3D) ; également utilisé par notre serveur pour corriger l'altitude des traces importées | Votre navigateur ou application, uniquement si vous activez l'ombrage du relief ou le relief 3D (tous deux désactivés par défaut) : adresse IP, zone de carte affichée. Côté serveur : coordonnées de tuiles grossières (~10 km de côté) couvertes par une trace et adresse IP de notre serveur — jamais votre identité, votre compte ni votre adresse IP |
| IGN / Géoplateforme (data.geopf.fr, France) | Fonds IGN, satellite et SCAN 25 | Adresse IP, zone de carte affichée |
| OpenStreetMap Foundation (tile.openstreetmap.org, Royaume-Uni) | Fond OpenStreetMap | Adresse IP, zone de carte affichée |
| OpenStreetMap France (tile-cyclosm.openstreetmap.fr) | Fond CyclOSM | Adresse IP, zone de carte affichée |
| OpenStreetMap Nominatim (nominatim.openstreetmap.org) | Recherche de lieux, interrogée par notre serveur | Le texte que vous saisissez et votre langue d'affichage ; jamais votre adresse IP ni votre identité |
| Esri (server.arcgisonline.com, États-Unis) | Fond « Satellite (ESRI) » | Adresse IP, zone de carte affichée |

Le fond de carte « Michelin » est servi par nos soins (tiles.pedalons.fr) : le choisir n'implique aucun tiers.

Nos services de traitement d'images (imgproxy), de rendu de cartes (tileserver) et de calcul d'itinéraires (Valhalla) sont auto-hébergés et ne transmettent rien à des tiers. Les polices de caractères du site et de l'application y sont intégrées : aucune n'est chargée depuis un service tiers, à l'exception des polices des étiquettes de carte, fournies avec le fond de carte (voir VersaTiles ci-dessus). Les seuls services externes impliqués dans le traitement d'une trace ou d'une recherche côté serveur sont le service d'altitude et le service de recherche de lieux listés ci-dessus ; ces requêtes sont émises par notre serveur, ne portent jamais votre identité, et sont mises en cache pour qu'une même zone ou une même recherche ne soit pas demandée deux fois.

### Nous ne vendons pas vos données

Nous ne vendons, ne louons et ne partageons pas vos données personnelles à des fins commerciales ou publicitaires.

### Autorités

Nous pouvons être amenés à communiquer vos données si la loi l'exige (demande judiciaire, obligation légale).

---

## 5. Transferts internationaux de données

Nos serveurs sont hébergés par **OVHcloud** (OVH SAS, Roubaix, France) et sont situés en France. Les données que nous stockons restent dans l'Union européenne.

Certains traitements que vous pouvez déclencher impliquent des serveurs situés hors de l'Union européenne :

- **La connexion à un service GPS tiers** (Hammerhead, Garmin, Wahoo) implique un transfert de données vers ces services, situés aux États-Unis. Ce transfert repose sur votre consentement explicite lors de la connexion du service.
- **Le fond de carte « Satellite (ESRI) »** est demandé directement par votre navigateur ou par l'application mobile à Esri (États-Unis), qui reçoit votre adresse IP et la zone de carte affichée, uniquement si vous le choisissez ; vous pouvez éviter entièrement ce transfert en choisissant un autre fond. Le fond OpenStreetMap est servi depuis le Royaume-Uni, qui bénéficie d'une décision d'adéquation de la Commission européenne.
- **L'application mobile intègre le composant de messagerie Firebase de Google**, qui contacte les serveurs de Google au démarrage de l'application, avant même que vous vous connectiez ou autorisiez les notifications. Cela révèle l'adresse IP de votre appareil à Google et crée un identifiant d'installation Firebase. L'application n'intègre que cette partie messagerie de Firebase : aucun composant Firebase d'analyse, de publicité ou de suivi. Sur le site, ce composant n'est chargé qu'une fois les notifications activées.
- **Les notifications push** transitent par **Firebase Cloud Messaging**, fourni par Google Ireland Limited. Google peut traiter ces données aux États-Unis ; ce transfert est encadré par les clauses contractuelles types de la Commission européenne et par l'adhésion de Google LLC au cadre de protection des données UE–États-Unis (Data Privacy Framework). Sur iPhone, les notifications sont remises par le service Apple Push Notification (Apple Inc., États-Unis), comme pour toute application iOS ; dans un navigateur, par le service push propre au navigateur, comme pour tout site qui envoie des notifications. Le titre et le texte d'une notification et le jeton push de votre téléphone ou de votre navigateur peuvent donc être traités aux États-Unis. Cela n'a lieu que tant que vous autorisez les notifications ; vous pouvez y mettre fin à tout moment en désactivant le push dans vos réglages de notifications, en désactivant les notifications dans les réglages de votre téléphone ou de votre navigateur, ou en vous déconnectant.
- **Les signalements de problème et rapports d'erreur** sont transmis à **GitHub** (GitHub, Inc.), aux États-Unis ; ce transfert est encadré par les clauses contractuelles types de la Commission européenne. Ils ne contiennent ni votre nom ni votre adresse e-mail.
- **Canaux de discussion d'équipe** : si les administrateurs de votre équipe en ont relié un, le service qu'ils ont choisi (par exemple Slack ou Discord, exploités depuis les États-Unis) reçoit les annonces de l'équipe. Ce transfert relève du choix de l'équipe et suit les conditions propres à ce service.
- **Programmes bêta** : si nous vous invitons à tester l'une de nos applications, votre adresse e-mail est saisie dans Apple TestFlight, Google Play Console ou Garmin Connect IQ, dont certains sont exploités depuis les États-Unis.

---

## 6. Conservation des données

| Type de données | Durée de conservation |
|----------------|----------------------|
| Données de compte (adresse e-mail, nom d'affichage, photo de profil, préférences, hachage du mot de passe) | Tant que votre compte existe ; effacées dès que vous le supprimez (voir section 7) |
| Date d'acceptation des conditions d'utilisation | Tant que votre compte existe |
| Sessions de connexion (jeton de rafraîchissement haché, adresse IP, agent utilisateur, dates de création et de dernière utilisation) | 30 jours à compter de la connexion qui a créé la session (la durée n'est pas prolongée par l'utilisation). Les sessions expirées ou déconnectées sont effacées par un nettoyage nocturne |
| Sessions d'un appareil GPS appairé (Karoo, Garmin) | 90 jours à compter de l'appairage ; l'appareil renouvelle son jeton d'accès sans prolonger la session. « Déconnecter tous les appareils » la révoque ; se déconnecter sur l'appareil lui-même ne la révoque pas |
| Codes de connexion temporaires (OTP) | 5 minutes, ou jusqu'à 5 tentatives erronées |
| Lien de vérification d'adresse e-mail | 24 heures. Tant qu'il n'est pas utilisé, cet enregistrement contient aussi le nom d'affichage, le hachage du mot de passe et l'heure d'acceptation des conditions, choisis ou donnés à l'inscription |
| Lien de réinitialisation de mot de passe | 1 heure |
| Lien de vérification de changement d'e-mail | 24 heures |
| Codes d'appairage d'appareils GPS | Valides 10 minutes ; l'enregistrement est supprimé dès que l'appareil termine l'appairage |
| Challenges de clés d'accès (WebAuthn) | Valides 5 minutes ; l'enregistrement est supprimé quand le challenge est utilisé ou qu'un nouveau est demandé |
| Jeton de calendrier (adresse secrète de votre flux .ics) | Sans expiration, jusqu'à régénération |
| Équipes et contenus (sorties, posts, itinéraires, voyages, pages) | La suppression d'un élément le masque aux membres et aux visiteurs, mais il reste dans notre base : les administrateurs de l'équipe le voient toujours dans leurs listes, marqué « Supprimé », et peuvent le restaurer. L'enregistrement est conservé jusqu'à demande d'effacement définitif |
| Commentaires | Effacés dès que vous ou votre équipe les supprimez, avec les réponses qu'ils ont reçues |
| Fichiers attachés au contenu (images, GPX, FIT, images de carte générées) | Tant que l'enregistrement associé existe en base. Les fichiers téléversés mais jamais attachés à un contenu sont effacés automatiquement un jour après l'envoi |
| Aperçus de l'outil d'analyse GPX (trace téléversée, points, fichiers GPX/FIT générés et vignette de carte) | 30 jours après création, puis effacement automatique avec les fichiers stockés (supprimables par vous à tout moment) |
| Notifications (entrées de la boîte de réception, statut de lecture, copie du contenu auquel elles renvoient, trace des envois par e-mail et push, et trace des messages publiés dans les canaux de discussion d'équipe) | 90 jours après création, puis effacement par un nettoyage nocturne. Les messages déjà publiés dans le canal de discussion d'une équipe y restent, selon les règles de ce service |
| Réglages de notifications (réglages par type, récapitulatif quotidien, équipes en sourdine) | Tant que votre compte existe ; effacés à sa suppression |
| Enregistrement push d'un téléphone ou d'un navigateur (jeton push, plateforme, nom de l'appareil ou du navigateur, version de l'application, dates d'enregistrement et de dernière activité) | Jusqu'à ce que vous vous déconnectiez de l'application sur ce téléphone ou du site dans ce navigateur, ou coupiez les notifications sur le site, jusqu'à ce qu'un envoi montre que Firebase Cloud Messaging n'accepte plus le jeton (par exemple après la désinstallation de l'application), jusqu'à ce qu'un autre compte se connecte sur cet appareil, ou jusqu'à la suppression de votre compte. Il n'est pas supprimé après une période d'inactivité |
| Réglages du canal de discussion d'une équipe (adresse du canal, langue, état du dernier envoi) | Jusqu'à ce que les administrateurs de l'équipe retirent le canal |
| Invitations dans une équipe (adresse e-mail invitée, rôle proposé, auteur de l'invitation, et date d'acceptation, de révocation ou d'expiration) | Valables 14 jours ; l'enregistrement est conservé 1 an après l'acceptation, la révocation ou l'expiration, pour que l'équipe sache qui a invité qui, puis supprimé par un nettoyage nocturne |
| Trace des messages envoyés au sujet d'une petite annonce (expéditeur, annonce, date ; le message lui-même n'est pas stocké) | Tant que l'annonce est dans notre base. Supprimer une annonce ne fait que la masquer : en pratique, la trace est conservée jusqu'à l'effacement définitif de l'annonce |
| Blocages | Jusqu'à ce que vous débloquiez la personne, ou jusqu'à la suppression du compte de l'un de vous deux |
| Signalements, copie du texte signalé et décision | Tant que le compte de la personne visée existe, pour garder la trace des décisions de modération ; à la suppression du compte du signaleur, conservés sans lien avec lui (voir section 7) |
| Signalements de problème et suggestions | 1 an sur nos serveurs, ou jusqu'à la suppression de votre compte ; le ticket GitHub correspondant est conservé tant qu'il est utile au suivi du bug |
| Rapports d'erreur automatiques | 90 jours sur nos serveurs, ou jusqu'à la suppression de votre compte ; le ticket GitHub, commun à tous les membres touchés par la même erreur, est conservé tant qu'il est utile |
| Demandes de transfert d'équipe depuis Biketeam (compte qui l'a confirmée, équipe Biketeam, dates, résultat) | Conservées comme historique de migration de l'équipe, sans suppression automatique ; après la suppression du compte, la demande n'est plus rattachée à rien qui identifie la personne |
| Inscriptions aux programmes bêta (adresse e-mail, site, date) | Pas de suppression automatique à ce jour ; écrivez à privacy@pedalons.fr pour être retiré de la liste |
| Archive d'export de vos données (ZIP) | 7 jours, puis effacement par un nettoyage nocturne (au plus tard la nuit suivante) ; comme tout fichier stocké, une copie peut subsister dans les sauvegardes jusqu'à 30 jours de plus |
| Historique des demandes d'export (date de la demande, statut, taille de l'archive, date d'expiration) | 90 jours |
| Sauvegardes (copie complète de la base de données et des fichiers stockés) | Une copie par nuit sur un serveur séparé ; les 30 copies les plus récentes sont conservées, les plus anciennes supprimées automatiquement |
| Données après suppression du compte | Effacement immédiat ; disparition des sauvegardes sous 30 jours (voir section 7) |

Les durées ci-dessus sont les durées pendant lesquelles les données peuvent être utilisées. Les enregistrements expirés, utilisés ou déconnectés sont effacés physiquement de la base par des nettoyages exécutés une fois par nuit : ils peuvent donc rester stockés jusqu'à 24 heures au-delà de la durée indiquée. Certains enregistrements temporaires abandonnés (codes d'appairage inutilisés, challenges de clés d'accès abandonnés) ne sont pas encore couverts par un nettoyage.

**Sauvegardes.** Chaque nuit, nous réalisons une copie complète de la base de données et des fichiers téléversés (photos, avatars, fichiers GPX) et la stockons sur un serveur séparé situé en France, afin de pouvoir restaurer le service après une panne. Nous conservons les 30 dernières copies nocturnes ; chacune est supprimée automatiquement à l'ancienneté. Ainsi, lorsque vous supprimez un contenu, ou lorsque des données sont effacées, une copie peut subsister dans nos sauvegardes pendant 30 jours supplémentaires au maximum. Les sauvegardes ne servent qu'à restaurer le service, et une restauration n'a lieu qu'après un incident.

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

Vous pouvez exercer vous-même vos droits d'accès et de portabilité, sans nous écrire : depuis le site web, dans **Profil → Vos données**, choisissez « Télécharger mes données » ; dans l'application mobile, dans **Profil → Vos données**, touchez « Demander un export ». Dans les deux cas, nous préparons une archive ZIP et vous envoyons par e-mail un lien de téléchargement, qui fonctionne sur tous vos appareils.

L'archive contient votre profil, vos équipes, vos inscriptions, tout ce que vous avez publié, ainsi que vos fichiers (photo de profil, images envoyées, fichiers GPX et FIT de vos parcours). Elle contient aussi vos notifications des 90 derniers jours (avec les e-mails et notifications push envoyés pour chacune), vos réglages de notifications (y compris le récapitulatif quotidien et les équipes mises en sourdine), les téléphones et navigateurs enregistrés pour les notifications push, les membres que vous avez bloqués, les signalements que vous avez faits (sans la copie du texte signalé, qui est le contenu de quelqu'un d'autre) et vos signalements de problème et suggestions. Les données sont au format JSON, structuré et lisible par machine. L'archive ne contient pas encore votre fuseau horaire, votre réglage de contact pour les annonces, les invitations dans une équipe que vous avez envoyées ou reçues, ni la trace des messages envoyés via le relais des petites annonces ; demandez-les à privacy@pedalons.fr.

Pour des raisons de sécurité, les éléments d'identification en sont exclus : hachage de votre mot de passe, jetons de session, matériel cryptographique de vos clés d'accès, jeton de votre calendrier, jetons d'accès à vos services GPS connectés et jeton push de chacun de vos téléphones et navigateurs. Leurs métadonnées (dates, appareils, services concernés) sont bien présentes. Le lien de téléchargement expire au bout de **7 jours**, après quoi l'archive est supprimée de nos serveurs (une copie peut subsister dans nos sauvegardes pendant 30 jours au maximum, voir section 6). Un export par heure et par compte.

### Supprimer votre compte

Vous pouvez supprimer votre compte vous-même, à tout moment, sans nous écrire :

- **dans l'application mobile** : **Profil → Compte → Zone de danger**, puis « Supprimer le compte » ;
- **sur le site web** : **Profil → Actions du compte → Zone de danger**, puis « Supprimer le compte ».

Si vous êtes le seul administrateur d'une équipe qui compte d'autres membres, vous devez d'abord nommer un autre membre administrateur : l'application et le site vous indiquent les équipes concernées et la marche à suivre. Une équipe dont vous êtes le seul membre est supprimée avec votre compte, de la même façon que toute équipe supprimée (voir section 6).

Si vous n'avez plus accès à votre compte ou à l'application, écrivez-nous depuis l'adresse e-mail de votre compte à **privacy@pedalons.fr** en demandant sa suppression ; nous la traiterons dans un délai de 30 jours.

La suppression est irréversible et immédiate. Dès votre confirmation, votre compte est désactivé et vos données personnelles sont effacées : adresse e-mail, nom, photo de profil, mot de passe et clés d'accès, sessions (y compris celles de vos appareils GPS), préférences, jeton de calendrier, services GPS connectés, appartenance aux équipes, inscriptions aux sorties et voyages à venir, commentaires, notifications et leurs réglages, enregistrement de vos téléphones et navigateurs pour les notifications push, exports de données, aperçus GPX, vos signalements de problème et rapports d'erreur, et la trace des messages que vous avez envoyés au sujet de petites annonces. Vos petites annonces sont retirées, vidées de leur texte, de leur prix et de leur lieu, et leurs photos sont supprimées. Les blocages sont supprimés dans les deux sens, ceux que vous aviez faits comme ceux qui vous visaient, et les signalements qui vous visent sont supprimés avec la copie de votre contenu qu'ils contenaient. Les signalements que vous avez faits sont conservés, sans plus aucun lien avec vous, pour que les décisions prises restent vérifiables. Les tickets GitHub déjà créés à partir de vos signalements restent en place, avec un identifiant technique qui ne mène plus à personne.

Ce que vous avez publié pour une équipe (sorties, voyages, parcours, posts et leurs fichiers) appartient à cette équipe et reste en ligne. Ces contenus sont désormais attribués à « Ancien membre » et ne sont plus rattachés à aucune donnée permettant de vous identifier. De même, un commentaire auquel d'autres membres ont répondu est conservé vide, avec la mention « Commentaire supprimé », pour que leurs réponses ne disparaissent pas avec lui. Vos inscriptions aux sorties passées sont conservées sous la même forme anonyme et ne sont plus affichées. Les notifications que vous avez déclenchées chez d'autres membres gardent votre nom d'affichage jusqu'à leur expiration (90 jours), et les messages déjà publiés dans le canal de discussion d'une équipe y restent.

Pour qu'un contenu publié pour une équipe disparaisse, supprimez-le avant de supprimer votre compte, ou demandez-le à un organisateur de l'équipe. Supprimer votre compte efface la connexion à vos services GPS de notre côté, mais ne retire pas l'autorisation que vous leur aviez donnée : vous pouvez la révoquer depuis votre compte Hammerhead, Garmin ou Wahoo.

Nos sauvegardes, conservées 30 jours, contiennent encore vos données jusqu'à leur renouvellement ; elles ne servent qu'à rétablir le service après un incident.

### Nous contacter

Pour les autres droits, ou si vous préférez passer par nous, contactez-nous à : **privacy@pedalons.fr** Pour protéger vos données, nous pouvons vous demander de confirmer une demande depuis l'adresse e-mail rattachée à votre compte.

Nous répondrons à votre demande dans un délai de **30 jours**. Si nous ne pouvons pas donner suite, nous vous expliquerons pourquoi.

Vous pouvez également introduire une réclamation auprès de la **CNIL** (Commission Nationale de l'Informatique et des Libertés) : [www.cnil.fr](https://www.cnil.fr)

---

## 8. Cookies et stockage local

Pedalons utilise un nombre minimal de cookies et de données de stockage local :

| Élément | Type | Finalité | Durée |
|---------|------|----------|-------|
| refresh_token | Cookie HttpOnly (site web) | Maintenir votre session authentifiée, y compris sur les pages construites par notre serveur | 30 jours, renouvelés à l'usage (la session elle-même prend fin 30 jours après la connexion) |
| refresh_token | Trousseau iOS / stockage chiffré Keystore Android (application mobile) | Vous garder connecté sans ressaisir vos identifiants | Jusqu'à la déconnexion (validité de la session côté serveur : 30 jours) |
| lang | Cookie | Mémoriser la langue que vous avez choisie, pour afficher les pages dans cette langue | 1 an |
| pedalons-unit-system | localStorage | Mémoriser votre système d'unités | Persistant |
| mantine-color-scheme-value | localStorage | Mémoriser votre thème (clair/sombre) | Persistant |
| pedalons-map-style, pedalons-map-terrain3d, pedalons-map-hillshade | localStorage | Mémoriser vos préférences d'affichage de carte | Persistant |
| pedalons-error-reports | localStorage | Mémoriser que vous avez désactivé les rapports d'erreur automatiques | Persistant |
| pedalons.webPush.token | localStorage | Désinscrire ce navigateur des notifications push lorsque vous vous déconnectez ou les coupez | Jusqu'à votre déconnexion ou la coupure des notifications |
| Données de messagerie Firebase | Stockage du navigateur (site web) | Inscription de ce navigateur aux notifications push, uniquement une fois que vous les avez activées | Jusqu'à l'effacement des données de votre navigateur |
| pedalons.installBanner.dismissedAt | localStorage | Mémoriser que vous avez fermé la proposition d'installer le site comme une application | Persistant (la proposition revient après 90 jours) |
| pendingInvitationToken, pendingBiketeamMigrationRequest | sessionStorage | Garder une invitation à une équipe, ou une demande de transfert d'équipe depuis Biketeam, le temps de vous connecter | Jusqu'à la fermeture de l'onglet |

Les valeurs de langue, d'unités et de thème stockées dans votre navigateur ou dans l'application sont une copie locale : lorsque vous êtes connecté, la valeur enregistrée dans votre compte prévaut. La ligne « application mobile » correspond à un stockage effectué par l'application sur votre appareil, pas à un cookie de navigateur ; elle est strictement nécessaire pour vous garder connecté. Pour les autres données conservées par l'application mobile (préférences, caches, jeton push, journal des dernières actions et rapports d'erreur en attente), et pour les jetons stockés par les extensions Karoo et Garmin, voir « Données stockées localement sur votre appareil » à la section 1. L'application mobile et le site n'intègrent que la partie messagerie de Google Firebase, utilisée pour les notifications push : aucun composant Firebase d'analyse, de publicité ou de suivi.

**Nous n'utilisons aucun cookie de suivi, d'analyse ou de publicité.** Aucun consentement aux cookies n'est donc requis : le cookie de session est strictement nécessaire au fonctionnement du service, et le cookie lang ne fait que mémoriser un choix que vous avez fait. Notez que l'affichage d'une carte amène votre navigateur à demander des tuiles directement au fournisseur du style sélectionné — voir « Affichage des cartes et recherche d'adresses » à la section 4.

---

## 9. Sécurité

Nous mettons en œuvre les mesures suivantes pour protéger vos données :

- **Chiffrement en transit** : toutes les communications utilisent HTTPS (TLS).
- **Chiffrement au repos** : les jetons OAuth des services GPS sont chiffrés en AES-256-GCM.
- **Hachage des mots de passe** : les mots de passe sont stockés avec bcrypt, une transformation à sens unique volontairement lente — jamais en clair.
- **Hachage des secrets** : vos jetons de rafraîchissement de session, codes à usage unique (OTP), liens de vérification d'e-mail, de changement d'e-mail et de réinitialisation de mot de passe, et liens d'invitation dans une équipe ne sont stockés que sous forme de hachages irréversibles — nous ne conservons jamais la valeur d'origine. Deux secrets font exception et sont stockés en clair car nos systèmes doivent les rechercher directement : le jeton contenu dans votre lien d'abonnement calendrier (ICS), et le court code d'appairage affiché sur votre appareil GPS (qui expire après 10 minutes).
- **Cookies sécurisés** : le cookie de session est HttpOnly (les scripts ne peuvent pas le lire), Secure (envoyé uniquement en HTTPS) et SameSite=Lax. Il est envoyé quand vous arrivez depuis un lien sur un autre site, pour que la page s'ouvre déjà connectée ; notre serveur refuse toute requête qui modifierait des données si elle vient d'un autre site et n'est authentifiée que par ce cookie.
- **Stockage des jetons sur l'appareil** : sur mobile, le jeton de rafraîchissement est conservé dans le trousseau Apple ou le stockage chiffré Keystore d'Android ; sur les appareils GPS (Karoo, Garmin), les jetons sont conservés dans le stockage applicatif cloisonné de l'extension, sans chiffrement additionnel.
- **Isolation multi-tenant** : les données de chaque domaine sont isolées au niveau de la base de données ; les utilisateurs ordinaires et les administrateurs d'équipe ne peuvent jamais atteindre les données d'un autre domaine. Les administrateurs de la plateforme sont la seule exception documentée (voir section 4).
- **Limitation des codes de connexion** : nous plafonnons le nombre de codes de connexion à usage unique, de liens de réinitialisation et de liens de changement d'e-mail pouvant être demandés pour une même adresse sur une courte période ; chaque code ou lien expire rapidement et n'est utilisable qu'une fois, et un code de connexion est invalidé après 5 tentatives erronées.
- **Suppression** : commentaires (avec leurs réponses), photos et autres fichiers téléversés, points d'intérêt, lieux, inscriptions, clés d'accès, liens calendrier et connexions aux services GPS sont supprimés immédiatement et définitivement lorsque vous ou votre équipe les supprimez. Si une notification a été créée au sujet d'un commentaire ou d'une réponse, l'extrait conservé dans la notification subsiste jusqu'à l'expiration de la notification (90 jours), même si le commentaire est supprimé. Les équipes et contenus publiés (sorties, posts, itinéraires) sont quant à eux marqués comme supprimés : ils disparaissent immédiatement pour les membres et les visiteurs, tandis que les administrateurs d'équipe les voient toujours, marqués « Supprimé », et peuvent les restaurer ; l'effacement définitif est effectué sur demande (voir section 6). Un compte supprimé est en revanche effacé immédiatement (voir section 7).
- **Sauvegardes sur un serveur séparé** : réalisées chaque nuit et poussées via un tunnel chiffré par un canal à sens unique, de sorte qu'une compromission du serveur de production ne puisse ni lire ni supprimer l'historique de sauvegarde.
- **Journaux serveur** : conservés uniquement pour la sécurité, la détection d'abus et le dépannage (voir section 1).

Aucun système n'est infaillible. Si vous constatez une activité suspecte sur votre compte, contactez-nous immédiatement.

---

## 10. Mineurs

Pedalons n'est pas destiné aux enfants de moins de 16 ans. Nous ne collectons pas sciemment de données personnelles de mineurs de moins de 16 ans. Si vous êtes parent et pensez que votre enfant nous a fourni des données, contactez-nous pour que nous les supprimions.

---

## 11. Modifications de cette politique

Nous pouvons mettre à jour cette politique pour refléter des changements dans nos pratiques ou dans la réglementation. En cas de modification substantielle :

- Nous publierons la version mise à jour sur cette page.
- Nous mettrons à jour la date de « dernière mise à jour » en haut de ce document.
- Pour les changements importants, nous vous informerons par e-mail.

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
