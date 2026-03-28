package fr.pedalons.repository.calendar;

import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CalendarTokenRepository implements BaseRepository<CalendarToken> {

  public Optional<CalendarToken> findByToken(String token) {
    return find("token = ?1", token).firstResultOptional();
  }

  public Optional<CalendarToken> findByUserId(Long userId) {
    return find("user.id = ?1", userId).firstResultOptional();
  }
}
