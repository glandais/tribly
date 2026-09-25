#!/usr/bin/env python3
"""Seeds the fictional demo clubs the store screenshots are taken on, on staging.

    python3 screenshots/seed.py            # create what is missing, then rebuild both clubs
    python3 screenshots/seed.py --dry-run  # print the plan, touch nothing

Two clubs around Lake Annecy, one per store locale (fr-FR, en-US), share the same
eight fictional members; each locale has its own viewer account, the one the
capture signs in as, member of its club only. Every run deletes and recreates the
two clubs, so dates stay relative to today: capture right after seeding.

Everything goes through the public API except two SQL statements run over SSH on
the staging database, both on accounts this script created:

- registration always e-mails a verification link, and no endpoint creates a
  verified user. The script registers normally, then swaps the pending token's
  hash for the hash of a token it knows, and verifies through the API — the
  account is created by the real code path, and no inbox is read. The addresses
  are plus-aliases of one real inbox, so no verification mail bounces;
- the organizer account is made PLATFORM_ADMIN, which lets it add members to a
  club (the API only adds members to a club that allows it, a flag only a
  platform admin can set).

Writes screenshots/accounts.local.json (git-ignored: passwords) and
screenshots/plan.json (the screens to capture, with this run's slugs).
"""

from __future__ import annotations

import base64
import hashlib
import json
import secrets
import subprocess
import sys
import urllib.error
import urllib.request
from datetime import datetime, time, timedelta
from pathlib import Path
from zoneinfo import ZoneInfo

API = "https://staging.pedalons.fr"
SSH = "pedalons@pedalons.fr"
DB_CONTAINER = "pedalons-staging-postgres"
INBOX = "gabriel.landais+pdl-demo-{key}@gmail.com"
TZ = ZoneInfo("Europe/Paris")

HERE = Path(__file__).resolve().parent
ACCOUNTS = HERE / "accounts.local.json"
PLAN = HERE / "plan.json"

DRY_RUN = "--dry-run" in sys.argv

# ---------------------------------------------------------------- people

# key -> display name. `julien` organizes both clubs (ADMIN, platform admin),
# `sophie` is an organizer; the viewers are the accounts the capture signs in as.
MEMBERS = {
    "julien": "Julien Berthet",
    "sophie": "Sophie Lambert",
    "thomas": "Thomas Girod",
    "lea": "Léa Dubois",
    "marc": "Marc Perrin",
    "emma": "Emma Rossi",
    "nicolas": "Nicolas Favre",
    "claire": "Claire Vidal",
}
VIEWERS = {"fr-FR": ("viewer-fr", "Camille Martin"), "en-US": ("viewer-en", "Alex Morgan")}

# ---------------------------------------------------------------- places

ANNECY = (45.9003, 6.1302)  # Le Pâquier
PLACES = {
    "paquier": {"fr": "Le Pâquier, Annecy", "en": "Le Pâquier, Annecy", "at": ANNECY,
                "address": "Avenue d'Albigny, 74000 Annecy"},
    "thorens": {"fr": "Place de Thorens-Glières", "en": "Thorens-Glières square", "at": (45.9957, 6.2478),
                "address": "74570 Thorens-Glières"},
}

# Waypoints, routed leg by leg with the staging Valhalla (/api/router).
ROUTES = [
    {"key": "lac", "surface": "ROAD", "profile": "BIKE",
     "fr": "Tour du lac d'Annecy", "en": "Lake Annecy loop",
     "md": {"fr": "Le grand classique, à plat : parfait pour une reprise ou une sortie récupération.",
            "en": "The flat classic: ideal to get back on the bike or for a recovery ride."},
     "via": [ANNECY, (45.8640, 6.1400), (45.8290, 6.2010), (45.7760, 6.2230), (45.8410, 6.2130),
             (45.8830, 6.1720), ANNECY]},
    {"key": "forclaz", "surface": "ROAD", "profile": "BIKE",
     "fr": "Col de la Forclaz par Talloires", "en": "Col de la Forclaz via Talloires",
     "md": {"fr": "Montée par Talloires, descente sur Doussard et retour par la rive ouest.",
            "en": "Up from Talloires, down to Doussard and back along the west shore."},
     "via": [ANNECY, (45.8830, 6.1720), (45.8410, 6.2130), (45.8134, 6.2466), (45.7760, 6.2230),
             (45.8640, 6.1400), ANNECY]},
    {"key": "semnoz", "surface": "ROAD", "profile": "BIKE",
     "fr": "Semnoz et col de Leschaux", "en": "Semnoz and Col de Leschaux",
     "md": {"fr": "Le Semnoz par Quintal, puis la descente sur Leschaux. Vue sur le Mont-Blanc au sommet.",
            "en": "The Semnoz from Quintal, then down to Leschaux. Mont Blanc views at the top."},
     "via": [ANNECY, (45.8290, 6.0880), (45.7990, 6.1010), (45.7720, 6.1130), (45.8640, 6.1400), ANNECY]},
    {"key": "bauges", "surface": "ROAD", "profile": "BIKE",
     "fr": "Boucle des Bauges", "en": "Bauges loop",
     "md": {"fr": "Leschaux, Lescheraines et le cœur des Bauges : une longue sortie vallonnée.",
            "en": "Leschaux, Lescheraines and the heart of the Bauges: a long rolling ride."},
     "via": [ANNECY, (45.8640, 6.1400), (45.7720, 6.1130), (45.7110, 6.1060), (45.7410, 6.1400),
             (45.7720, 6.1130), ANNECY]},
    {"key": "glieres", "surface": "GRAVEL", "profile": "GRAVEL",
     "fr": "Plateau des Glières en gravel", "en": "Glières plateau gravel",
     "md": {"fr": "Départ de Thorens, montée au plateau et pistes d'alpage. Pneus de 40 mm conseillés.",
            "en": "From Thorens up to the plateau and its alpine tracks. 40 mm tyres recommended."},
     "via": [(45.9957, 6.2478), (45.9650, 6.3310), (45.9757, 6.3197), (45.9957, 6.2478)]},
    {"key": "beaufort", "surface": "ROAD", "profile": "BIKE",
     "fr": "Annecy – Beaufort", "en": "Annecy to Beaufort",
     "md": {"fr": "Première étape : la vallée jusqu'à Albertville, puis la montée vers Beaufort.",
            "en": "Stage one: down the valley to Albertville, then up to Beaufort."},
     "via": [ANNECY, (45.7760, 6.2230), (45.6760, 6.3920), (45.7190, 6.5730)]},
    {"key": "roselend", "surface": "ROAD", "profile": "BIKE",
     "fr": "Beaufort – Cormet de Roselend", "en": "Beaufort to Cormet de Roselend",
     "md": {"fr": "Deuxième étape : le lac de Roselend et le Cormet, descente sur Bourg-Saint-Maurice.",
            "en": "Stage two: Lake Roselend and the Cormet, down to Bourg-Saint-Maurice."},
     "via": [(45.7190, 6.5730), (45.6880, 6.6720), (45.6180, 6.7690)]},
]

# ---------------------------------------------------------------- HTTP


class ApiError(Exception):
    def __init__(self, status: int, body: str):
        super().__init__(f"HTTP {status}: {body[:400]}")
        self.status = status
        self.body = body


def call(method: str, path: str, body=None, token: str | None = None):
    data = None if body is None else json.dumps(body).encode()
    req = urllib.request.Request(API + path, data=data, method=method)
    req.add_header("Accept", "application/json")
    if data is not None:
        req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(req, timeout=120) as resp:
            raw = resp.read().decode()
            return json.loads(raw) if raw else None
    except urllib.error.HTTPError as e:
        raise ApiError(e.code, e.read().decode(errors="replace")) from None


def sql(statement: str) -> str:
    cmd = ["ssh", SSH, f'docker exec -i {DB_CONTAINER} sh -c '
           f'\'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -At -v ON_ERROR_STOP=1\'']
    out = subprocess.run(cmd, input=statement, capture_output=True, text=True, check=True)
    return out.stdout.strip()


def media(markdown: str = "") -> dict:
    return {"markdown": markdown, "assets": {"images": [], "attachments": []}}


def point(lat: float, lon: float) -> dict:
    return {"type": "Point", "coordinates": [lon, lat]}


# ---------------------------------------------------------------- accounts


def load_accounts() -> dict:
    return json.loads(ACCOUNTS.read_text()) if ACCOUNTS.exists() else {}


def ensure_account(accounts: dict, key: str, name: str) -> dict:
    """Signs `key` in, registering and verifying it first if it doesn't exist."""
    acc = accounts.get(key) or {"email": INBOX.format(key=key), "password": secrets.token_urlsafe(18)}
    accounts[key] = acc
    try:
        auth = call("POST", "/api/auth/login", {"email": acc["email"], "password": acc["password"]})
    except ApiError as e:
        if e.status not in (400, 401):
            raise
        print(f"  registering {name} <{acc['email']}>")
        call("POST", "/api/auth/register", {"email": acc["email"], "displayName": name,
                                             "password": acc["password"], "acceptTerms": True})
        token = secrets.token_urlsafe(32)
        token_hash = base64.b64encode(hashlib.sha256(token.encode()).digest()).decode()
        updated = sql(
            "UPDATE auth_tokens SET token_hash = '%s' WHERE email = '%s' "
            "AND token_type = 'EMAIL_VERIFICATION' AND used_at IS NULL RETURNING id;"
            % (token_hash, acc["email"]))
        if not updated:
            raise RuntimeError(f"no pending verification token for {acc['email']}")
        auth = call("POST", "/api/auth/verify-email", {"token": token})
    acc["id"] = auth["user"]["id"]
    acc["token"] = auth["accessToken"]
    if auth["user"].get("displayName") != name:
        call("PUT", "/api/users/me", {"displayName": name}, acc["token"])
    return acc


# ---------------------------------------------------------------- routes


def routed_points(via: list, profile: str, token: str) -> list:
    points: list = []
    for (a, b) in zip(via, via[1:]):
        leg = call("POST", "/api/router", {"from": {"lat": a[0], "lng": a[1]},
                                           "to": {"lat": b[0], "lng": b[1]}, "profile": profile}, token)
        coords = leg["route"]["coordinates"]
        if points:
            coords = coords[1:]
        points += [{"lng": c[0], "lat": c[1]} for c in coords]
    return points


# ---------------------------------------------------------------- club


def next_weekday(weekday: int, at: time, min_days: int = 1) -> datetime:
    """The next `weekday` (Mon=0) at least `min_days` away, at local `at`."""
    today = datetime.now(TZ).date()
    d = today + timedelta(days=min_days)
    while d.weekday() != weekday:
        d += timedelta(days=1)
    return datetime.combine(d, at, TZ)


def iso(dt: datetime) -> str:
    return dt.astimezone(ZoneInfo("UTC")).isoformat().replace("+00:00", "Z")


def seed_club(locale: str, acc: dict, viewer: dict) -> list:
    lang = locale[:2]
    fr = lang == "fr"
    org = acc["julien"]["token"]
    name = "VC du Lac d'Annecy" if fr else "Annecy Lakeside Cycling"

    # One club per locale, rebuilt on every run.
    # Looked up in SQL: the organizer is a platform admin, so the API lists every
    # club on staging, and the demo club can fall past any page.
    quoted = name.replace("'", "''")
    for old in sql("SELECT slug FROM teams WHERE NOT deleted AND name = '%s';" % quoted).split():
        print(f"  deleting previous {old}")
        call("DELETE", f"/api/teams/{old}", token=org)
    # A deleted club keeps its slug (uk_teams_domain_slug), and slug generation
    # doesn't reliably step around it (HTTP 500). Free it so the club comes back
    # under the same slug on every run.
    sql("UPDATE teams SET slug = slug || '-deleted-' || id WHERE deleted AND name = '%s' "
        "AND slug NOT LIKE '%%-deleted-%%';" % quoted)
    about = ("Club cyclosportif du bassin annécien : sorties route et gravel toute l'année, "
             "trois groupes d'allure, un voyage par saison." if fr else
             "A road and gravel club around Lake Annecy: rides all year round, three pace groups, "
             "one trip per season.")
    team = call("POST", "/api/teams", {
        "name": name, "media": media(about), "visibility": "TEAM",
        "enableTrips": True, "enableAds": True, "enablePosts": True, "enableRides": True,
        "enableRoutes": True, "enableMemberDirectory": True, "geometry": point(*ANNECY)}, org)
    slug = team["slug"]
    print(f"  club {slug}")
    base = f"/api/teams/{slug}"

    roles = {"sophie": "ORGANIZER"}
    for key in [k for k in MEMBERS if k != "julien"]:
        call("POST", f"{base}/members", {"userId": acc[key]["id"], "role": roles.get(key, "MEMBER")}, org)
    call("POST", f"{base}/members", {"userId": viewer["id"], "role": "MEMBER"}, org)

    places = {}
    for key, p in PLACES.items():
        places[key] = call("POST", f"{base}/places", {
            "name": p[lang], "address": p["address"], "startPlace": True, "endPlace": True,
            "geometry": point(*p["at"])}, org)["id"]

    routes = {}
    for r in ROUTES:
        pts = routed_points(r["via"], r["profile"], org)
        preview = call("POST", "/api/gpx-previews/from-points", {"name": r[lang], "points": pts}, org)
        route = call("POST", f"/api/gpx-previews/{preview['id']}/routes/{slug}", {
            "name": r[lang], "media": media(r["md"][lang]), "surfaceType": r["surface"],
            "visibility": "TEAM"}, org)
        routes[r["key"]] = route["slug"]
        print(f"  route {route['slug']}  {preview['distance'] / 1000:.0f} km, +{preview['elevationGain']:.0f} m")

    def groups(route: str, leaders: tuple) -> list:
        names = (["Groupe rapide", "Groupe intermédiaire", "Groupe découverte"] if fr
                 else ["Fast group", "Intermediate group", "Discovery group"])
        speeds = [31, 27, 23]
        out = []
        for i, n in enumerate(names):
            g = {"name": n, "time": "08:30:00" if i == 0 else "08:45:00", "averageSpeed": speeds[i],
                 "maxParticipants": 12, "routeSlug": route}
            if leaders[i]:
                g["leaderId"] = acc[leaders[i]]["id"]
            out.append(g)
        return out

    rides_spec = [
        ("forclaz", "paquier", next_weekday(5, time(8, 30)), ("thomas", None, "sophie"),
         "Sortie du samedi : la Forclaz" if fr else "Saturday ride: the Forclaz",
         "Rendez-vous au Pâquier. Café à Talloires au retour pour ceux qui veulent." if fr else
         "Meet at Le Pâquier. Coffee in Talloires on the way back for those who want."),
        ("lac", "paquier", next_weekday(2, time(18, 15)), (None, None, None),
         "Tour du lac en semaine" if fr else "Midweek lake loop",
         "Sortie courte après le travail, éclairage obligatoire au retour." if fr else
         "Short after-work ride, lights required on the way back."),
        ("semnoz", "paquier", next_weekday(6, time(8, 30), 3), ("marc", None, None),
         "Dimanche au Semnoz" if fr else "Sunday on the Semnoz",
         "Grosse journée de montagne : prévoyez deux bidons et un coupe-vent pour la descente." if fr else
         "A big mountain day: bring two bottles and a windproof for the descent."),
        ("glieres", "thorens", next_weekday(5, time(9, 0), 8), (None, None, None),
         "Gravel aux Glières" if fr else "Glières gravel ride",
         "Pistes roulantes, quelques passages caillouteux au plateau." if fr else
         "Fast tracks, a few rocky sections on the plateau."),
    ]
    rides = []
    for route, place, when, leaders, title, text in rides_spec:
        ride = call("POST", f"{base}/rides", {
            "name": title, "media": media(text), "dateTime": iso(when), "status": "PUBLISHED",
            "visibility": "TEAM", "routeSlug": routes[route], "startPlaceId": places[place],
            "endPlaceId": places[place], "groups": groups(routes[route], leaders)}, org)
        rides.append(ride)
        print(f"  ride {ride['slug']}  {when:%a %d %b %H:%M}")

    # Registrations: the viewer rides the first ride, in the intermediate group.
    signups = {0: {"thomas": 0, "nicolas": 0, "emma": 0, "lea": 1, "marc": 1, "claire": 2, "sophie": 2},
               1: {"lea": 0, "claire": 0, "nicolas": 1},
               2: {"marc": 0, "thomas": 0, "emma": 1},
               3: {"nicolas": 0, "julien": 0, "lea": 1}}
    for i, ride in enumerate(rides):
        detail = call("GET", f"{base}/rides/{ride['slug']}", token=org)
        gids = [g["id"] for g in detail["groups"]]
        for key, g in signups[i].items():
            call("POST", f"{base}/rides/{ride['slug']}/groups/{gids[g]}/join", token=acc[key]["token"])
        if i == 0:
            call("POST", f"{base}/rides/{ride['slug']}/groups/{gids[1]}/join", token=viewer["token"])

    trip_day = next_weekday(5, time(8, 0), 15)
    trip = call("POST", f"{base}/trips", {
        "name": "Week-end dans le Beaufortain" if fr else "Beaufortain weekend",
        "media": media("Deux jours, deux étapes, une nuit à Beaufort. Voiture d'assistance pour les sacs." if fr
                       else "Two days, two stages, one night in Beaufort. A support car carries the bags."),
        "dateTime": iso(trip_day), "status": "PUBLISHED", "visibility": "TEAM",
        "stages": [
            {"name": "Annecy – Beaufort" if fr else "Annecy to Beaufort", "dateTime": iso(trip_day),
             "routeSlug": routes["beaufort"], "startPlaceId": places["paquier"],
             "media": media("Pique-nique à Albertville." if fr else "Picnic lunch in Albertville.")},
            {"name": "Cormet de Roselend" if fr else "Cormet de Roselend",
             "dateTime": iso(trip_day + timedelta(days=1)), "routeSlug": routes["roselend"],
             "media": media("Retour en train depuis Bourg-Saint-Maurice." if fr
                            else "Train back from Bourg-Saint-Maurice.")},
        ]}, org)
    for key in ["sophie", "thomas", "emma", "marc"]:
        call("POST", f"{base}/trips/{trip['slug']}/join", token=acc[key]["token"])

    now = datetime.now(TZ)
    posts = [
        ("Calendrier des sorties d'octobre" if fr else "October ride calendar",
         ("Les sorties du samedi partent désormais à **8 h 30** du Pâquier. Le mercredi soir, on garde "
          "le tour du lac tant qu'il fait jour.\n\nPensez à vous inscrire dans votre groupe : ça aide "
          "les capitaines de route à s'organiser." if fr else
          "Saturday rides now leave Le Pâquier at **8:30**. On Wednesday evenings we keep the lake loop "
          "while there is daylight.\n\nPlease sign up in your group: it helps the ride captains plan."),
         now - timedelta(days=2)),
        ("Nouveaux maillots du club" if fr else "New club jerseys",
         ("Les maillots 2027 sont commandés ! Livraison prévue mi-novembre, distribution à la sortie du "
          "samedi suivant." if fr else
          "The 2027 jerseys are ordered! Delivery expected mid-November, handed out at the following "
          "Saturday ride."),
         now - timedelta(days=6)),
    ]
    post_slugs = []
    for title, text, when in posts:
        post = call("POST", f"{base}/posts", {"name": title, "media": media(text), "dateTime": iso(when),
                                              "status": "PUBLISHED", "visibility": "TEAM"}, org)
        post_slugs.append(post["slug"])

    ads = [
        ("emma", "SALE", 1450, None, "Vélo route carbone, taille M" if fr else "Carbon road bike, size M",
         "Groupe Shimano 105 12 vitesses, freins à disque, 4 000 km. Révisé cet été." if fr else
         "Shimano 105 12-speed, disc brakes, 4,000 km. Serviced this summer.", (45.9050, 6.1250)),
        ("marc", "RENTAL", 15, "DAY", "Porte-vélos d'attelage 3 vélos" if fr else "Tow-bar bike rack, 3 bikes",
         "Idéal pour le week-end dans le Beaufortain." if fr else "Just right for the Beaufortain weekend.",
         (45.8700, 6.1100)),
        ("nicolas", "WANTED", None, None, "Cherche roues gravel 700C" if fr else "Looking for 700C gravel wheels",
         "Axe traversant 12 mm, compatibles tubeless." if fr else "12 mm thru-axle, tubeless-ready.",
         (45.9200, 6.1500)),
    ]
    for key, ad_type, price, period, title, text, at in ads:
        body = {"name": title, "media": media(text), "status": "PUBLISHED", "adType": ad_type,
                "locationDescription": "Annecy", "locationGeometry": point(*at)}
        if price is not None:
            body["price"] = price
        if period:
            body["rentalPeriod"] = period
        call("POST", f"{base}/classifieds", body, acc[key]["token"])

    # Comments: someone answers the viewer, which lands in the viewer's inbox.
    ride0 = rides[0]["slug"]
    c1 = call("POST", f"{base}/rides/{ride0}/comments", {
        "content": "Je prends le groupe rapide, on regroupe au sommet ?" if fr
        else "I'll lead the fast group, regroup at the top?"}, acc["thomas"]["token"])
    mine = call("POST", f"{base}/rides/{ride0}/comments", {
        "content": "Première sortie avec le club, j'ai hâte !" if fr
        else "First ride with the club, can't wait!"}, viewer["token"])
    call("POST", f"{base}/rides/{ride0}/comments", {
        "content": "Bienvenue ! Tu verras, le groupe intermédiaire est très sympa." if fr
        else "Welcome! You'll see, the intermediate group is great fun.", "parentId": mine["id"]},
         acc["lea"]["token"])
    call("POST", f"{base}/rides/{ride0}/comments", {
        "content": "Oui, regroupement au col, 5 minutes max." if fr else "Yes, regroup at the col, 5 minutes max.",
        "parentId": c1["id"]}, acc["sophie"]["token"])
    call("POST", f"{base}/posts/{post_slugs[1]}/comments", {
        "content": "Trop bien, j'en prends deux !" if fr else "Great, I'll take two!"}, acc["claire"]["token"])

    prefix = "/equipes" if fr else "/teams"
    sorties = "sorties" if fr else "rides"
    parcours = "parcours" if fr else "routes"
    voyages = "voyages" if fr else "trips"
    # `wait`: seconds before the screen may be taken, for maps whose tiles keep
    # arriving after the frame first looks still. The iPad home is left out: its
    # wide layout adds "latest publications" from every public club on staging,
    # real people's content a store page must not show; the trip takes its place.
    return [
        {"id": "01-home", "path": "/", "devices": ["iphone"]},
        {"id": "01-trip", "path": f"{prefix}/{slug}/{voyages}/{trip['slug']}", "devices": ["ipad"], "wait": 12},
        {"id": "02-ride", "path": f"{prefix}/{slug}/{sorties}/{ride0}", "wait": 12},
        {"id": "03-route", "path": f"{prefix}/{slug}/{parcours}/{routes['forclaz']}", "wait": 12},
        {"id": "04-routes", "path": f"{prefix}/{slug}/{parcours}"},
        {"id": "05-calendar", "path": "/calendrier" if fr else "/calendar"},
        {"id": "06-team", "path": f"{prefix}/{slug}"},
    ]


# ---------------------------------------------------------------- main


def main() -> None:
    if DRY_RUN:
        print(json.dumps({"api": API, "members": MEMBERS, "viewers": VIEWERS,
                          "routes": [r["key"] for r in ROUTES]}, indent=2, ensure_ascii=False))
        return
    accounts = load_accounts()
    print("▸ accounts")
    people = {k: ensure_account(accounts, k, n) for k, n in MEMBERS.items()}
    viewers = {loc: ensure_account(accounts, key, n) for loc, (key, n) in VIEWERS.items()}
    sql("UPDATE users SET platform_role = 'PLATFORM_ADMIN' WHERE email = '%s';" % people["julien"]["email"])
    # The token was minted before the promotion: sign in again to carry the role.
    accounts["julien"].pop("token", None)
    people["julien"] = ensure_account(accounts, "julien", MEMBERS["julien"])

    plan = {"api": API, "locales": {}}
    for locale, viewer in viewers.items():
        print(f"▸ club {locale}")
        plan["locales"][locale] = {"screens": seed_club(locale, people, viewer)}

    keys = list(MEMBERS) + [key for key, _ in VIEWERS.values()]
    stored = {k: {"email": accounts[k]["email"], "password": accounts[k]["password"], "id": accounts[k]["id"]}
              for k in keys}
    for locale, (key, _) in VIEWERS.items():
        stored[locale] = {"email": accounts[key]["email"], "password": accounts[key]["password"]}
    ACCOUNTS.write_text(json.dumps(stored, indent=2, ensure_ascii=False) + "\n")
    PLAN.write_text(json.dumps(plan, indent=2, ensure_ascii=False) + "\n")
    print(f"✔ {PLAN.name} and {ACCOUNTS.name} written")


if __name__ == "__main__":
    main()
