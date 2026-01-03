package com.tribly.dto.pages.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to reorder team pages")
@ValidateSchema
public record ReorderPagesRequest(
    @Schema(description = "Ordered list of page IDs", required = true) @NotNull
        List<String> pageIds) {}
