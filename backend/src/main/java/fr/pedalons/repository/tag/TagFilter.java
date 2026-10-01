package fr.pedalons.repository.tag;

import fr.pedalons.repository.query.PedalonsQuery;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The {@code ?tags=} clause of a team's dedicated list (plan D6, D18): a content matches when it
 * carries <b>at least one</b> of the tags.
 *
 * <p>An {@code EXISTS} rather than a join, so a content carrying two of the requested tags still
 * comes out once — and the page size and the {@code countMatching} total stay right. Correlated on
 * the alias the listing query binds its content to ({@code te} in {@code TeamEntityRepository}).
 */
public final class TagFilter {

  /** The parameter name the clause binds; unique enough not to collide with a listing's own. */
  static final String PARAM = "filterTagIds";

  private TagFilter() {}

  /**
   * ANDs « tagged with any of {@code tagIds} » onto {@code query}. Adds nothing when {@code tagIds}
   * is empty: unknown ids are dropped beforehand by {@code TagService.resolveFilter}, and a filter
   * left with no known id is no filter.
   *
   * @param alias the content's alias in {@code query}, e.g. {@code te}
   */
  public static PedalonsQuery andTaggedWithAny(
      PedalonsQuery query, String alias, Collection<Long> tagIds) {
    if (tagIds.isEmpty()) {
      return query;
    }
    return query.and(
        "exists (select 1 from TeamEntityTag tft where tft.teamEntity.id = "
            + alias
            + ".id and tft.tag.id in (:"
            + PARAM
            + "))",
        Map.of(PARAM, Set.copyOf(tagIds)));
  }
}
