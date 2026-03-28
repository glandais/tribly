package fr.pedalons.repository.query;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public interface Clause {

  String clause();

  Map<String, @Nullable Object> params();
}
