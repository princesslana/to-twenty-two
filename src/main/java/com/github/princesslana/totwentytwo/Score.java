package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableScore.class)
public interface Score {
  User getUser();

  long getScore();
}
