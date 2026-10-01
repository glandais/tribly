package fr.pedalons.service.tag;

import fr.pedalons.domain.tag.Tag;
import fr.pedalons.dto.tags.response.ContentTags;
import fr.pedalons.dto.tags.response.TagDto;
import fr.pedalons.repository.tag.RideTemplateTagRepository;
import fr.pedalons.repository.tag.TeamEntityTagRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the tags of a page of contents, in bulk (docs/LEDGER_*.md API-59).
 *
 * <p><b>It costs a page, not a row</b>: one query whatever the page size, the tags loaded with
 * their links and already sorted by label. Single entry point for the {@code tags} field of every
 * list row and every detail — a detail is a page of one.
 *
 * <p>No visibility rule of its own: a tag is as public as the vocabulary of the team, which is as
 * public as the team — and the caller only hands in contents it was allowed to read.
 *
 * <p><b>Tenancy:</b> answers only about the ids it is handed, which come from queries already
 * filtered by {@code domainId}; the query restates the domain on the tag's team anyway.
 */
@ApplicationScoped
public class TagLookup {

  @Inject TeamEntityTagRepository teamEntityTagRepository;

  @Inject RideTemplateTagRepository rideTemplateTagRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  /** Tags of a page of rides, posts, trips, routes or ads. One query, none for an empty page. */
  public ContentTags forContents(Collection<Long> contentIds) {
    if (contentIds.isEmpty()) {
      return ContentTags.NONE;
    }
    return group(
        teamEntityTagRepository.findTagsByEntityIds(pedalonsContext.getDomainId(), contentIds));
  }

  /** Tags of a single content — the detail path. Same query. */
  public List<TagDto> forContent(Long contentId) {
    return forContents(List.of(contentId)).forContent(contentId);
  }

  /** Tags of a page of ride templates. One query, none for an empty page. */
  public ContentTags forTemplates(Collection<Long> templateIds) {
    if (templateIds.isEmpty()) {
      return ContentTags.NONE;
    }
    return group(
        rideTemplateTagRepository.findTagsByTemplateIds(
            pedalonsContext.getDomainId(), templateIds));
  }

  public List<TagDto> forTemplate(Long templateId) {
    return forTemplates(List.of(templateId)).forContent(templateId);
  }

  /** (owner id, tag) rows, label order kept within each owner. */
  private static ContentTags group(List<Object[]> rows) {
    if (rows.isEmpty()) {
      return ContentTags.NONE;
    }
    Map<Long, List<TagDto>> byOwner = new HashMap<>();
    for (Object[] row : rows) {
      byOwner
          .computeIfAbsent((Long) row[0], id -> new ArrayList<>())
          .add(TagDto.from((Tag) row[1]));
    }
    byOwner.replaceAll((id, tags) -> List.copyOf(tags));
    return new ContentTags(Map.copyOf(byOwner));
  }
}
