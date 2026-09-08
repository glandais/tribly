# Le plugin maplibre n'a presque pas de code Kotlin : il pilote le SDK MapLibre Android
# *depuis Dart*, via package:jni. Les interfaces Java qu'il implémente le sont par un
# `java.lang.reflect.Proxy` construit au runtime (`PortProxyBuilder`), et le descripteur de
# chaque méthode y est comparé à une chaîne littérale émise par le générateur Dart. Aucune
# classe Java n'implémente ces interfaces : R8 se croit donc libre de les réduire, et la
# résolution échoue alors **sans bruit** — le proxy renvoie null, `MapLibreMapFactory.create`
# renvoie null à son tour, et la création de la platform view meurt sur un
# NullPointerException dans `PlatformViewsController.createPlatformView`, sans une seule
# frame du plugin dans la pile. En release, aucune carte ne s'affiche (c'était le cas du
# build 50) ; en debug, sans R8, tout fonctionne.
#
# C'est le bug amont josxha/flutter-maplibre#562, introduit par les règles de la 0.3.6
# elle-même. La règle sur `io.flutter.plugin.platform.**` est celle de la PR amont #564,
# toujours ouverte : **à supprimer d'ici quand elle sera publiée**, puisqu'elle appartient au
# `consumer-rules.pro` de `maplibre_android`. La bissection sur un Pixel 6a a montré que
# `PlatformView` seule suffit ; on garde le paquet entier pour ne pas diverger de l'amont.
#
# Les deux autres interfaces sont implémentées de la même façon par le plugin et portent la
# demande de permission de position — gardées par précaution, sans reproduction à l'appui.
# `org.maplibre.**` et `com.github.dart_lang.jni.**` sont déjà couvertes par les consumer
# rules de leurs paquets respectifs, ce qui n'a pas suffi.
-keep class io.flutter.plugin.platform.** { *; }
-keep class io.flutter.plugin.common.PluginRegistry { *; }
-keep class io.flutter.plugin.common.PluginRegistry$* { *; }
-keep class io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding { *; }
-keep class io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding$* { *; }
