import 'api/teams_content_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-team.e2e.ts` › team pages, and `ssr-session.e2e.ts` › a TEAM page never
/// leaks to an outsider.
///
/// An outsider of a public team must not even see the title of its members-only pages: the API
/// lists in `TeamDetailDto.pages` only the pages the caller may open (`TeamPageReadRule`, the same
/// rule as reading a page by its slug), so `TeamAboutPage` never gets a row whose tap would end on
/// the 404 error screen.
void main() {
  testApp(
    'Members read a team’s TEAM and PUBLIC pages; an outsider of the public team only sees the PUBLIC one',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Pages owner');
      final member = await backend.newUser('Pages member');
      final outsider = await backend.newUser('Pages outsider');
      final team = await backend.newTeam(
        owner,
        'Equipe pages',
        visibility: 'PUBLIC',
      );
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final teamTitle = unique('Charte');
      final teamPage = await backend.newTeamPage(
        owner,
        teamSlug,
        teamTitle,
        markdown: 'Réservé aux membres',
      );
      final teamPageSlug = teamPage['slug'] as String;
      final publicTitle = unique('Parcours club');
      final publicPage = await backend.newTeamPage(
        owner,
        teamSlug,
        publicTitle,
        visibility: 'PUBLIC',
        markdown: 'Nos boucles du samedi',
      );
      final publicPageSlug = publicPage['slug'] as String;

      expect(await backend.teamPageStatus(member, teamSlug, teamPageSlug), 200);
      expect(
        await backend.teamPageStatus(outsider, teamSlug, teamPageSlug),
        404,
      );

      // A member: both pages listed, both open.
      await openAppSignedIn($, member);
      await openLink($, Paths.teamAbout(teamSlug));
      await modules.teams.waitUntilAboutPageRowIsShown(publicPageSlug);
      await modules.teams.waitUntilAboutPageRowIsShown(teamPageSlug);
      await modules.teams.openAboutPage(teamPageSlug);
      expect(modules.teams.customPageTitle, teamTitle);
      expect(modules.teams.customPageBodyShows('Réservé aux membres'), isTrue);

      await openLink($, Paths.teamAbout(teamSlug));
      await modules.teams.openAboutPage(publicPageSlug);
      expect(modules.teams.customPageTitle, publicTitle);
      expect(
        modules.teams.customPageBodyShows('Nos boucles du samedi'),
        isTrue,
      );

      // An outsider: the PUBLIC page only.
      await openAppSignedIn($, outsider);
      await openLink($, Paths.teamAbout(teamSlug));
      await modules.teams.waitUntilAboutPageRowIsShown(publicPageSlug);
      // The TEAM page's title is not listed.
      expect(modules.teams.showsAboutPageRow(teamPageSlug), isFalse);
      await modules.teams.openAboutPage(publicPageSlug);
      expect(modules.teams.customPageTitle, publicTitle);
    },
  );
}
