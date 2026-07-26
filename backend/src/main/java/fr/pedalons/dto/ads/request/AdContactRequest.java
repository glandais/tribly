package fr.pedalons.dto.ads.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A message relayed to the author of an ad.
 *
 * <p>There is no recipient field, and that is the design: the server knows who wrote the ad, so a
 * client never learns the author's address and never gets to choose one. The lower bound on the
 * message is not decoration — a relay that forwards "?" is a notification channel with a text box,
 * and the recipient cannot tell it from noise.
 */
@Schema(description = "A message to relay to the author of an ad")
@ValidateSchema
@Builder
public record AdContactRequest(
    @NotBlank
        @Size(min = 10, max = 2000)
        @Schema(
            description =
                "The message body, plain text. Rendered as text in the email: any markup is"
                    + " escaped rather than interpreted.",
            examples = "Bonjour, le vélo est-il toujours disponible ? Je peux passer samedi.",
            required = true)
        String message) {}
