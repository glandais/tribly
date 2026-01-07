package com.tribly.repository.query;

import java.util.Map;
import org.jspecify.annotations.Nullable;

public record SimpleClause(String clause, Map<String, @Nullable Object> params) implements Clause {}
