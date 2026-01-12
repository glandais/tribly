package com.tribly.repository.calendar;

import com.tribly.domain.calendar.CalendarToken;
import com.tribly.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CalendarTokenRepository implements BaseRepository<CalendarToken> {

  public Optional<CalendarToken> findByToken(String token) {
    return find("token = ?1 and deleted = false", token).firstResultOptional();
  }

  public Optional<CalendarToken> findByUserId(Long userId) {
    return find("user.id = ?1 and deleted = false", userId).firstResultOptional();
  }
}
