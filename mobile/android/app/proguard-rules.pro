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
# `io.flutter.plugin.platform.PlatformView` est la règle dont dépend l'affichage des cartes
# — établie par bissection sur un Pixel 6a. Les autres interfaces que le plugin implémente
# de la même façon sont gardées par précaution : ce sont celles qui portent la demande de
# permission de position. `org.maplibre.**` et `com.github.dart_lang.jni.**` sont déjà
# couvertes par les consumer rules de leurs paquets respectifs — ce qui n'a pas suffi.
-keep class io.flutter.plugin.platform.PlatformView { *; }
-keep class io.flutter.plugin.common.PluginRegistry { *; }
-keep class io.flutter.plugin.common.PluginRegistry$* { *; }
-keep class io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding { *; }
-keep class io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding$* { *; }
