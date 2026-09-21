# Notifications évènementielles

> Écrit le 18 septembre 2026. **Plan actif.** L'avancement, phase par phase, est tenu dans le
> ledger dédié : [`2026-09-18-notifications-ledger.md`](2026-09-18-notifications-ledger.md). Ce
> document porte la conception et ses arbitrages ; le ledger porte l'état.

Reprend et remplace deux entrées ouvertes : le « Versatile notification system » de
[`BACKLOG.md`](../../BACKLOG.md) (P3) et le §4.2 « Notifications push » de
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
                              NotificationDispatchScheduler (toutes les 15 s)
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

Le service métier appelle `NotificationPublisher.publish(event, actor)` avec un `record` du type
scellé `NotificationEvent`. Une ligne `notification_events` est écrite **dans la même transaction**
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

`NotificationDispatchScheduler` réclame un évènement `PENDING` (`for update skip locked` +
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
(un import ou une saisie rétroactive ne réveille personne). La migration biketeam crée ses entités
sans passer par les services : elle ne produit aucun évènement.

Les quatre visibilités (`TEAM`, `PUBLIC_UNLISTED`, `PUBLIC`) sont toutes lisibles par les membres,
d'où « membres de l'équipe » sans autre filtre. Un brouillon n'est jamais notifié.

## 5. Canaux

| Canal | Statut | Remarques |
|---|---|---|
| `IN_APP` | phase 1 | **Toujours actif, non configurable.** La ligne `notifications` *est* l'entrée de la boîte de réception. Les préférences ne gouvernent que les canaux qui interrompent. |
| `EMAIL` | phase 1, **désactivé par défaut** | `pedalons.notifications.email.enabled`. Un seul gabarit générique `notification` (fr/en) — voir §6. |
| `PUSH` | phase 4, **en production depuis le 21 septembre 2026** | FCM (Android + iOS via APNs). Table `push_devices`, enregistrement du jeton, purge sur `UNREGISTERED`. Disponible seulement avec `PEDALONS_PUSH_ENABLED=true` *et* un compte de service lisible. |
| Web Push, webhook d'équipe, résumé | plus tard | Le webhook n'est pas un canal *par destinataire* : il se branchera à l'étage 2, sur l'évènement. |

Un canal n'est proposé (dans les préférences) et n'engendre de livraisons que s'il est **disponible**
côté serveur : implémenté *et* activé par configuration. Un environnement sans compte de service FCM
(dev, test) ne crée aucune ligne `PUSH` — sinon elles s'empileraient sans jamais partir.

## 6. E-mail : un gabarit générique

Les e-mails existants ont chacun leur gabarit Brevo (un ID par langue) doublé d'un miroir Qute. À six
types aujourd'hui, et davantage demain, ce serait 2 × N gabarits Brevo maintenus à la main. Les
notifications utilisent donc **un seul** gabarit, `notification`, dont les paramètres sont déjà
rendus : `title`, `body`, `ctaLabel`, `ctaUrl`, `appName`, `recipientName`, `preferencesUrl`. Le
texte propre à chaque type vit dans `texts_{fr,en}.properties`, côté serveur, en un seul endroit.

Conséquence d'exploitation : **deux gabarits Brevo à créer** (fr, en) et leurs IDs à renseigner dans
`pedalons.email.brevo.templates.notification.{fr,en}` avant d'activer le canal en production.

Pourquoi le canal est désactivé par défaut : une base locale issue de la migration biketeam contient
des milliers d'adresses réelles, et la première sortie publiée en production écrirait à toute une
équipe. L'activer est une décision produit, pas un effet de bord d'un déploiement.

## 7. Préférences

`notification_preferences(user_id, type, channel, enabled)`, unique sur le triplet. Seules les
**dérogations** comptent : l'absence de ligne vaut le défaut du type (`NotificationType.defaultChannels`).
Changer un défaut dans le code change donc le comportement de tous ceux qui n'y ont pas touché — c'est
voulu.

`GET /api/notifications/preferences` renvoie la matrice complète (types × canaux *disponibles*), avec
pour chaque case la valeur effective et le défaut. `PUT` accepte une liste de cases.

## 8. Multi-tenant

- Chaque ligne porte `domain_id`, et chaque requête de lecture filtre par domaine **et** par
  destinataire.
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

La liste lit l'instantané par une jointure `fetch` sur l'évènement : son coût ne dépend pas de la taille
de la page (`NotificationQueryCountTest`, invariant des `…QueryCountTest`).

Pas de temps réel en phase 1 : les clients sondent `unread-count` (au focus, et toutes les minutes au
plus). Un flux SSE viendra si le besoin se confirme.

## 10. Rétention

Nuitamment, les évènements plus vieux que `pedalons.notifications.retention-days` (90 j par défaut)
sont supprimés, et avec eux, en cascade, leurs notifications et livraisons. Un évènement bloqué en
`PROCESSING` (crash entre réclamation et fin) repasse `PENDING` ; de même une livraison bloquée en
`SENDING`.

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
