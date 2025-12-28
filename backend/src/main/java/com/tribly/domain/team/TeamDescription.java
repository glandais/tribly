package com.tribly.domain.team;

import com.tribly.domain.common.TeamEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("4")
public class TeamDescription extends TeamEntity {}
