package com.tribly.domain.calendar;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "calendar_tokens")
@NoArgsConstructor
public class CalendarToken extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "token", nullable = false, unique = true, length = 64)
  private String token;

  public CalendarToken(User user, String token) {
    super(user);
    this.user = user;
    this.token = token;
  }
}
