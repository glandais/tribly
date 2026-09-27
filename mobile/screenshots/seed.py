#!/usr/bin/env python3
"""Seeds the fictional demo clubs: on staging for the store screenshots, on prod for
the store reviewers' test account.

    python3 screenshots/seed.py                  # staging: create what is missing, rebuild both clubs
    python3 screenshots/seed.py --dry-run        # print the plan, touch nothing
    MARKETPLACE_TESTER_PASSWORD=… python3 screenshots/seed.py --target prod

`--target prod` builds the same two clubs on www.pedalons.fr for the account given
to the Apple, Google and Garmin reviewers (marketplace-tester@pedalons.fr, which
must exist: its password comes from the environment, and it is neither renamed
nor registered). It is the viewer of both clubs, and the calendar is dense: one
or two rides a week from a few weeks back to the end of 2027, so the account
never runs out of upcoming rides between two releases.

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

On prod the organizer's PLATFORM_ADMIN role is taken back at the end of the run,
failed or not; the demo accounts get their e-mail notifications switched off, and
the test account joins the clubs only once the rides' announcements are fanned out,
so it doesn't receive a hundred "new ride" mails in one go.

Writes screenshots/accounts.local.json (staging) or accounts.prod.local.json
(git-ignored: passwords), and on staging screenshots/plan.json (the screens to
capture, with this run's slugs).
"""

from __future__ import annotations

import base64
import hashlib
import json
import os
import random
import secrets
import subprocess
import sys
import time as clock
import urllib.error
import urllib.request
from datetime import date, datetime, time, timedelta
from pathlib import Path
from zoneinfo import ZoneInfo

HERE = Path(__file__).resolve().parent
TARGETS = {
    "staging": {"api": "https://staging.pedalons.fr", "db": "pedalons-staging-postgres",
                "accounts": HERE / "accounts.local.json"},
    "prod": {"api": "https://www.pedalons.fr", "db": "pedalons-prod-postgres",
             "accounts": HERE / "accounts.prod.local.json"},
}
TARGET = sys.argv[sys.argv.index("--target") + 1] if "--target" in sys.argv else "staging"
if TARGET not in TARGETS:
    sys.exit(f"--target must be one of {', '.join(TARGETS)}")
PROD = TARGET == "prod"

API = TARGETS[TARGET]["api"]
SSH = "pedalons@pedalons.fr"
DB_CONTAINER = TARGETS[TARGET]["db"]
INBOX = "gabriel.landais+pdl-demo-{key}@gmail.com"
TZ = ZoneInfo("Europe/Paris")

ACCOUNTS = TARGETS[TARGET]["accounts"]
PLAN = HERE / "plan.json"

DRY_RUN = "--dry-run" in sys.argv

# The account the store reviewers sign in with, on prod. It exists already.
TESTER = "marketplace-tester@pedalons.fr"
TESTER_PASSWORD_ENV = "MARKETPLACE_TESTER_PASSWORD"
# The prod calendar: from CALENDAR_BACK_WEEKS ago until CALENDAR_END.
CALENDAR_BACK_WEEKS = 6
CALENDAR_END = date(2027, 12, 31)

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


def known_token(email: str, token_type: str) -> str:
    """Swaps the hash of `email`'s pending `token_type` token for the hash of a token
    this script knows, and returns that token: no inbox is read."""
    token = secrets.token_urlsafe(32)
    token_hash = base64.b64encode(hashlib.sha256(token.encode()).digest()).decode()
    updated = sql(
        "UPDATE auth_tokens SET token_hash = '%s' WHERE email = '%s' "
        "AND token_type = '%s' AND used_at IS NULL RETURNING id;" % (token_hash, email, token_type))
    if not updated:
        raise RuntimeError(f"no pending {token_type} token for {email}")
    return token


def ensure_account(accounts: dict, key: str, name: str) -> dict:
    """Signs `key` in, registering and verifying it first if it doesn't exist."""
    acc = accounts.get(key) or {"email": INBOX.format(key=key), "password": secrets.token_urlsafe(18)}
    accounts[key] = acc
    try:
        auth = call("POST", "/api/auth/login", {"email": acc["email"], "password": acc["password"]})
    except ApiError as e:
        if e.status not in (400, 401):
            raise
        try:
            call("POST", "/api/auth/register", {"email": acc["email"], "displayName": name,
                                                 "password": acc["password"], "acceptTerms": True})
            print(f"  registered {name} <{acc['email']}>")
            auth = call("POST", "/api/auth/verify-email",
                        {"token": known_token(acc["email"], "EMAIL_VERIFICATION")})
        except ApiError as e:
            if "EMAIL_ALREADY_EXISTS" not in e.body:
                raise
            # The account exists with a password this file doesn't know (the
            # database was rebuilt, or seeded from another checkout): reset it.
            print(f"  resetting the password of {name} <{acc['email']}>")
            call("POST", "/api/auth/forgot-password", {"email": acc["email"]})
            auth = call("POST", "/api/auth/reset-password",
                        {"token": known_token(acc["email"], "PASSWORD_RESET"),
                         "newPassword": acc["password"]})
    acc["id"] = auth["user"]["id"]
    acc["token"] = auth["accessToken"]
    if auth["user"].get("displayName") != name:
        call("PUT", "/api/users/me", {"displayName": name}, acc["token"])
    return acc


def tester_account() -> dict:
    """Signs the reviewers' account in. Never registered nor renamed by this script."""
    password = os.environ.get(TESTER_PASSWORD_ENV)
    if not password:
        sys.exit(f"--target prod needs {TESTER_PASSWORD_ENV}")
    auth = call("POST", "/api/auth/login", {"email": TESTER, "password": password})
    return {"email": TESTER, "id": auth["user"]["id"], "token": auth["accessToken"]}


def mute_email(acc: dict) -> None:
    """Switches every e-mail notification of a demo account off; the inbox keeps them."""
    prefs = call("GET", "/api/notifications/preferences", token=acc["token"])
    cells = [{"type": c["type"], "channel": c["channel"], "enabled": False}
             for c in prefs["preferences"] if c["channel"] != "IN_APP" and c["enabled"]]
    if cells:
        call("PUT", "/api/notifications/preferences", {"preferences": cells}, acc["token"])


def wait_for_fan_out(slug: str) -> None:
    """Waits until the club's notification events have all been fanned out."""
    team_id = sql("SELECT id FROM teams WHERE NOT deleted AND slug = '%s';" % slug)
    for _ in range(120):
        pending = int(sql("SELECT count(*) FROM notification_events WHERE team_id = %s "
                          "AND status IN ('PENDING', 'PROCESSING');" % team_id))
        if not pending:
            return
        print(f"  waiting for {pending} notification events to fan out")
        clock.sleep(10)
    raise RuntimeError("notification events still pending after 20 minutes")


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


def calendar(fr: bool) -> list:
    """The prod calendar, as `rides_spec` entries: every Saturday (mountains from April
    to October, the lake and the Bauges later in the morning in winter), Wednesday
    evenings round the lake from April to September, gravel on the first Sunday of
    the month from May to October."""
    rng = random.Random(f"calendar-{fr}")
    leaders = ["thomas", "marc", "sophie", "lea", None, None]
    names = {r["key"]: r["fr" if fr else "en"] for r in ROUTES}
    today = datetime.now(TZ).date()
    d = today - timedelta(weeks=CALENDAR_BACK_WEEKS)
    out = []
    while d <= CALENDAR_END:
        summer = 4 <= d.month <= 10
        week = d.isocalendar()[1]
        if d.weekday() == 5:
            route = (["forclaz", "semnoz", "bauges", "lac"] if summer else ["lac", "bauges"])[week % (4 if summer else 2)]
            out.append((route, "paquier", datetime.combine(d, time(8, 30) if summer else time(9, 30), TZ),
                        tuple(rng.choice(leaders) for _ in range(3)),
                        (f"Sortie du samedi : {names[route]}" if fr else f"Saturday ride: {names[route]}")
                        if summer else (f"Sortie d'hiver : {names[route]}" if fr else f"Winter ride: {names[route]}"),
                        ("Rendez-vous au Pâquier, départ à l'heure. Regroupement au sommet." if fr else
                         "Meet at Le Pâquier, we leave on time. Regroup at the top.") if summer else
                        ("Départ plus tard pour éviter le gel. Gants longs et couvre-chaussures." if fr else
                         "Later start to avoid the frost. Full gloves and overshoes.")))
        elif d.weekday() == 2 and 4 <= d.month <= 9:
            out.append(("lac", "paquier", datetime.combine(d, time(18, 15), TZ), (rng.choice(leaders),),
                        "Tour du lac en semaine" if fr else "Midweek lake loop",
                        "Sortie courte après le travail, éclairage obligatoire au retour." if fr else
                        "Short after-work ride, lights required on the way back."))
        elif d.weekday() == 6 and d.day <= 7 and 5 <= d.month <= 10:
            out.append(("glieres", "thorens", datetime.combine(d, time(9, 0), TZ),
                        (rng.choice(leaders), None),
                        "Gravel aux Glières" if fr else "Glières gravel ride",
                        "Pistes roulantes, quelques passages caillouteux au plateau." if fr else
                        "Fast tracks, a few rocky sections on the plateau."))
        d += timedelta(days=1)
    return out


def random_signups(when: datetime, group_count: int, seed: str) -> dict:
    """Members registered on a calendar ride: many on past and imminent rides, a few
    early birds on rides months away."""
    rng = random.Random(seed)
    days = (when - datetime.now(TZ)).days
    count = rng.randint(4, 8) if days < 21 else rng.randint(0, 3)
    return {key: rng.randrange(group_count) for key in rng.sample(list(MEMBERS), count)}


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
    if not PROD:
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

    def groups(route: str, leaders: tuple, when: datetime) -> list:
        """One group per entry of `leaders` (None: no leader), fastest first, the
        first leaving at the ride's time and the others a quarter of an hour later."""
        names = (["Groupe rapide", "Groupe intermédiaire", "Groupe découverte"] if fr
                 else ["Fast group", "Intermediate group", "Discovery group"])
        speeds = [31, 27, 23]
        # Fewer groups keep the middle ones: a lone group is the intermediate one.
        picked = {1: [1], 2: [0, 1], 3: [0, 1, 2]}[len(leaders)]
        out = []
        for n, (i, leader) in enumerate(zip(picked, leaders)):
            start = when if n == 0 else when + timedelta(minutes=15)
            g = {"name": names[i], "time": f"{start:%H:%M}:00", "averageSpeed": speeds[i],
                 "maxParticipants": 12, "routeSlug": route}
            if leader:
                g["leaderId"] = acc[leader]["id"]
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
    # Registrations of the four rides above.
    signups = [{"thomas": 0, "nicolas": 0, "emma": 0, "lea": 1, "marc": 1, "claire": 2, "sophie": 2},
               {"lea": 0, "claire": 0, "nicolas": 1},
               {"marc": 0, "thomas": 0, "emma": 1},
               {"nicolas": 0, "julien": 0, "lea": 1}]
    if PROD:
        taken = {when.date() for _, _, when, *_ in rides_spec}
        for spec in calendar(fr):
            if spec[2].date() not in taken:
                rides_spec.append(spec)
                signups.append(random_signups(spec[2], len(spec[3]), f"{locale}-{spec[2]:%Y%m%d}"))
        rides_spec, signups = map(list, zip(*sorted(zip(rides_spec, signups), key=lambda x: x[0][2])))
    ride0 = None
    rides = []
    for (route, place, when, leaders, title, text), joins in zip(rides_spec, signups):
        ride = call("POST", f"{base}/rides", {
            "name": title, "media": media(text), "dateTime": iso(when), "status": "PUBLISHED",
            "visibility": "TEAM", "routeSlug": routes[route], "startPlaceId": places[place],
            "endPlaceId": places[place], "groups": groups(routes[route], leaders, when)}, org)
        gids = [g["id"] for g in ride["groups"]]
        for key, g in joins.items():
            call("POST", f"{base}/rides/{ride['slug']}/groups/{gids[g]}/join", token=acc[key]["token"])
        rides.append((ride["slug"], when, gids))
        if joins is signups[0]:
            ride0 = ride["slug"]
        print(f"  ride {ride['slug']}  {when:%a %d %b %Y %H:%M}  {len(joins)} riders")

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

    if PROD:
        # The viewer joins last, once every announcement above has been fanned out
        # to the members of the moment: the reviewers' inbox gets comments, not
        # a hundred "new ride" mails.
        wait_for_fan_out(slug)
        call("POST", f"{base}/members", {"userId": viewer["id"], "role": "MEMBER"}, org)

    # The viewer rides the first ride, in the intermediate group. On prod it also
    # has a history, and the next rides of its calendar.
    now = datetime.now(TZ)
    past = [r[0] for r in rides if r[1] < now]
    upcoming = [r[0] for r in rides if r[1] > now]
    viewer_rides = {ride0} | (set(past[::3] + upcoming[:3]) if PROD else set())
    for ride_slug, when, gids in rides:
        if ride_slug in viewer_rides:
            call("POST", f"{base}/rides/{ride_slug}/groups/{gids[len(gids) // 2]}/join",
                 token=viewer["token"])

    # Comments: someone answers the viewer, which lands in the viewer's inbox.
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
        print(json.dumps({"target": TARGET, "api": API, "members": MEMBERS,
                          "viewers": {"fr-FR": TESTER, "en-US": TESTER} if PROD else VIEWERS,
                          "routes": [r["key"] for r in ROUTES]}, indent=2, ensure_ascii=False))
        if PROD:
            for fr in (True, False):
                spec = calendar(fr)
                print(f"{'fr' if fr else 'en'}: {len(spec) + 4} rides, "
                      f"{spec[0][2]:%d/%m/%Y} → {spec[-1][2]:%d/%m/%Y}")
        return
    tester = tester_account() if PROD else None
    accounts = load_accounts()
    print(f"▸ accounts on {API}")
    people = {k: ensure_account(accounts, k, n) for k, n in MEMBERS.items()}
    if PROD:
        viewers = {"fr-FR": tester, "en-US": tester}
        for acc in people.values():
            mute_email(acc)
    else:
        viewers = {loc: ensure_account(accounts, key, n) for loc, (key, n) in VIEWERS.items()}
    julien = people["julien"]["email"]
    sql("UPDATE users SET platform_role = 'PLATFORM_ADMIN' WHERE email = '%s';" % julien)
    try:
        # The token was minted before the promotion: sign in again to carry the role.
        accounts["julien"].pop("token", None)
        people["julien"] = ensure_account(accounts, "julien", MEMBERS["julien"])

        plan = {"api": API, "locales": {}}
        for locale, viewer in viewers.items():
            print(f"▸ club {locale}")
            plan["locales"][locale] = {"screens": seed_club(locale, people, viewer)}
    finally:
        if PROD:
            # No fictional account keeps a platform role on prod.
            sql("UPDATE users SET platform_role = NULL WHERE email = '%s';" % julien)
            print("  organizer's platform role taken back")

    keys = list(MEMBERS) + ([] if PROD else [key for key, _ in VIEWERS.values()])
    stored = {k: {"email": accounts[k]["email"], "password": accounts[k]["password"], "id": accounts[k]["id"]}
              for k in keys}
    if not PROD:
        for locale, (key, _) in VIEWERS.items():
            stored[locale] = {"email": accounts[key]["email"], "password": accounts[key]["password"]}
    ACCOUNTS.write_text(json.dumps(stored, indent=2, ensure_ascii=False) + "\n")
    if PROD:
        print(f"✔ {ACCOUNTS.name} written")
        return
    PLAN.write_text(json.dumps(plan, indent=2, ensure_ascii=False) + "\n")
    print(f"✔ {PLAN.name} and {ACCOUNTS.name} written")

if __name__ == "__main__":
    main()
