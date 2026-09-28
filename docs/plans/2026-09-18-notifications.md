# Notifications évènementielles

> Écrit le 18 septembre 2026. **Phases 1 à 5 en production depuis le 21 septembre 2026** ; le Web
> Push est dans `develop` depuis le 28 septembre. Les trois points encore ouverts (recette du
> webhook, `FCM_WEB_*` en production, décision sur l'e-mail) sont repris au §8.3 de
> [`docs/NEXT.md`](../NEXT.md). L'avancement, phase par phase, est tenu dans le ledger dédié :
> [`2026-09-18-notifications-ledger.md`](2026-09-18-notifications-ledger.md). Ce document porte la
> conception et ses arbitrages ; le ledger porte l'état.

Reprend et remplace deux entrées ouvertes : le « Versatile notification system » de
[`BACKLOG.md`](../BACKLOG.md) (P3) et le §4.2 « Notifications push » de
[`docs/NEXT.md`](../NEXT.md). Le push n'est plus un chantier à part : c'est un canal parmi d'autres
d'un même pipeline.

## 1. Ce qu'on veut

- Un **évènement métier** (une sortie est publiée, une sortie est annulée, on répond à mon
  commentaire…) produit **une notification par destinataire**.
- Une notification est **fortement typée** : un type fermé, connu du compilateur, pas une chaîne
  libre ni un titre pré-rendu.
- Une notification est livrée sur **un ou plusieurs canaux** : la boîte de réception dans
  l'application (web et mobile), l'e-mail, le push mobile ; plus tard le Web Push, un webhook
  d'équipe, un résumé quotidien.
- Chaque membre choisit, **par type et par canal**, ce qu'il reçoit.

## 2. Le pipeline en trois étages

```
 service métier ──(même transaction)──▶ notification_events      (outbox, 1 ligne / évènement)
                                               │
                                  NotificationScheduler (toutes les 15 s)
                                               │  étage 2 : fan-out
                                               ▼
                                         notifications             (1 ligne / destinataire)
                                               │  = la boîte de réception (IN_APP)
                                               ▼
                                     notification_deliveries       (1 ligne / destinataire × canal hors-app)
                                               │  étage 3 : envoi
                                               ▼
                                 NotificationChannelSender (EMAIL, puis PUSH…)
```

### Étage 1 — publier (dans la transaction métier)

Le service métier appelle `NotificationPublisher.publish(event, team, actor)` (acteur nullable ; une
surcharge avec une `Duration` diffère l'évènement) avec un `record` du type scellé `NotificationEvent`. Une ligne `notification_events` est écrite **dans la même transaction**
que la modification métier : une sortie annulée dont la transaction échoue ne notifie personne, et
une sortie annulée qui est commitée notifiera même si l'application redémarre dans la seconde
(*transactional outbox*).

Ce que l'étage 1 ne fait **jamais** : chercher des destinataires, rendre un texte, parler au réseau.
La transaction de l'utilisateur ne paie qu'un `INSERT`. C'est l'inverse de ce que font aujourd'hui
les e-mails d'invitation et de contact (envoyés en synchrone, un échec SMTP annule la transaction) —
acceptable pour un e-mail unique que l'utilisateur attend, inacceptable pour 2 000 destinataires.

L'insertion est un `INSERT … ON CONFLICT (dedup_key) DO NOTHING` natif. Un doublon ne doit **jamais**
lever d'exception : une violation de contrainte ferait échouer la mise à jour de la sortie elle-même.

« Une seule fois » vaut pour les notifications **envoyées** : un évènement `SKIPPED` ou `FAILED`
n'a notifié personne, et rend sa clé (suffixée de son id). Une sortie publiée, repassée en brouillon
avant le tick puis republiée pour de bon est donc annoncée ; sinon elle ne l'aurait jamais été.

### Étage 2 — fan-out (scheduler, hors requête)

`NotificationScheduler` (tick de 15 s) fait réclamer par `NotificationDispatchService` un évènement `PENDING` (`for update skip locked` +
compare-and-set, le même schéma que `user_exports`), puis dans une transaction :

1. Désérialise le payload vers son `record` et le passe à `NotificationRecipientResolver`, un
   `switch` **exhaustif** sur l'interface scellée. Ajouter un type sans le traiter ne compile pas.
2. Le résolveur **relit l'entité** et décide si l'évènement est toujours pertinent : une sortie
   publiée puis dépubliée dans les 15 s, supprimée, ou dont la date est passée ⇒ `SKIPPED`.
3. Il fige un **instantané** du sujet sur la ligne d'évènement (nom et slug de l'équipe, type, nom,
   slug et date du sujet, nom de l'acteur, extrait) ainsi que le site (URL de base, nom) à utiliser
   dans les liens.
4. Il renvoie les destinataires. On retire l'acteur, les comptes supprimés et, en garde-fou commun,
   toute personne qui n'est plus membre de l'équipe.
5. Une ligne `notifications` par destinataire, puis une ligne `notification_deliveries` par canal
   hors-app **activé** pour ce destinataire.

Un fan-out qui échoue repasse `PENDING` avec un `next_attempt_at` reculé (exponentiel, comme les
livraisons) : retenté dans le même tick, un incident passager consommerait toutes les tentatives en
quelques millisecondes.

### Étage 3 — envoi (scheduler, par lots)

Chaque canal hors-app a un `NotificationChannelSender`. Un tick réclame un lot de livraisons
`PENDING` échues (`next_attempt_at <= now`), envoie **hors transaction**, puis marque `SENT`, ou
replanifie avec un recul exponentiel, ou `FAILED` au-delà de `max-attempts`. Un envoi réussi dont le
marquage échoue sera renvoyé : on accepte le « au moins une fois », pas le « peut-être jamais ».

Un worker qui meurt en plein travail (un déploiement) laisse des évènements `PROCESSING` et des
livraisons `SENDING`. Une récupération toutes les 5 min les remet en file passé
`stuck-after-minutes` — ou les passe `FAILED` s'ils ont épuisé leurs tentatives. Pas la nuit : une
annulation bloquée jusqu'à 4 h serait écartée comme passée.

## 3. Le typage

| Couche | Porteur du type |
|---|---|
| Java, producteurs | `sealed interface NotificationEvent permits RidePublished, RideCancelled, …` — des `record` qui ne portent que des identifiants |
| Java, fan-out | `switch` exhaustif sur l'interface scellée dans `NotificationRecipientResolver` |
| Base | `notification_events.type` (`varchar`, nom de l'enum) + `payload` (`jsonb`, le `record` sérialisé) |
| API | `NotificationType` (enum OpenAPI) + champs structurés (`teamSlug`, `subjectType`, `subjectSlug`…) |
| Clients | le client choisit libellé, icône et route **d'après le type** ; aucun texte pré-rendu ne transite |

Le payload est volontairement minimal (des identifiants) : c'est la relecture à l'étage 2 qui fait
foi. Il existe pour les types futurs qui auront besoin de plus qu'un identifiant (une modification de
sortie qui dit *ce qui* a changé, un groupe complet qui dit *lequel*).

Les textes rendus côté serveur (sujet et corps d'e-mail, titre et corps de push) viennent de
`notifications/texts_{fr,en}.properties`, indexés par type. Les clients, eux, localisent à partir du
type et des champs structurés — le même principe que les `ErrorCode`.

## 4. Types de la première passe

| Type | Déclencheur | Destinataires | Canaux par défaut |
|---|---|---|---|
| `RIDE_PUBLISHED` | sortie créée publiée, passée de brouillon à publiée, ou publiée par `PublicationPublishScheduler` | membres de l'équipe | in-app, push |
| `RIDE_CANCELLED` | statut passé à `CANCELLED` | inscrits de tous les groupes | in-app, e-mail, push |
| `TRIP_PUBLISHED` | idem sorties | membres de l'équipe | in-app, e-mail, push |
| `TRIP_CANCELLED` | idem sorties | inscrits au voyage | in-app, e-mail, push |
| `POST_PUBLISHED` | idem sorties | membres de l'équipe | in-app |
| `COMMENT_REPLY` | réponse à un commentaire | auteur du commentaire parent | in-app, push |

Déduplication par `TYPE:idSujet` : une sortie publiée, dépubliée puis republiée ne notifie qu'une
fois ; une annulation, qu'une fois. Une sortie ou un voyage dont la date est passée ne notifie pas
(un import ou une saisie rétroactive ne réveille personne). La migration biketeam passe par les
services producteurs, mais sous `NotificationPublisher.silently` : elle ne produit aucun évènement.

Les trois visibilités (`TEAM`, `PUBLIC_UNLISTED`, `PUBLIC`) sont toutes lisibles par les membres,
d'où « membres de l'équipe » sans autre filtre. Un brouillon n'est jamais notifié.

## 5. Canaux

| Canal | Statut | Remarques |
|---|---|---|
| `IN_APP` | phase 1 | **Toujours actif, non configurable.** La ligne `notifications` *est* l'entrée de la boîte de réception. Les préférences ne gouvernent que les canaux qui interrompent. |
| `EMAIL` | phase 1, **désactivé par défaut** | `pedalons.notifications.email.enabled`. Un seul gabarit générique `notification` (fr/en) — voir §6. |
| `PUSH` | phase 4, **en production depuis le 21 septembre 2026** | FCM (Android + iOS via APNs). Table `push_devices`, enregistrement du jeton, purge sur `UNREGISTERED`. Disponible seulement avec `PEDALONS_PUSH_ENABLED=true` *et* un compte de service lisible. |
| `PUSH`, plateforme `WEB` | dans `develop` depuis le 28 septembre 2026, `FCM_WEB_*` à renseigner en production | Le Web Push n'est **pas un canal de plus** : le navigateur est un appareil `push_devices` de plateforme `WEB`, servi par le même FCM (message *data only*, affiché par `frontend/public/sw.js`). Proposé seulement si `ConfigDto.webPush` est rempli : canal disponible *et* `FCM_WEB_*` configurés (app web Firebase du même projet). Sur iOS, n'existe que pour le site installé sur l'écran d'accueil (16.4+). |
| Webhook d'équipe, résumé | phase 5 (§12) | Le webhook n'est pas un canal *par destinataire* : il se branche à l'étage 2, sur l'évènement. |

Un canal n'est proposé (dans les préférences) et n'engendre de livraisons que s'il est **disponible**
côté serveur : implémenté *et* activé par configuration. Un environnement sans compte de service FCM
(dev, test) ne crée aucune ligne `PUSH` — sinon elles s'empileraient sans jamais partir.

## 6. E-mail : un gabarit générique

À l'écriture, les e-mails existants avaient chacun leur gabarit Brevo (un ID par langue) doublé d'un
miroir Qute. À six types, et davantage demain, ce serait 2 × N gabarits maintenus à la main. Les
notifications utilisent donc **un seul** gabarit, `notification`, dont les paramètres sont déjà
rendus : `title`, `body`, `ctaLabel`, `ctaUrl`, `appName`, `recipientName`, `preferencesUrl`. Le
texte propre à chaque type vit dans `texts_{fr,en}.properties`, côté serveur, en un seul endroit.

Depuis le 22 septembre 2026, Brevo est remplacé par le relais SMTP de Scaleway Transactional Email :
les gabarits Qute de `backend/src/main/resources/templates/mail/` (`notification.{fr,en}` et
`notification-digest.{fr,en}`, en `.html` et `.txt`) sont ceux qui partent, et il n'y a plus aucun
gabarit à créer ni d'identifiant à renseigner hors du dépôt avant d'activer le canal.

Pourquoi le canal est désactivé par défaut : une base locale issue de la migration biketeam contient
des milliers d'adresses réelles, et la première sortie publiée en production écrirait à toute une
équipe. L'activer est une décision produit, pas un effet de bord d'un déploiement.

## 7. Préférences

`notification_preferences(user_id, type, channel, enabled)`, unique sur le triplet. Seules les
**dérogations** comptent : l'absence de ligne vaut le défaut du type (`NotificationType.defaultChannels`).
Changer un défaut dans le code change donc le comportement de tous ceux qui n'y ont pas touché — c'est
voulu.

`GET /api/notifications/preferences` renvoie la matrice complète (types × canaux *disponibles*), avec
pour chaque case la valeur effective et le défaut. `PUT` accepte une liste de cases. Depuis la
phase 5, les deux portent aussi `teams` (les équipes coupées) et `emailDigest` (le résumé, §12).

## 8. Multi-tenant

- `notification_events` et `notifications` portent `domain_id` ; les autres tables (livraisons,
  préférences, appareils, réglages, webhooks) héritent du domaine par leur utilisateur, leur
  notification ou leur équipe. Chaque lecture de la boîte filtre par domaine **et** par destinataire.
- Le scheduler n'a pas de requête HTTP, donc pas de `DomainResolver`. Le site utilisé dans les liens
  est résolu **à partir de l'équipe** : un alias de domaine épinglé sur cette équipe s'il en existe
  un actif, sinon le domaine parent. C'est plus juste que l'instantané de la requête (ce que fait
  `user_exports`) : la sortie publiée par un organisateur sur le domaine parent doit amener les
  membres sur le site de leur club.
- Langue et fuseau : ceux du destinataire, sinon `fr` et `Europe/Paris`.

## 9. API

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/api/notifications?page&size&unreadOnly` | la boîte de réception, récente d'abord, avec `unreadCount` |
| `GET` | `/api/notifications/unread-count` | pastille de la cloche ; appel bon marché, sondé par les clients |
| `POST` | `/api/notifications/{id}/read` | marquer lue (idempotent) |
| `POST` | `/api/notifications/read-all` | tout marquer lu |
| `GET` | `/api/notifications/preferences` | matrice type × canal |
| `PUT` | `/api/notifications/preferences` | modifier des cases |
| `POST` | `/api/push-devices` | enregistrer un appareil push (phase 4) |
| `DELETE` | `/api/push-devices/{token}` | oublier un appareil push |
| `GET`, `PUT`, `DELETE` | `/api/teams/{teamSlug}/webhook` | webhook d'équipe (phase 5, §12) |
| `POST` | `/api/teams/{teamSlug}/webhook/test` | message d'essai |

La liste lit l'instantané par une jointure `fetch` sur l'évènement : son coût ne dépend pas de la taille
de la page (`NotificationQueryCountTest`, invariant des `…QueryCountTest`).

Pas de temps réel en phase 1 : les clients sondent `unread-count` (au focus, et toutes les minutes au
plus). Un flux SSE viendra si le besoin se confirme.

## 10. Rétention

Chaque nuit (4 h 15), les évènements plus vieux que `pedalons.notifications.retention-days` (90 j
par défaut) sont supprimés avec leurs notifications, livraisons et livraisons de webhook (suppression
explicite, par tranches de 500 — `NotificationRetentionService`). La remise en file des évènements
`PROCESSING` et des livraisons `SENDING` bloqués n'est pas nocturne : voir §2, étage 3 (toutes les
5 min).

## 11. Ce qui est délibérément écarté

- **Pas d'événements CDI** (`Event<>` / `@Observes(during = AFTER_SUCCESS)`) comme transport : un
  observateur après commit perd l'évènement si l'application s'arrête entre les deux. La table est le
  transport ; un appel explicite au publieur est aussi plus lisible qu'un observateur caché.
- **Pas de broker** (Kafka, RabbitMQ) : un seul réplica, PostgreSQL suffit, et la file est
  inspectable en SQL.
- **Pas de texte pré-rendu dans l'API** : il figerait la langue au moment de l'envoi et empêcherait
  les clients d'afficher la notification avec leurs propres composants.
- **Pas de préférences par équipe** en phase 1 (« pas de notifications de l'équipe X ») : la table
  pourra gagner une colonne `team_id` nullable sans rien casser.

## 12. Phase 5 — nouveaux types, préférences par équipe, webhook, résumé

> Écrit le 21 septembre 2026, avant la phase 5. État dans le ledger.

### Cinq types de plus

| Type | Déclencheur | Destinataires | Canaux par défaut | Clé de dédup |
|---|---|---|---|---|
| `RIDE_REMINDER` | `RideReminderScheduler`, toutes les heures : sortie publiée qui part dans 20 à 24 h | inscrits | in-app, push | `RIDE_REMINDER:id:dateTime` |
| `RIDE_UPDATED` | date ou point de départ changés sur une sortie publiée | inscrits | in-app, e-mail, push | `RIDE_UPDATED:id`, **rendue une fois l'évènement traité** |
| `RIDE_JOINED` | inscription à un groupe | créateur de la sortie et meneur du groupe | in-app | `RIDE_JOINED:idInscription` |
| `COMMENT_ON_MY_PUBLICATION` | commentaire **de premier niveau** | auteur de la sortie, du voyage, de l'article ou du parcours | in-app, push | `…:idCommentaire` |
| `TEAM_INVITATION` | invitation envoyée | le compte à l'adresse **vérifiée** qui porte l'adresse invitée, s'il existe et n'est pas déjà membre | in-app, push | `TEAM_INVITATION:idInvitation` |

- **Le rappel** porte la date dans sa clé : une sortie déplacée a droit à un nouveau rappel, et le
  résolveur écarte le rappel dont la date n'est plus celle de la sortie. Une fenêtre de quatre heures
  plutôt qu'un instant : un tick manqué (un redéploiement) ne fait pas perdre le rappel, et la clé
  empêche le doublon. Une sortie créée moins de 20 h avant son départ n'a pas de rappel — ceux qui s'y
  inscrivent viennent de la voir.
- **La modification** porte l'état *d'avant* (date, identifiant du lieu) et le résolveur compare à
  l'état courant : une modification défaite avant l'envoi ne notifie personne. Elle part avec
  **cinq minutes** de retard (`pedalons.notifications.update-delay-seconds`), et sa clé reste prise
  tant qu'elle attend : trois retouches successives font une seule notification, qui compare le
  premier état au dernier. La clé est rendue une fois l'évènement traité — c'est la seule
  différence avec les autres types (`NotificationEvent.coalescesWhilePending`), partagée depuis avec
  `CONTENT_REPORTED`. Ce qui a changé est
  figé dans l'instantané (`changes`) et exposé à l'API (`NotificationDto.changes`). Les horaires de
  groupe n'en font pas partie.
- **L'inscription** ne passe pas par le push par défaut : une sortie populaire en ferait trente. Le
  nom du groupe voyage dans `excerpt`.
- **Le commentaire** ne concerne que le premier niveau : une réponse notifie déjà l'auteur du
  commentaire parent (`COMMENT_REPLY`), et l'auteur de la publication qui répond dans un fil ne
  recevrait que du bruit.
- **L'invitation** est publiée pour toute invitation, compte existant ou non : c'est le résolveur
  qui cherche le compte, hors requête — vérifié seulement : un compte non vérifié peut être la
  revendication d'un tiers sur l'adresse. L'invitant ne peut donc rien en déduire — la règle de
  `TeamInvitationService` (« créer une invitation ne dit pas si un compte existe ») tient. Le
  destinataire n'étant pas membre, c'est, avec `CONTENT_REPORTED` (admins de
  plateforme), un type exempté du garde-fou « encore membre ». Pas
  d'e-mail par défaut : l'invitation a déjà le sien. Nouveau sujet `TEAM`, qui ouvre la liste des
  équipes, là où les invitations en attente s'acceptent.

`NotificationType` gagne une audience (`Audience.BROADCAST` pour les trois `…_PUBLISHED`,
`PERSONAL` sinon), un drapeau `urgent` (le résumé ne peut pas attendre — annulations, modification,
rappel, et depuis retrait de groupe et signalement) et `isRelayedToTeamWebhook()` (les `…_PUBLISHED`,
les annulations et `RIDE_UPDATED`), que le webhook d'équipe consulte (voir plus bas).

### Types ajoutés après la phase 5

| Type | Déclencheur | Destinataires | Canaux par défaut | Clé de dédup |
|---|---|---|---|---|
| `RIDE_GROUP_REMOVED` (28 septembre 2026, `34431a89`) | retrait d'un groupe qui a des inscrits | ses inscrits encore membres et non réinscrits ailleurs dans la sortie | in-app, e-mail, push | `RIDE_GROUP_REMOVED:idGroupe` |
| `CONTENT_REPORTED` (24 septembre 2026, `4486bf0b`) | signalement ouvert | modérateurs de l'équipe et admins de plateforme du domaine, jamais le signaleur ni la cible | in-app, push, e-mail | `CONTENT_REPORTED:idÉquipe:type:idCible`, rendue une fois l'évènement traité |

Tous deux `PERSONAL` et urgents. `RIDE_GROUP_REMOVED` transporte le nom du groupe et les inscrits
dans son `record` : à l'étage 2, le groupe n'existe plus. `CONTENT_REPORTED` est publié sans acteur,
ouvre un nouveau sujet `REPORT` (la file de modération de l'équipe) et ne porte pas d'extrait : un
push s'affiche sur l'écran verrouillé.

### Préférences par équipe : un interrupteur, pas une matrice

Le plan prévoyait une colonne `team_id` sur `notification_preferences`, donc une matrice type × canal
*par équipe*. Écarté : personne ne réglera 11 × 2 cases (13 × 2 aujourd'hui) pour chacune de ses équipes, et le besoin
exprimé est « plus rien de l'équipe X ». Donc une table `notification_team_mutes(user_id, team_id)` :
une équipe coupée ne produit **plus aucune notification des types `broadcast`** pour ce membre, boîte
comprise. Les types personnels (annulation d'une sortie où l'on est inscrit, rappel, réponse,
invitation…) passent toujours : couper les annonces d'un club ne doit pas faire rater l'annulation de
la sortie où l'on va demain.

### Résumé quotidien

Un réglage par membre (`notification_settings.email_digest`), faux par défaut. Coché, les livraisons
`EMAIL` des types non urgents sont créées avec `digest = true` et échues au prochain **7 h** du fuseau
du destinataire ; l'envoi ordinaire les ignore, et un tick du résumé (toutes les 15 min) les réclame
**par destinataire** pour en faire un seul e-mail (`notification-digest`, Qute fr/en — les gabarits
Brevo prévus à l'écriture n'ont plus d'objet depuis le passage au relais SMTP de Scaleway TEM, §6). Un résumé qui échoue est rejoué en bloc.
Les types urgents partent tout de suite même en mode résumé : l'annulation d'une sortie du lendemain
matin arriverait après le départ.

### Webhook d'équipe

Un par équipe (`team_webhooks`), réglé par les administrateurs de l'équipe (`TEAM` / `UPDATE`) depuis
le web. Branché à l'étage 2, sur l'**évènement** : après le fan-out d'un type annoncé à toute
l'équipe (publications, annulations, modification), une ligne `team_webhook_deliveries`, envoyée par
le tick avec le même recul exponentiel — même quand l'évènement n'a aucun destinataire individuel.

- **Format déduit de l'URL, jamais stocké** : `hooks.slack.com` ⇒ `{"text"}`,
  `discord.com/api/webhooks` ⇒ `{"content"}` (mentions coupées), un chemin `/hooks/<26 car.>` ⇒
  Mattermost, sinon un JSON structuré (`type`, équipe, sujet, URL, acteur, texte). Aucun réglage de
  format à se tromper.
- **Mattermost** est auto-hébergé : pas d'hôte reconnaissable, d'où le chemin. Il reçoit la charge
  Slack, que sa [couche de compatibilité](https://developers.mattermost.com/integrate/webhooks/incoming/#slack-compatibility)
  lit (`{"text"}`, liens `<url|libellé>`), avec deux écarts : le gras s'écrit `**` (une étoile est
  de l'italique), et un `@channel`, `@here` ou `@all` écrit dans le texte **notifie tout le canal**
  — Slack, lui, ne l'interprète pas sans `link_names`. Chaque `@` est donc suivi d'une espace sans
  chasse, et le Markdown d'un nom de sortie est échappé. Un faux positif ne coûte rien : le JSON
  générique porte lui aussi un `text`. Si la détection se trompait un jour, une colonne `kind`
  nullable (null = automatique) et un sélecteur dans le formulaire suffiraient. La langue du texte est réglée sur le webhook.
- **SSRF** : `https` seulement ; à l'envoi, l'hôte est résolu et refusé s'il pointe vers une adresse
  de bouclage, privée, lien-local, multicast ou non routable ; redirections non suivies ; 10 s de
  délai. Un 4xx (hors 408 et 429) est définitif, pas rejoué.
- **L'URL est un secret** (celles de Slack et Discord suffisent à poster) : l'API ne la renvoie que
  masquée, et un `PUT` sans URL garde celle en place.
- `POST …/webhook/test` envoie un message d'essai tout de suite et rend le code HTTP obtenu.
