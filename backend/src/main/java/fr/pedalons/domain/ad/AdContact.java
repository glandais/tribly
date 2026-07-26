package fr.pedalons.domain.ad;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The record that one member wrote to the author of an ad.
 *
 * <p>Deliberately not a message: the body is never stored. Relaying a message means the server
 * holds it exactly as long as it takes to hand it to the mail provider, and the only durable copy
 * is the one in the recipient's inbox. Storing bodies here would recreate, without any of the
 * affordances, the internal-messaging feature this relay exists to avoid — plus a corpus of private
 * correspondence to export, redact and delete.
 *
 * <p>What remains is what two jobs need. Rate limiting reads "how many did this sender send since
 * T". An abuse report reads "who has been writing to this ad". Both are answered by the sender, the
 * ad and {@code createdAt} inherited from {@link BaseEntity}.
 */
@Setter
@Getter
@Entity
@Table(name = "ad_contacts")
@NoArgsConstructor
public class AdContact extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ad_id", nullable = false)
  private Ad ad;

  public AdContact(User sender, Ad ad) {
    super(sender);
    this.ad = ad;
  }
}
