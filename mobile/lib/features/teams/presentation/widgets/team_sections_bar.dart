import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_tokens.dart';
import 'team_sections.dart';

/// The row of team sections — Feed, Calendar, Routes, Ads, Members, About.
///
/// **This is content, not navigation.** It used to be a second `NavigationBar`
/// stacked under the app one, which replaced the five global tabs as soon as
/// one entered a team. It is now a [PdlChipRow] sitting under the team header,
/// so a team is a page of the Teams tab like any other.
///
/// Two forms, one look. [TeamSectionsToolbar] is the sliver form — a
/// [PdlPinnedToolbar], so the row stays reachable however far the section
/// scrolls. [TeamSectionsBar] is the box form, for the sections whose body is
/// a page of its own rather than a list of slivers (calendar, route library,
/// classifieds): the bar is then fixed above that body, which reads the same
/// since a pinned toolbar placed first in a scroll view never moves either.
class TeamSectionsBar extends StatelessWidget {
  const TeamSectionsBar({
    super.key,
    required this.sections,
    required this.current,
  });

  final List<TeamSection> sections;
  final TeamSectionKind current;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;

    return PdlBlurSurface(
      color: c.overlaySolid,
      border: Border(bottom: BorderSide(color: c.borderSubtle)),
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: PdlMetrics.toolbarPadding,
        ),
        child: TeamSectionChips(sections: sections, current: current),
      ),
    );
  }
}

/// The sliver form of [TeamSectionsBar]: pinned under the team header.
class TeamSectionsToolbar extends StatelessWidget {
  const TeamSectionsToolbar({
    super.key,
    required this.sections,
    required this.current,
  });

  final List<TeamSection> sections;
  final TeamSectionKind current;

  @override
  Widget build(BuildContext context) => PdlPinnedToolbar(
    child: TeamSectionChips(sections: sections, current: current),
  );
}

/// The chips themselves, shared by both forms.
class TeamSectionChips extends StatelessWidget {
  const TeamSectionChips({
    super.key,
    required this.sections,
    required this.current,
  });

  final List<TeamSection> sections;
  final TeamSectionKind current;

  @override
  Widget build(BuildContext context) {
    final int selected = indexOfSection(current, sections);

    return PdlChipRow(
      children: <Widget>[
        for (int i = 0; i < sections.length; i++)
          PdlChip(
            label: sections[i].label.tr(),
            icon: sections[i].icon,
            selected: i == selected,
            // Sections replace one another inside the Teams branch rather than
            // stacking: going back from a section leads out of the team, not
            // through every section visited on the way.
            onTap: i == selected
                ? null
                : () => context.go(sections[i].currentPath),
          ),
      ],
    );
  }
}
