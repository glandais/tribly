package fr.pedalons.service.tag;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.tag.RideTemplateTag;
import fr.pedalons.domain.tag.Tag;
import fr.pedalons.domain.tag.TeamEntityTag;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.tags.request.TagCreateRequest;
import fr.pedalons.dto.tags.request.TagUpdateRequest;
import fr.pedalons.dto.tags.response.TagDeletedDto;
import fr.pedalons.dto.tags.response.TagWithUsageDto;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TagColor;
import fr.pedalons.enums.TagTarget;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.tag.RideTemplateTagRepository;
import fr.pedalons.repository.tag.TagRepository;
import fr.pedalons.repository.tag.TeamEntityTagRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.Nullable;

/**
 * A team's tag vocabulary, and the tags its contents carry (docs/LEDGER_*.md API-59,
 * docs/plans/archive/2026-10-01-tags.md).
 *
 * <p>Two halves:
 *
 * <ul>
 *   <li><b>The vocabulary</b> — list, create, rename/recolour, delete — behind {@code TagResource}.
 *       Read by whoever sees the team; written by its admins only, and only from the admin screen
 *       (plan D4).
 *   <li><b>Tagging a content</b> — {@link #replaceTags}, {@link #replaceTemplateTags}, {@link
 *       #resolveFilter}. No access check of their own: they run inside the
 *       content's own create/update, whose edit rights are exactly the tagging rights (plan D5).
 * </ul>
 */
@ApplicationScoped
public class TagService {

  /** At most this many tags on one content (plan D16). */
  public static final int MAX_TAGS_PER_CONTENT = 10;

  /** At most this many tags per team and kind (plan D16). */
  public static final int MAX_TAGS_PER_TEAM_AND_TYPE = 100;

  @Inject TagRepository tagRepository;

  @Inject TeamEntityTagRepository teamEntityTagRepository;

  @Inject RideTemplateTagRepository rideTemplateTagRepository;

  @Inject TeamService teamService;

  @Inject PedalonsQueryContext pedalonsContext;

  // ─── Vocabulary ───────────────────────────────────────────────────────────

  /**
   * The team's tags, of one kind or all, by label, each with its usage count. Three queries
   * whatever the number of tags.
   */
  @CheckAccess(entityType = EntityType.TAG, action = ActionType.LIST)
  public List<TagWithUsageDto> listTags(String teamSlug, @Nullable TagTarget type) {
    Team team = teamService.getTeam(teamSlug);
    List<Tag> tags = tagRepository.findByTeam(team.getId(), type);
    Map<Long, Long> usage = tagRepository.countUsage(tags.stream().map(Tag::getId).toList());
    return tags.stream()
        .map(tag -> TagWithUsageDto.from(tag, usage.getOrDefault(tag.getId(), 0L)))
        .toList();
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TAG, action = ActionType.CREATE)
  public TagWithUsageDto createTag(String teamSlug, TagCreateRequest request) {
    Team team = teamService.getTeam(teamSlug);
    String label = normalizeLabel(request.label());
    if (tagRepository.countByTeamAndType(team.getId(), request.type())
        >= MAX_TAGS_PER_TEAM_AND_TYPE) {
      throw new BusinessException(ErrorCode.TAG_LIMIT_REACHED);
    }
    if (tagRepository.existsLabel(team.getId(), request.type(), label, null)) {
      throw new ConflictException(ErrorCode.TAG_LABEL_TAKEN);
    }
    Tag tag = new Tag(pedalonsContext.getUser(), team, request.type(), label, request.color());
    tagRepository.persist(tag);
    flushLabel();
    return TagWithUsageDto.from(tag, 0);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TAG, action = ActionType.UPDATE)
  public TagWithUsageDto updateTag(String teamSlug, String tagId, TagUpdateRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Tag tag = getTag(team, tagId);
    String requestedLabel = request.label();
    if (requestedLabel != null) {
      String label = normalizeLabel(requestedLabel);
      if (tagRepository.existsLabel(team.getId(), tag.getType(), label, tag.getId())) {
        throw new ConflictException(ErrorCode.TAG_LABEL_TAKEN);
      }
      // A change of case alone (« gravel » → « Gravel ») is a rename like any other.
      tag.setLabel(label);
    }
    if (request.color() != null) {
      tag.setColor(request.color());
    }
    flushLabel();
    long usage = tagRepository.countUsage(List.of(tag.getId())).getOrDefault(tag.getId(), 0L);
    return TagWithUsageDto.from(tag, usage);
  }

  /**
   * Detaches the tag from every content and template, then deletes it for good (plan D11). The
   * count returned is the one {@code usageCount} announced: links held by trashed contents go too,
   * uncounted — restoring such a content brings it back untagged.
   */
  @Transactional
  @CheckAccess(entityType = EntityType.TAG, action = ActionType.DELETE)
  public TagDeletedDto deleteTag(String teamSlug, String tagId) {
    Team team = teamService.getTeam(teamSlug);
    Tag tag = getTag(team, tagId);
    long detached = tagRepository.countUsage(List.of(tag.getId())).getOrDefault(tag.getId(), 0L);
    teamEntityTagRepository.deleteByTag(tag.getId());
    rideTemplateTagRepository.deleteByTag(tag.getId());
    tagRepository.delete(tag);
    return new TagDeletedDto(detached);
  }

  /**
   * Flushes a tag just created or renamed, so that two admins writing the same label at once get
   * the 409 {@code existsLabel} gives the slower one, not the exception mapper's 500 at commit:
   * {@code uk_tags_team_type_label} settles that race, and it is the only constraint a tag write
   * can break — the team and the author exist.
   */
  private void flushLabel() {
    try {
      tagRepository.flush();
    } catch (PersistenceException e) {
      if (isConstraintViolation(e)) {
        throw new ConflictException(ErrorCode.TAG_LABEL_TAKEN, e);
      }
      throw e;
    }
  }

  private static boolean isConstraintViolation(Throwable e) {
    for (Throwable t = e; t != null; t = t.getCause()) {
      if (t instanceof ConstraintViolationException) {
        return true;
      }
    }
    return false;
  }

  private Tag getTag(Team team, String tagId) {
    if (!TSID.isValid(tagId)) {
      throw new NotFoundException(EntityType.TAG, tagId);
    }
    Long id = TsidUtils.toLong(tagId);
    return tagRepository
        .findByTeamAndId(team.getId(), id)
        .orElseThrow(() -> new NotFoundException(EntityType.TAG, id));
  }

  /**
   * Trimmed, non-empty, at most 32 characters once trimmed (plan D16) — or a 400 {@link
   * ErrorCode#TAG_LABEL_INVALID}. Characters are code points, as the {@code varchar(32)} counts
   * them.
   */
  public static String normalizeLabel(String label) {
    String trimmed = label.strip();
    if (trimmed.isEmpty() || trimmed.codePointCount(0, trimmed.length()) > Tag.MAX_LABEL_LENGTH) {
      throw new BadRequestException(ErrorCode.TAG_LABEL_INVALID);
    }
    return trimmed;
  }

  // ─── Tagging a content ────────────────────────────────────────────────────

  /**
   * The tags a content of {@code team} and of kind {@code type} may carry, from the ids of a create
   * or update request — or a 400. Duplicates count once; more than 10 is {@link
   * ErrorCode#TOO_MANY_TAGS}; an id malformed, unknown, of another team or of another kind is
   * {@link ErrorCode#TAG_INVALID}. One query.
   */
  public List<Tag> resolveForContent(Team team, TagTarget type, Collection<String> tagIds) {
    Set<Long> ids = new LinkedHashSet<>();
    for (String tagId : tagIds) {
      if (!TSID.isValid(tagId)) {
        throw new BadRequestException(ErrorCode.TAG_INVALID);
      }
      ids.add(TsidUtils.toLong(tagId));
    }
    if (ids.size() > MAX_TAGS_PER_CONTENT) {
      throw new BadRequestException(ErrorCode.TOO_MANY_TAGS);
    }
    List<Tag> tags = tagRepository.findByTeamTypeAndIds(team.getId(), type, ids);
    if (tags.size() != ids.size()) {
      throw new BadRequestException(ErrorCode.TAG_INVALID);
    }
    return tags;
  }

  /**
   * Sets the tags of a ride, post, trip, route or ad to exactly {@code tagIds}, validated by {@link
   * #resolveForContent} against the content's own team and kind.
   *
   * @param tagIds {@code null} leaves the tags as they are (a client that does not send the field
   *     — an older release — must not wipe them); an empty list removes them all
   */
  @Transactional
  public void replaceTags(TeamEntity content, @Nullable Collection<String> tagIds) {
    if (tagIds == null) {
      return;
    }
    setLinks(content, resolveForContent(content.getTeam(), TagTarget.of(content), tagIds));
  }

  /** Links the content to exactly {@code wanted}: removes the other links, adds the missing ones. */
  private void setLinks(TeamEntity content, Collection<Tag> wanted) {
    Set<Long> wantedIds = new HashSet<>();
    wanted.forEach(tag -> wantedIds.add(tag.getId()));
    Set<Long> kept = new HashSet<>();
    for (TeamEntityTag link : teamEntityTagRepository.findByEntityId(content.getId())) {
      if (wantedIds.contains(link.getTag().getId())) {
        kept.add(link.getTag().getId());
      } else {
        teamEntityTagRepository.delete(link);
      }
    }
    for (Tag tag : wanted) {
      if (!kept.contains(tag.getId())) {
        teamEntityTagRepository.persist(new TeamEntityTag(content, tag));
      }
    }
  }

  /** Same as {@link #replaceTags}, for a ride template and its {@code RIDE} tags (plan D14). */
  @Transactional
  public void replaceTemplateTags(RideTemplate template, @Nullable Collection<String> tagIds) {
    if (tagIds == null) {
      return;
    }
    List<Tag> wanted = resolveForContent(template.getTeam(), TagTarget.RIDE, tagIds);
    Set<Long> wantedIds = new HashSet<>();
    wanted.forEach(tag -> wantedIds.add(tag.getId()));
    Set<Long> kept = new HashSet<>();
    for (RideTemplateTag link : rideTemplateTagRepository.findByTemplateId(template.getId())) {
      if (wantedIds.contains(link.getTag().getId())) {
        kept.add(link.getTag().getId());
      } else {
        rideTemplateTagRepository.delete(link);
      }
    }
    for (Tag tag : wanted) {
      if (!kept.contains(tag.getId())) {
        rideTemplateTagRepository.persist(new RideTemplateTag(template, tag));
      }
    }
  }

  // ─── Importing free-form labels ───────────────────────────────────────────

  /**
   * Sets the tags of a content to {@code labels}, free-form strings from another system — the
   * biketeam migration's route tags (plan §7, D21) — finding each in the team's vocabulary of the
   * content's kind case-insensitively, or creating it in {@link TagColor#GRAY} with the first
   * spelling met. Replaces the content's tags entirely, so a replay is idempotent: no tag nor link
   * is doubled.
   *
   * <p>Never fails on the data: a blank label is dropped, one longer than 32 characters is cut, and
   * beyond 10 tags on the content or 100 in the team's vocabulary (plan D16) the first ones, in
   * {@code labels} order, are kept and the rest dropped. What was cut or dropped comes back, for the
   * caller to report.
   *
   * @param creator the author of the tags this creates
   * @return one line per label cut or dropped, empty when every label made it as is
   */
  @Transactional
  public List<String> importTags(TeamEntity content, User creator, List<String> labels) {
    Team team = content.getTeam();
    TagTarget type = TagTarget.of(content);
    List<String> notes = new ArrayList<>();
    Map<String, Tag> vocabulary = new HashMap<>();
    for (Tag tag : tagRepository.findByTeam(team.getId(), type)) {
      vocabulary.putIfAbsent(tag.getLabel().toLowerCase(Locale.ROOT), tag);
    }
    Map<Long, Tag> wanted = new LinkedHashMap<>();
    for (String raw : labels) {
      String label = raw.strip();
      if (label.isEmpty()) {
        continue;
      }
      if (label.codePointCount(0, label.length()) > Tag.MAX_LABEL_LENGTH) {
        // Cut on code points: a cut through a surrogate pair would store a lone half.
        String cut = label.substring(0, label.offsetByCodePoints(0, Tag.MAX_LABEL_LENGTH)).strip();
        notes.add("Tag '" + label + "' cut to '" + cut + "'");
        label = cut;
      }
      String key = label.toLowerCase(Locale.ROOT);
      Tag tag = vocabulary.get(key);
      if (tag != null && wanted.containsKey(tag.getId())) {
        continue;
      }
      if (wanted.size() >= MAX_TAGS_PER_CONTENT) {
        notes.add("Tag '" + label + "' dropped: more than " + MAX_TAGS_PER_CONTENT + " tags");
        continue;
      }
      if (tag == null) {
        if (vocabulary.size() >= MAX_TAGS_PER_TEAM_AND_TYPE) {
          notes.add(
              "Tag '"
                  + label
                  + "' dropped: the team already has "
                  + MAX_TAGS_PER_TEAM_AND_TYPE
                  + " "
                  + type
                  + " tags");
          continue;
        }
        tag = new Tag(creator, team, type, label, TagColor.GRAY);
        tagRepository.persist(tag);
        vocabulary.put(key, tag);
      }
      wanted.put(tag.getId(), tag);
    }
    setLinks(content, wanted.values());
    return notes;
  }

  // ─── Filtering a list ─────────────────────────────────────────────────────

  /**
   * The ids of a list's {@code ?tags=} filter that name a tag of this team and kind (plan D18). An
   * id malformed, unknown — deleted since the link was shared — of another team or of another kind
   * is ignored, not an error. An empty result means no filter at all, never « match nothing »:
   * pass it to {@code TagFilter.andTaggedWithAny}, which then adds nothing.
   *
   * @param rawTagIds the parameter as received: each value may itself hold comma-separated ids
   *     ({@code ?tags=a,b} as well as {@code ?tags=a&tags=b}); {@code null} for none
   */
  public Set<Long> resolveFilter(
      Team team, TagTarget type, @Nullable Collection<String> rawTagIds) {
    if (rawTagIds == null || rawTagIds.isEmpty()) {
      return Set.of();
    }
    Set<Long> ids = new HashSet<>();
    for (String raw : rawTagIds) {
      for (String part : raw.split(",")) {
        String tagId = part.strip();
        if (TSID.isValid(tagId)) {
          ids.add(TsidUtils.toLong(tagId));
        }
      }
    }
    if (ids.isEmpty()) {
      return Set.of();
    }
    // A shared link can carry anything; a bounded IN list keeps a forged one cheap. More ids than a
    // team can hold tags of this kind cannot all be real.
    if (ids.size() > MAX_TAGS_PER_TEAM_AND_TYPE) {
      ids = new HashSet<>(ids.stream().limit(MAX_TAGS_PER_TEAM_AND_TYPE).toList());
    }
    return tagRepository.findByTeamTypeAndIds(team.getId(), type, ids).stream()
        .map(Tag::getId)
        .collect(Collectors.toUnmodifiableSet());
  }
}
