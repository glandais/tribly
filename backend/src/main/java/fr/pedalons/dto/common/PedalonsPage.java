package fr.pedalons.dto.common;

import java.util.List;

public record PedalonsPage<T>(List<T> items, long total) {}
