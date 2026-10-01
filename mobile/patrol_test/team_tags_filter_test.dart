import 'api/routes_seed.dart';
import 'api/tags_seed.dart';
import 'common.dart';

/// Ledger `MOB-39` — a team's tags on its route list: the card shows the route's tag, and the
/// « Tags » chip narrows the list to the routes carrying the chosen one (`?tags=`, server side).
///
/// Web counterpart: `tags.e2e.ts`, on ads and through the list's URL; the app has no URL to type,
/// so it goes through the chip and its sheet, on routes.
void main() {
  testApp(
    'A team route card shows its tag, and filtering by that tag keeps that route alone',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Tags owner');
      final member = await backend.newUser('Tags member');
      final team = await backend.newTeam(owner, 'Parcours tags');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);

      final labelA = unique('Col');
      final labelB = unique('Plat');
      final tagA = await backend.newTag(
        owner,
        teamSlug,
        type: 'ROUTE',
        label: labelA,
        color: 'RED',
      );
      final tagB = await backend.newTag(
        owner,
        teamSlug,
        type: 'ROUTE',
        label: labelB,
        color: 'GREEN',
      );
      final tagAId = tagA['id'] as String;
      final first = await backend.newRoute(
        owner,
        teamSlug,
        unique('Parcours A'),
        tagIds: [tagAId],
      );
      final second = await backend.newRoute(
        owner,
        teamSlug,
        unique('Parcours B'),
        tagIds: [tagB['id'] as String],
      );
      final firstSlug = first['slug'] as String;
      final secondSlug = second['slug'] as String;

      await openAppSignedIn($, member);
      await openLink($, Paths.routes(teamSlug));
      await modules.routes.waitUntilListed(firstSlug);
      await modules.routes.waitUntilListed(secondSlug);
      expect(await modules.routes.cardTagsShow(firstSlug, labelA), isTrue);
      expect(await modules.routes.cardTagsShow(secondSlug, labelA), isFalse);

      await modules.routes.filterByTag(tagAId);
      await modules.routes.waitUntilNotListed(secondSlug);
      expect(modules.routes.isListed(firstSlug), isTrue);
    },
  );
}
