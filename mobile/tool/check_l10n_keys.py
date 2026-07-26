#!/usr/bin/env python3
"""fr.json et en.json doivent porter le même jeu de clés.

Une clé présente d'un seul côté ne casse rien à la compilation : elle rend son
propre nom à l'écran, dans la langue où on ne l'a pas relue. Ce script est donc
le seul garde-fou, et il tient en trois lignes utiles.

    python3 tool/check_l10n_keys.py
"""
import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent / "assets" / "l10n"
REFERENCE = "fr"


def flatten(node, prefix=""):
    if isinstance(node, dict):
        for key, value in node.items():
            yield from flatten(value, f"{prefix}.{key}" if prefix else key)
    else:
        yield prefix


def main() -> int:
    trees = {
        path.stem: set(flatten(json.loads(path.read_text(encoding="utf-8"))))
        for path in sorted(ROOT.glob("*.json"))
    }
    failed = False
    for locale, keys in trees.items():
        if locale == REFERENCE:
            continue
        for missing in sorted(trees[REFERENCE] - keys):
            print(f"manquante dans {locale}.json : {missing}")
            failed = True
        for extra in sorted(keys - trees[REFERENCE]):
            print(f"absente de {REFERENCE}.json : {extra}")
            failed = True
    if failed:
        return 1
    print(f"{len(trees[REFERENCE])} clés, jeux identiques")
    return 0


if __name__ == "__main__":
    sys.exit(main())
