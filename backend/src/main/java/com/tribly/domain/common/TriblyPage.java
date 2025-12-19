package com.tribly.domain.common;

import java.util.List;

public record TriblyPage<T>(List<T> items, long total) {}
