import 'package:flutter/foundation.dart';

/// Une publication voisine : de quoi rendre un bloc de navigation, rien de plus.
@immutable
class PostNeighbour {
  const PostNeighbour({required this.slug, required this.name});

  final String slug;
  final String name;
}

/// La place d'une publication **dans le fil d'où on l'a ouverte**.
///
/// Le contrat n'expose aucun voisinage : `PostDto` ne porte ni `previousSlug`
/// ni `nextSlug`, et il n'existe pas d'endpoint pour les demander. La seule
/// source honnête est donc la liste que l'appelant a déjà chargée, transmise
/// dans l'`extra` de la route — hors de l'URL, puisqu'elle n'appartient pas à
/// l'identité de la publication.
///
/// C'est la **liste entière et un index** qui voyagent, et non les deux seuls
/// voisins : sans elle, passer au suivant laisserait le suivant sans voisins à
/// lui, ou pire, avec ceux du précédent.
///
/// Conséquence assumée (§5.1) : **ouverte par un deeplink, la publication n'a
/// pas de voisins** et `PdlPrevNextNav` disparaît entièrement, sans laisser
/// l'espace vide d'un bloc désactivé qui ferait croire à un chargement.
@immutable
class PostNeighbours {
  const PostNeighbours({required this.posts, required this.index});

  /// Les publications du fil, dans son ordre, la courante comprise.
  final List<PostNeighbour> posts;

  /// Position de la publication courante dans [posts].
  final int index;

  PostNeighbour? get previous => index > 0 ? posts[index - 1] : null;

  PostNeighbour? get next => index < posts.length - 1 ? posts[index + 1] : null;

  bool get isEmpty => previous == null && next == null;

  /// La même liste, recentrée sur [slug] — ce qu'on transmet en passant au
  /// voisin pour qu'il connaisse les siens.
  PostNeighbours? movedTo(String slug) {
    final int next = posts.indexWhere((PostNeighbour p) => p.slug == slug);
    if (next < 0) return null;
    return PostNeighbours(posts: posts, index: next);
  }
}
