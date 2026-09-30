package fr.pedalons.dto.auth.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.EmailLinkKind;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "What a verification link is about, read without spending it: the page shows the address"
            + " before anything happens, so that nobody activates someone else's account unaware.")
@Builder
@ValidateSchema
public record EmailLinkPreviewResponse(
    @Schema(description = "The address the link verifies", required = true) String email,
    @Schema(description = "What following the link does", required = true) EmailLinkKind kind) {}
