import 'package:flutter/material.dart';

/// Couche d'indirection du jeu d'icônes (§1.0.3-10 du plan v2).
///
/// Les maquettes et `brand.md` §6 emploient le jeu **Tabler**, servi en SVG ;
/// le brief §4.1.2 et tout le code existant emploient **Material outline**.
/// L'arbitrage retenu pour la v2 est Material outline : l'écart ne porte que
/// sur la graisse du trait des icônes de 11 px de badge, et ajouter
/// `flutter_svg` plus un jeu d'assets pour cela est disproportionné en regard
/// des ~65 composants à écrire.
///
/// Ce fichier est le **seul** endroit de la bibliothèque `core/pdl` autorisé à
/// nommer `Icons.*`. La règle de revue associée :
///
/// ```
/// grep -rn "Icons\." mobile/lib/core/pdl   # ne doit rien renvoyer
/// ```
///
/// Basculer vers Tabler devient alors mécanique : seul le corps de ce fichier
/// change.
abstract final class PdlIcons {
  // ── Navigation racine (les 5 onglets) ──────────────────────────────────
  static const IconData home = Icons.home_outlined;
  static const IconData homeActive = Icons.home;
  static const IconData teams = Icons.group_outlined;
  static const IconData teamsActive = Icons.group;
  static const IconData calendar = Icons.calendar_today_outlined;
  static const IconData calendarActive = Icons.calendar_today;
  static const IconData routes = Icons.route_outlined;
  static const IconData routesActive = Icons.route;
  static const IconData profile = Icons.person_outlined;
  static const IconData profileActive = Icons.person;

  // ── Barres et gestes ───────────────────────────────────────────────────
  static const IconData back = Icons.arrow_back;
  static const IconData close = Icons.close;
  static const IconData chevronRight = Icons.chevron_right;
  static const IconData chevronLeft = Icons.chevron_left;
  static const IconData chevronDown = Icons.expand_more;
  static const IconData chevronUp = Icons.expand_less;
  static const IconData more = Icons.more_horiz;
  static const IconData share = Icons.ios_share;
  static const IconData search = Icons.search;
  static const IconData filter = Icons.tune;
  static const IconData sort = Icons.swap_vert;
  static const IconData refresh = Icons.refresh;
  static const IconData check = Icons.check;
  static const IconData checkCircle = Icons.check_circle;
  static const IconData add = Icons.add;
  static const IconData remove = Icons.close;
  static const IconData edit = Icons.edit_outlined;
  static const IconData delete = Icons.delete_outline;
  static const IconData copy = Icons.copy_all_outlined;
  static const IconData openExternal = Icons.open_in_new;
  static const IconData settings = Icons.settings_outlined;
  static const IconData logout = Icons.logout;

  // ── Types de contenu ───────────────────────────────────────────────────
  static const IconData ride = Icons.directions_bike;
  static const IconData post = Icons.article_outlined;
  static const IconData trip = Icons.luggage_outlined;
  static const IconData stage = Icons.flag_outlined;
  static const IconData ad = Icons.sell_outlined;
  static const IconData team = Icons.group_outlined;
  static const IconData route = Icons.route_outlined;
  static const IconData page = Icons.description_outlined;
  static const IconData feed = Icons.dynamic_feed_outlined;
  static const IconData comment = Icons.mode_comment_outlined;
  static const IconData attachment = Icons.attach_file;
  static const IconData gpx = Icons.download_outlined;
  static const IconData image = Icons.image_outlined;
  static const IconData brokenImage = Icons.broken_image_outlined;

  // ── Vélo, parcours, relief ─────────────────────────────────────────────
  static const IconData distance = Icons.straighten;
  static const IconData elevationUp = Icons.trending_up;
  static const IconData elevationDown = Icons.trending_down;
  static const IconData terrain = Icons.terrain;
  static const IconData climb = Icons.landscape;
  static const IconData speed = Icons.speed;
  static const IconData surfaceRoad = Icons.add_road;
  static const IconData surfaceGravel = Icons.grain;
  static const IconData surfaceMtb = Icons.forest_outlined;
  static const IconData surfaceMixed = Icons.alt_route;
  static const IconData wind = Icons.air;

  // ── Carte ──────────────────────────────────────────────────────────────
  static const IconData map = Icons.map_outlined;
  static const IconData list = Icons.view_list_outlined;
  static const IconData layers = Icons.layers_outlined;
  static const IconData fullscreen = Icons.fullscreen;
  static const IconData fullscreenExit = Icons.fullscreen_exit;
  static const IconData locate = Icons.my_location;
  static const IconData nearMe = Icons.near_me_outlined;
  static const IconData place = Icons.location_on_outlined;
  static const IconData placeStart = Icons.trip_origin;
  static const IconData placeEnd = Icons.flag_outlined;

  // ── Temps, agenda, personnes ───────────────────────────────────────────
  static const IconData time = Icons.schedule;
  static const IconData date = Icons.event_outlined;
  static const IconData dateBusy = Icons.event_busy;
  static const IconData people = Icons.people_outline;
  static const IconData person = Icons.person_outline;
  static const IconData personOff = Icons.person_off_outlined;
  static const IconData leader = Icons.star_outline;
  static const IconData admin = Icons.shield_outlined;
  static const IconData seats = Icons.event_seat_outlined;

  // ── Statuts, états, retours ────────────────────────────────────────────
  static const IconData info = Icons.info_outline;
  static const IconData warning = Icons.warning_amber_outlined;
  static const IconData error = Icons.error_outline;
  static const IconData offline = Icons.wifi_off_outlined;
  static const IconData notFound = Icons.help_outline;
  static const IconData emptySearch = Icons.search_off;
  static const IconData draft = Icons.edit_note;
  static const IconData cancelled = Icons.block;
  static const IconData done = Icons.done_all;
  static const IconData visibilityPublic = Icons.public;
  static const IconData visibilityUnlisted = Icons.link;
  static const IconData visibilityTeam = Icons.lock_outline;

  // ── Compte, préférences ────────────────────────────────────────────────
  static const IconData email = Icons.email_outlined;
  static const IconData password = Icons.lock_reset;
  static const IconData passkey = Icons.fingerprint;
  static const IconData device = Icons.smartphone;
  static const IconData devices = Icons.devices;
  static const IconData language = Icons.language;
  static const IconData theme = Icons.brightness_6_outlined;
  static const IconData units = Icons.straighten;
  static const IconData privacy = Icons.policy_outlined;
  static const IconData price = Icons.euro_symbol;
  static const IconData camera = Icons.photo_camera_outlined;
}
