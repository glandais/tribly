package com.tribly.domain.common.repository;

import java.util.List;

public record TriblyPage<T>(List<T> items, long total) {}
