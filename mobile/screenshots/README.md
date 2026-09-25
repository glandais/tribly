# Captures des stores

Quatre temps, tous scriptés, depuis `mobile/` :

```bash
python3 screenshots/seed.py                 # 1. les clubs de démo sur staging (dates relatives à aujourd'hui)
./screenshots/capture.sh                    # 2. captures brutes -> screenshots/flat/<appareil>/<locale>/
kou generate screenshots/koubou/iphone.yaml # 3. cartes Koubou  -> screenshots/koubou/out/<appareil>/…
kou generate screenshots/koubou/ipad.yaml
./screenshots/assemble.sh                   # 4. jeu final      -> screenshots/appstore/<type>/<locale>/ (Git LFS)
```

Seul `appstore/` est versionné, en **Git LFS** (`.gitattributes`). Les captures brutes, les
rendus Koubou et `accounts.local.json` (les mots de passe des comptes de démo) sont ignorés.

## 1. Données de démo — `seed.py`

Deux clubs fictifs autour du lac d'Annecy, un par langue de la fiche (`VC du Lac d'Annecy`,
`Annecy Lakeside Cycling`), en visibilité `TEAM` sur **staging** : sept parcours tracés par le
Valhalla de staging (dénivelé et cols calculés par le serveur), quatre sorties à venir à trois
groupes d'allure, un voyage en deux étapes, des publications, des annonces, des commentaires. Huit
membres fictifs communs aux deux clubs, et un compte « spectateur » par langue — celui sous lequel
on capture, membre de son club seulement.

Chaque passage **supprime et recrée** les deux clubs : les dates restent relatives au jour, et les
slugs restent les mêmes. Capturer juste après avoir semé.

Tout passe par l'API, sauf deux requêtes SQL sur la base de staging (`ssh pedalons@pedalons.fr`) :

- l'inscription envoie toujours un lien de vérification et aucun endpoint ne crée un compte
  vérifié. Le script s'inscrit normalement, remplace le hash du jeton en attente par celui d'un
  jeton qu'il connaît, et vérifie par l'API. Les adresses sont des alias `+pdl-demo-…` d'une vraie
  boîte : aucun mail ne rebondit sur le relais SMTP ;
- l'organisateur (`Julien Berthet`) est passé `PLATFORM_ADMIN`, seul moyen d'ajouter des membres
  à un club sans invitation par mail.

Il écrit aussi `plan.json` : les écrans à capturer, avec les slugs de ce passage.

## 2. Captures brutes — `capture.sh`

L'app est construite en **mode capture** (`--dart-define=SCREENSHOTS=true`,
`lib/screenshots/screenshot_mode.dart`) contre staging. Chaque capture est un lancement : le
script écrit `Documents/screenshot.json` dans le conteneur de l'app (écran, compte), l'app se
connecte et ouvre l'écran par le chemin des liens profonds, avec ses vrais parents. Aucun tap.

Pourquoi un fichier : sur iOS, `Platform.environment` est vide côté Dart, `shared_preferences`
ignore les arguments de lancement, et le `cfprefsd` du simulateur met en cache les préférences de
l'app par-dessus toute édition du fichier. Les trois ont été essayés.

Un seul simulateur à la fois (iPhone 17 Pro Max, puis iPad Pro 13" M4, éteint en partant),
barre d'état figée à 9:41, app réinstallée à chaque changement de langue (EasyLocalization garde
la dernière). Une capture est prise quand deux images successives sont identiques, après le délai
`wait` de l'écran dans `plan.json` — les cartes continuent de recevoir des tuiles après la
première image stable.

L'accueil n'est pas capturé sur iPad : sa mise en page large ajoute les « dernières publications »
de tous les clubs publics de staging, donc le contenu de vraies personnes. Le voyage le remplace.

## 3–4. Cartes et assemblage

`koubou/templates/hero.html` : fond bleu de la marque, filet orange, titre et sous-titre, appareil
qui sort par le bas. Les titres se traduisent dans `koubou/koubou-strings.xcstrings`, clé = la
phrase anglaise. Ne citer dans un titre que ce que l'app fait (directive 2.3.1).

`assemble.sh` aplatit les rendus vers `appstore/IPHONE_65/` (1242×2688) et
`appstore/IPAD_PRO_3GEN_129/` (2048×2732), et refuse un jeu aux mauvaises dimensions, avec un
canal alpha (`IMAGE_ALPHA_NOT_ALLOWED`) ou anormalement léger.
