package fr.pedalons.dto.feedback.request;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.FeedbackKind;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "A bug report or a suggestion written by a member")
@ValidateSchema
public record FeedbackRequest(
    @Schema(description = "Bug or suggestion", required = true) FeedbackKind kind,
    @Nullable
        @Schema(
            description =
                "What happened, in the member's words. Required for a suggestion; optional for a"
                    + " bug, whose member may not know what went wrong — the context, error and log"
                    + " then speak for them.")
        @Size(min = 10, max = 5000)
        String message,
    @Schema(description = "Client, device and screen", required = true) @Valid
        ClientContextDto context,
    @Nullable
        @Schema(
            description =
                "The unhandled error the report was opened from, if any. Links the report to the"
                    + " automatic error report of the same error.")
        @Valid
        ClientErrorDto error,
    @Nullable
        @Schema(
            description =
                "The client's recent log, oldest first. Absent when the member chose not to attach"
                    + " technical details.")
        @Size(max = 200)
        List<@Valid ClientLogEntryDto> logs) {}
