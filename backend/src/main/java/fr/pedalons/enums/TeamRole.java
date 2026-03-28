package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TeamRole {
  MEMBER(true, false, false),
  ORGANIZER(true, true, false),
  ADMIN(true, true, true);

  final boolean member;
  final boolean organizer;
  final boolean admin;
}
