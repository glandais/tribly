package fr.pedalons.dto.tags.response;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * The tags of each content of a page, label order. Built by {@code
 * fr.pedalons.service.tag.TagLookup}, always in bulk — one query whatever the page size.
 *
 * <p>Tags are as public as the content carrying them: no per-caller filtering, a content without
 * tags maps to an empty list.
 *
 * @param tagsByContentId content (or ride template) id → its tags; absent means none
 */
public record ContentTags(Map<Long, List<TagDto>> tagsByContentId) {

  public static final ContentTags NONE = new ContentTags(Map.of());

  /** This content's tags, never null. */
  public List<TagDto> forContent(@Nullable Long contentId) {
    return contentId == null ? List.of() : tagsByContentId.getOrDefault(contentId, List.of());
  }
}
