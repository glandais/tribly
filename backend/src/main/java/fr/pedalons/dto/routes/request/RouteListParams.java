package fr.pedalons.dto.routes.request;

import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.RouteSortBy;
import fr.pedalons.enums.SortDirection;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jspecify.annotations.Nullable;

/**
 * The route filters plus what only a paginated listing needs: a page, a size and a sort.
 *
 * <p>The tile, the bounding box and the count fold every match into one answer, so they take {@link
 * RouteFilterParams} instead — an {@code ORDER BY} would break their aggregate projection and a page
 * number would mean nothing.
 */
public class RouteListParams extends RouteFilterParams {

  @Parameter(description = "Page number (0-indexed)")
  @QueryParam("page")
  @DefaultValue("0")
  public int page;

  @Parameter(description = "Page size")
  @QueryParam("size")
  @DefaultValue("20")
  public int size;

  @Parameter(description = "Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)")
  @QueryParam("sortBy")
  public @Nullable RouteSortBy sortBy;

  @Parameter(description = "Sort direction (ASC, DESC)")
  @QueryParam("sortDir")
  public @Nullable SortDirection sortDir;

  @Parameter(
      description =
          "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty"
              + " and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which"
              + " are present either way. Omitted, or FULL, is the previous behaviour, byte for"
              + " byte.")
  @QueryParam("view")
  public @Nullable ListViewMode view;

  @Override
  public RouteSearchParams.RouteSearchParamsBuilder toBuilder() {
    return super.toBuilder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).view(view);
  }
}
