package com.github.princesslana.totwentytwo;

import org.immutables.value.Value;

@Value.Immutable
public interface Score {
  User getUser();
  long getScore();
}
