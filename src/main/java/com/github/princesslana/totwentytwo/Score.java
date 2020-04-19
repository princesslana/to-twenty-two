package com.github.princesslana.totwentytwo;

import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableScore.class)
public interface Score {
  User getUser();
  long getScore();
}
